package org.fixmed.fixmed.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/users/**").hasAnyAuthority("ADMIN", "DOCTOR", "PATIENT")
                        .requestMatchers("/doctors/**").authenticated()
                        .requestMatchers("/patients/**").permitAll()
                        .requestMatchers("/facilities/**").hasAnyAuthority("ADMIN", "RECEPTIONIST", "PATIENT")
                        .requestMatchers("/services/**").hasAnyAuthority("ADMIN", "DOCTOR")
                        .requestMatchers("/assignments/**").hasAnyAuthority("ADMIN", "DOCTOR", "PATIENT")
                        .requestMatchers("/slots/**").hasAnyAuthority("DOCTOR", "ADMIN", "PATIENT")
                        .requestMatchers("/service-prices/**").hasAnyAuthority("DOCTOR", "ADMIN", "PATIENT")
                        .requestMatchers("/appointments/**").hasAnyAuthority("PATIENT", "DOCTOR", "ADMIN")
                        .requestMatchers("/blocked-days/**").hasAnyAuthority("PATIENT", "DOCTOR", "ADMIN")
                        .requestMatchers("/review/**").hasAnyAuthority("PATIENT", "DOCTOR", "ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*"); // <-- tu ważne: ORIGIN PATTERN, nie setAllowedOrigins
        config.addAllowedMethod("*");         // <-- wszystkie metody: GET, POST, PUT, DELETE, itd.
        config.addAllowedHeader("*");          // <-- wszystkie nagłówki
        config.setExposedHeaders(List.of("Authorization")); // jeśli chcesz Authorization widoczne dla klienta
        config.setAllowCredentials(true);      // <- jeżeli korzystasz z cookies / Authorization header

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
