package com.trycore.evmTracker.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "EVM Tracker API",
                version = "v1",
                description = "API para gestionar proyectos y actividades en EVM Tracker",
                contact = @Contact(name = "Trycore", email = "info@trycore.com"),
                license = @License(name = "MIT")
        ),
        servers = @Server(url = "/")
)
@Configuration
public class OpenApiConfig {
}
