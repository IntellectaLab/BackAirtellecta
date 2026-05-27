package com.airtellecta.config;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import jakarta.ws.rs.core.Application;

@OpenAPIDefinition(
    info = @Info(
        title = "AirTellecta API",
        version = "1.0.0",
        description = "API de análisis epidemiológico y simulación de políticas de tabaco en México. " +
                      "Todos los endpoints protegidos requieren un token JWT emitido por Firebase Authentication.",
        contact = @Contact(name = "IntellectaLab", email = "soporte@airtellecta.com")
    ),
    servers = {
        @Server(url = "/", description = "Servidor actual")
    }
)
@SecurityScheme(
    securitySchemeName = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Token JWT emitido por Firebase Authentication. Incluir en el header: Authorization: Bearer <token>"
)
public class OpenApiConfig extends Application {
}
