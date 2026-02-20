package com.cooperativa.assembleia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI assembleiaOpenAPI() {
        return new OpenAPI()
            .addServersItem(new Server().url("https://api-teste.wincktech.com.br").description("Produção Traefik"))
            .addServersItem(new Server().url("/").description("Rotas Relativas"))
            .info(new Info().title("Assembleia Voting API")
            .description("API para gerenciamento de sessões de votação de pautas")
            .version("1.0"));
    }
}
