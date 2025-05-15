package inpt.aseds.userservice.config;

import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain webOAuth2FilterChain(final HttpSecurity http) throws  Exception{
        return http
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/api/public/**").permitAll()
                                .requestMatchers("/api/users/**").hasRole("USER")
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                .anyRequest()
                                .authenticated()
                )
                .oauth2Login(withDefaults())
                        .build();
    }
    @Bean
    GrantedAuthoritiesMapper keycloakGrantedAuthoritiesMapper() {
           return authorities -> authorities.stream()
            .filter(authority -> authority instanceof OidcUserAuthority)
            .map(authority -> (OidcUserAuthority) authority)
            .map(oidcUserAuthority -> (Map<String, Object>) oidcUserAuthority.getIdToken().getClaims().get("realm_access"))
            .map(realmAccess -> ((List<String>) realmAccess.get("roles"))
                    .stream().map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet()))
            .flatMap(Collection::stream)
            .collect(Collectors.toCollection(() -> new HashSet<GrantedAuthority>()));
    }
}