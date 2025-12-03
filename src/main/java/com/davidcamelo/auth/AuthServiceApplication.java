package com.davidcamelo.auth;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan
@OpenAPIDefinition(
        servers = { @Server(url = "/auth", description = "Auth Service URL"), @Server(url = "/", description = "Default Server") },
        info = @Info(title = "OpenAPI definition", version = "v0"))
public class AuthServiceApplication {

    public static void main(String[] args) {
        log.info("Current java.home {}", System.getProperty("java.home"));
        log.info("Current java.vendor {}", System.getProperty("java.vendor"));
        log.info("Current java.vendor.url {}", System.getProperty("java.vendor.url"));
        log.info("Current java.version {}", System.getProperty("java.version"));

        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
