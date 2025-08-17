package org.viniciusvirgilli.config;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.ws.rs.core.Application;

@OpenAPIDefinition(
    info = @Info(
        title = "Leitura API",
        version = "1.0.0",
        description = "API para gerenciamento de leituras e progresso de livros",
        contact = @Contact(
            name = "Vinicius Virgilli",
            email = "vinicius.virgilli3@gmail.com"
        ),
        license = @License(
            name = "MIT",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Servidor de desenvolvimento")
    },
    tags = {
        @Tag(name = "Livros", description = "Operações relacionadas ao gerenciamento de livros"),
        @Tag(name = "Métricas", description = "Operações relacionadas às métricas de leitura"),
        @Tag(name = "Usuários", description = "Operações relacionadas ao gerenciamento de usuários")
    }
)
@SecurityScheme(
    securitySchemeName = "jwt",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Autenticação JWT. Use o endpoint /login para obter o token."
)
public class OpenApiConfig extends Application {
    // Esta classe serve apenas para configurar o OpenAPI
    // Não é necessário implementar nenhum método
}