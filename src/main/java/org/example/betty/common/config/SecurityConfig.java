package org.example.betty.common.config;

import org.example.betty.common.jwt.JwtSessionFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtSessionFilter sessionFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(customJwtConverter()))
                )
                .addFilterAfter(sessionFilter, BearerTokenAuthenticationFilter.class);

        return http.build();
    }

    private Converter<Jwt, JwtAuthenticationToken> customJwtConverter() {
        return jwt -> {
            List<Map<String, Object>> wallets = jwt.getClaim("wallets");
            String address = wallets.get(0).get("address").toString();

            return new JwtAuthenticationToken(
                    jwt,
                    List.of(new SimpleGrantedAuthority("ROLE_USER")),
                    address
            );
        };
    }
}
