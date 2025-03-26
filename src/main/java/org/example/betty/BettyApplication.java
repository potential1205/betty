package org.example.betty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;

@SpringBootApplication
public class BettyApplication {

    public static void main(String[] args) {
        SpringApplication.run(BettyApplication.class, args);
    }

}
