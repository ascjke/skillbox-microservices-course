package ru.borisov.users.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${server.port}")
    private int port;
    @Value("${server_ip}")
    private String ip;


    @Bean
    public OpenAPI customOpenAPI() {

        final String securitySchemeName = "bearerAuth";
        final String url = "http://" + ip + ":" + port;

        return new OpenAPI()
                .info(new Info().title("Users service API")
                        .version("0.0.1-SNAPSHOT")
                        .description("Documentation of Users service API")
                        .license(new License().name("Apache 2.0")
                                .url("http://springdoc.org"))
                        .contact(new Contact().name("RCIT")
                                .url("https://www.rcitsakha.ru")
                                .email("borisov.za@rcitsakha.ru")))
                .servers(List.of(
                        new Server()
                                .url(url)
                                .description("Server address"),
                        new Server()
                                .url("http://10.50.50.99:8080")
                                .description("Address for frontend developer")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                );
    }
}
