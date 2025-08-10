package com.hb.cda.electricitybusiness.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private UserDetailsService userDetailsService;
    private JwtUtil jwtUtil;

    public JwtRequestFilter(UserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }



    //Méthode qui est exécutée une fois par requête afin d'extraire et valider le JWT
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        //tente d'estraire l'en-tête "Authorization"
        String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Vérifie si l'en-tête Authorization existe et commence par "Bearer "
        // Ensuite extrait le jwt et le username
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }

        // Si un nom d'utilisateur a été extrait et qu'il n'y a pas déjà d'authentification dans le contexte de sécurité
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Charge les détails de l'utilisateur à partir du nom d'utilisateur (email)
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Valide le token JWT par rapport aux détails de l'utilisateur
            if (jwtUtil.validateToken(jwt, userDetails)) {
                // Si le token est valide, crée un objet d'authentification
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Définit l'objet d'authentification dans le contexte de sécurité de Spring
                // Cela indique à Spring Security que l'utilisateur est authentifié pour cette requête
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        // Passe la requête au filtre suivant dans la chaîne
        chain.doFilter(request, response);
    }
}
