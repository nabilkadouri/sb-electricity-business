package com.hb.cda.electricitybusiness.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // La clé secrète pour signer les tokens, lue depuis les propriétés de l'application
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    // Durée de validité du token d'accès en millisecondes
    @Value("${jwt.expiration}")
    private long EXPIRATION_TIME;

    // Durée de validité du refresh token en millisecondes
    @Value("${jwt.refresh.expiration}")
    private long REFRESH_EXPIRATION_TIME;

    /**
     * Retourne la durée de validité du token d'accès en millisecondes.
     * @return La durée d'expiration du token d'accès.
     */
    public long getExpirationTime() {
        return EXPIRATION_TIME;
    }

    /**
     * Retourne la durée de validité du refresh token en millisecondes.
     * @return La durée d'expiration du refresh token.
     */
    public long getRefreshExpirationTime() {
        return REFRESH_EXPIRATION_TIME;
    }

    /**
     * Utilise la methode générique extractClaim pour récupérer le sujet du token qui est l'email de l'utilisateur.
     * @param token Le token JWT.
     * @return Le nom d'utilisateur (email) extrait.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait la date d'expiration du token JWT.
     * @param token Le token JWT.
     * @return La date d'expiration.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    /**
     * Méthode générique qui utilise une fonction lambda (claimsResolver) pour extraire n'importe quelle information (claim) du corps du token afin d'éviter la duplication de code.
     * @param token Le token JWT.
     * @param claimsResolver Une fonction pour résoudre la revendication.
     * @param <T> Le type de la revendication.
     * @return La revendication extraite.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    /**
     * Méthode de base qui analyse le token JWT. Elle décode le token, le valide avec la clé secrète (getSigningKey()) et retourne l'ensemble des revendications (claims) qu'il contient.
     * @param token Le token JWT.
     * @return Toutes les revendications.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Vérifie si le token JWT est expiré.
     * @param token Le token JWT.
     * @return true si le token est expiré, false sinon.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Génère un token JWT d'accès pour un utilisateur donné.
     * @param userDetails Les détails de l'utilisateur (implémentation de UserDetails).
     * @return Le token JWT d'accès généré.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(), EXPIRATION_TIME);
    }

    /**
     * Génère un refresh token JWT pour un utilisateur donné.
     * Le refresh token a généralement une durée de vie plus longue.
     * @param userDetails Les détails de l'utilisateur (implémentation de UserDetails).
     * @return Le refresh token JWT généré.
     */
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(), REFRESH_EXPIRATION_TIME);
    }

    /**
     * Crée le token JWT avec les revendications, le sujet et la durée d'expiration spécifiée.
     * Cette méthode est réutilisée pour les tokens d'accès et les refresh tokens.
     * @param claims Les données de revendication.
     * @param subject Le sujet du token, ici l'email de l'utilisateur.
     * @param expirationTime La durée de validité du token.
     * @return Le token JWT.
     */
    private String createToken(Map<String, Object> claims, String subject, long expirationTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Méthode qui effectue deux vérifications : elle s'assure que le nom d'utilisateur extrait du token correspond bien à celui des UserDetails, et elle vérifie que le token n'est pas expiré.
     * @param token Le token JWT à valider.
     * @param userDetails Les détails de l'utilisateur à comparer.
     * @return true si le token est valide pour l'utilisateur et non expiré, false sinon.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Récupère la clé de signature à partir de la clé secrète.
     * @return La clé de signature.
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
