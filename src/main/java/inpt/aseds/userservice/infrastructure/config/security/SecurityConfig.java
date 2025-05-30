package inpt.aseds.userservice.infrastructure.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain webOAuth2FilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/users/**").hasRole("USER")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new HashSet<>();

            // Extract realm roles
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                List<String> roles = (List<String>) realmAccess.get("roles");
                roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
            }

            // Extract client roles for your specific client
            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
            if (resourceAccess != null) {
                // Check for streaming-app client roles
                if (resourceAccess.containsKey("streaming-app")) {
                    Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get("streaming-app");
                    if (clientAccess.containsKey("roles")) {
                        List<String> clientRoles = (List<String>) clientAccess.get("roles");
                        clientRoles.forEach(role ->
                                authorities.add(new SimpleGrantedAuthority("ROLE_APP_" + role)));
                    }
                }

//                // Check for streaming-admin client roles if needed
//                if (resourceAccess.containsKey("streaming-admin")) {
//                    Map<String, Object> adminAccess = (Map<String, Object>) resourceAccess.get("streaming-admin");
//                    if (adminAccess.containsKey("roles")) {
//                        List<String> adminRoles = (List<String>) adminAccess.get("roles");
//                        adminRoles.forEach(role ->
//                                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN_" + role)));
//                    }
//                }
            }

            return authorities;
        });
        return converter;
    }
};
