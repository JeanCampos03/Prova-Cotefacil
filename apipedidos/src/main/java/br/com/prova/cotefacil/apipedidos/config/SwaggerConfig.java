package br.com.prova.cotefacil.apipedidos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    private static final String BEARER_JWT = "bearer-jwt";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:8082").description("API 2 - CRUD Pedidos (esta aplicação)"),
                        new Server().url("http://localhost:8080").description("API 1 - Autenticação")
                ))
                .info(new Info()
                        .title("API 2 - Pedidos")
                        .version("1.0")
                        .description("API de pedidos (CoteFácil 2026). Login e registro ficam na API 1 (gateway).\n\n" +
                                "**Como usar:** Obtenha o token na API 1 (login/register), depois clique em **Authorize** e teste os endpoints abaixo."))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_JWT))
                .components(new Components()
                        .addSecuritySchemes(BEARER_JWT,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Token do /auth/login ou /auth/register")));
    }
}
