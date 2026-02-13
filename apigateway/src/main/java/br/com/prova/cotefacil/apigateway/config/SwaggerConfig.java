package br.com.prova.cotefacil.apigateway.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
                        new Server().url("http://localhost:8080").description("API 1 - Autenticação (esta aplicação)"),
                        new Server().url("http://localhost:8082").description("API 2 - CRUD Pedidos"))


                )
                .info(new Info()
                        .title("API 1 - Gateway / Autenticação")
                        .version("1.0")
                        .description("Gateway e autenticação (CoteFácil). Login e registro aqui; pedidos são encaminhados para a API 2.\n\n" +
                                "**Como usar:** Registre ou faça login, copie o token e clique em **Authorize**."))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_JWT))
                .components(new Components()
                        .addSecuritySchemes(BEARER_JWT,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Token obtido em /auth/login ou /auth/register")));
    }
}
