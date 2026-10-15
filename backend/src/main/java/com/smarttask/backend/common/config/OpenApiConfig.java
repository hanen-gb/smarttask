package com.smarttask.backend.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI smartTaskOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("SmartTask API")
                .description("API REST de la plateforme de gestion de projets et taches SmartTask")
                .version("v0.1"));
    }
}
