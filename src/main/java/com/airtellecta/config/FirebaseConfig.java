package com.airtellecta.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Optional;

@ApplicationScoped
public class FirebaseConfig {

    private static final Logger LOG = Logger.getLogger(FirebaseConfig.class);

    @ConfigProperty(name = "firebase.credentials.path")
    Optional<String> credentialsPath;

    @ConfigProperty(name = "firebase.credentials.base64", defaultValue = "skip")
    String credentialsBase64;

    void onStart(@Observes StartupEvent ev) {
        // Initialization is handled lazily in produceFirebaseAuth to avoid
        // a timing race where the CDI producer runs before StartupEvent fires.
        LOG.info("FirebaseConfig startup observer called");
    }

    private synchronized void initializeIfNeeded() {
        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }
        if (credentialsPath.isEmpty() && "skip".equals(credentialsBase64)) {
            LOG.warn("Firebase initialization skipped (test mode)");
            return;
        }
        try {
            InputStream credentialsStream = resolveCredentials();
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();
            FirebaseApp.initializeApp(options);
            LOG.info("Firebase Admin SDK initialized successfully");
        } catch (IOException | IllegalArgumentException e) {
            throw new RuntimeException("Failed to initialize Firebase Admin SDK", e);
        }
    }

    private InputStream resolveCredentials() throws IOException {
        if (credentialsPath.isPresent()) {
            LOG.info("Loading Firebase credentials from file: " + credentialsPath.get());
            return new FileInputStream(credentialsPath.get());
        }
        LOG.info("Loading Firebase credentials from base64 env variable");
        byte[] decoded = Base64.getDecoder().decode(credentialsBase64);
        return new ByteArrayInputStream(decoded);
    }

    @Produces
    @Singleton
    FirebaseAuth produceFirebaseAuth() {
        initializeIfNeeded();
        if (FirebaseApp.getApps().isEmpty()) {
            throw new IllegalStateException(
                "Firebase is not initialized. " +
                "For local dev: set firebase.credentials.path to your service account JSON file. " +
                "For production: set the FIREBASE_CREDENTIALS_BASE64 environment variable.");
        }
        return FirebaseAuth.getInstance();
    }
}
