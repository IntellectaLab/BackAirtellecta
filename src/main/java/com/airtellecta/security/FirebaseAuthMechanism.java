package com.airtellecta.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.AuthenticationRequest;
import io.quarkus.security.runtime.QuarkusPrincipal;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class FirebaseAuthMechanism implements HttpAuthenticationMechanism {

    @Inject
    FirebaseAuth firebaseAuth;

    @Override
    public Uni<SecurityIdentity> authenticate(RoutingContext context, IdentityProviderManager identityProviderManager) {
        String authHeader = context.request().getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Uni.createFrom().optional(Optional.empty());
        }
        String token = authHeader.substring(7);
        return Uni.createFrom().item(() -> buildIdentity(token))
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    private SecurityIdentity buildIdentity(String token) {
        try {
            FirebaseToken decoded = firebaseAuth.verifyIdToken(token);
            Object roleClaim = decoded.getClaims().getOrDefault("role", "USER");
            String role = roleClaim instanceof String ? (String) roleClaim : "USER";
            return QuarkusSecurityIdentity.builder()
                    .setPrincipal(new QuarkusPrincipal(decoded.getUid()))
                    .addRole(role.toUpperCase())
                    .build();
        } catch (FirebaseAuthException e) {
            throw new AuthenticationFailedException("Invalid or expired token", e);
        }
    }

    @Override
    public Uni<ChallengeData> getChallenge(RoutingContext context) {
        return Uni.createFrom().item(new ChallengeData(401, "WWW-Authenticate", "Bearer"));
    }

    @Override
    public Uni<Boolean> sendChallenge(RoutingContext context) {
        context.response()
                .setStatusCode(401)
                .putHeader("Content-Type", MediaType.APPLICATION_JSON)
                .end("{\"success\":false,\"error\":\"Invalid or expired token\"}");
        return Uni.createFrom().item(true);
    }

    @Override
    public Set<Class<? extends AuthenticationRequest>> getCredentialTypes() {
        return Collections.emptySet();
    }
}
