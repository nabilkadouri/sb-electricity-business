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

                        .requestMatchers(HttpMethod.POST, "/api/auth/password/forgot").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/password/reset").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/charging_stations/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/charging_stations").authenticated()

                        .requestMatchers(HttpMethod.PATCH, "/api/account/*/password").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/account/*/uploadProfilePicture").authenticated()

                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/icons/**").permitAll()


                        .requestMatchers("/api/bookings/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/bookings/*/status").authenticated()

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

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
        // Permettre l'origine  du frontend
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "https://electricity-business.nk-dev.fr"));
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
