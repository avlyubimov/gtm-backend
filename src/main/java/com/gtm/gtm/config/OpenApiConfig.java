package com.gtm.gtm.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gtmOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GTM API")
                        .description("Геотехнический мониторинг")
                        .version("v1")
                        .contact(new Contact().name("Team").email("support@example.com")))
                .addServersItem(new Server().url("http://localhost:8080"));
    }
}
