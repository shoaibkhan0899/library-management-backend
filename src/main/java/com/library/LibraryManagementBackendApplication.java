package com.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        servers = {
                @Server(
                        url = "https://library-management-backend-production-40f3ad13.up.railway.app"
                )
        }
)

@SpringBootApplication
public class LibraryManagementBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementBackendApplication.class, args);
    }

}
