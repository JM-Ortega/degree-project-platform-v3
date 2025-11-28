package co.edu.unicauca.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)


                .authorizeExchange(ex -> ex
                        .anyExchange().permitAll()
                )


                .build();
    }
}


/*
package co.edu.unicauca.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.core.convert.converter.Converter;
import reactor.core.publisher.Mono;

import java.util.*;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    // Filtro principal del Gateway
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(ex -> ex
                // Endpoints públicos (sin token)
                .pathMatchers("/api/auth/**", "/actuator/health").permitAll()

                // Academic: docente o estudiante
                .pathMatchers("/api/academic/**")
                    .hasAnyRole("DOCENTE", "ESTUDIANTE")

                // Coordinator: solo COORDINADOR
                .pathMatchers("/api/coordinator/**")
                    .hasRole("COORDINADOR")

                // Department head: solo JEFE_DE_DEPARTAMENTO
                .pathMatchers("/api/department/**")
                    .hasRole("JEFE_DE_DEPARTAMENTO")

                // Cualquier otra ruta: autenticado
                .anyExchange().authenticated()
            )
            // Valida JWT de Keycloak y mapea roles
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            )
            .build();
    }

    // Convierte el Jwt de Keycloak en Authentication con authorities
    @Bean
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        return jwt -> {
            List<GrantedAuthority> authorities = new ArrayList<>();

            // 1) Roles de realm (opcional, por si quieres usarlos)
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null) {
                Object roles = realmAccess.get("roles");
                if (roles instanceof Collection<?> realmRoles) {
                    realmRoles.forEach(r -> {
                        String roleName = String.valueOf(r);
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
                    });
                }
            }

            // 2) Roles del cliente sistema-desktop (DOCENTE, ESTUDIANTE, etc.)
            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
            if (resourceAccess != null) {
                Object clientObj = resourceAccess.get("sistema-desktop");
                if (clientObj instanceof Map<?, ?> clientMap) {
                    Object roles = clientMap.get("roles");
                    if (roles instanceof Collection<?> clientRoles) {
                        clientRoles.forEach(r -> {
                            String roleName = String.valueOf(r); // EJ: ESTUDIANTE
                            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
                        });
                    }
                }
            }

            // Crea el Authentication final
            AbstractAuthenticationToken auth =
                new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());

            return Mono.just(auth);
        };
    }
}
*/