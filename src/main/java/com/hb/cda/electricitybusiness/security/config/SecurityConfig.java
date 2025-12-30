package com.hb.cda.electricitybusiness.security.config;


import com.hb.cda.electricitybusiness.security.jwt.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private AuthenticationConfiguration authenticationConfiguration;
    private JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JwtRequestFilter jwtRequestFilter) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain accessControl(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/account/register").permitAll()

                        // Réinitialisation via email (public)
                        .requestMatchers(HttpMethod.POST, "/api/account/password/**").permitAll()

                        // Modifier son mot de passe (privé)
                        .requestMatchers(HttpMethod.PATCH, "/api/account/*/password").authenticated()

                        .requestMatchers("/api/charging_stations/upload-temp-picture").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Ressources statiques
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/icons/**").permitAll()

                        // autres routes
                        .requestMatchers("/api/bookings/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/bookings/*/status").authenticated()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permettre l'origine de du frontend
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        // Permettre les méthodes HTTP standard
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        // Permettre tous les en-têtes (crucial pour l'en-tête Authorization du JWT)
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // Autoriser l'envoi de credentials (cookies, en-têtes d'authentification)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Appliquer cette configuration CORS à toutes les routes (/**)
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
