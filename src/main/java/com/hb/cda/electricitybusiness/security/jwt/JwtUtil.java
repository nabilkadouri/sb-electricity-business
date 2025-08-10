package com.hb.cda.electricitybusiness.security;

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

@Component // Indique que cette classe est un composant Spring et peut être injectée
public class JwtUtil {

    // La clé secrète pour signer les tokens, lue depuis les propriétés de l'application (ex: application.properties)
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    // Durée de validité du token d'accès en millisecondes (ex: 24 heures)
    @Value("${jwt.expiration}")
    private long EXPIRATION_TIME;

    // Durée de validité du refresh token en millisecondes (généralement plus longue, ex: 7 jours)
    @Value("${jwt.refresh.expiration}")
    private long REFRESH_EXPIRATION_TIME;

    /**
     * Extrait le nom d'utilisateur (sujet) du token JWT.
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
     * Extrait une revendication spécifique du token JWT.
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
     * Extrait toutes les revendications (claims) du token JWT.
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
        // Vous pouvez ajouter des rôles ou d'autres informations personnalisées aux claims ici
        // Par exemple: claims.put("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
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
        // Vous pouvez ajouter des claims spécifiques au refresh token si nécessaire
        return createToken(claims, userDetails.getUsername(), REFRESH_EXPIRATION_TIME);
    }

    /**
     * Crée le token JWT avec les revendications, le sujet et la durée d'expiration spécifiée.
     * Cette méthode est réutilisée pour les tokens d'accès et les refresh tokens.
     * @param claims Les revendications personnalisées.
     * @param subject Le sujet du token (généralement le nom d'utilisateur/email).
     * @param expirationTime La durée de validité du token en millisecondes.
     * @return Le token JWT.
     */
    private String createToken(Map<String, Object> claims, String subject, long expirationTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // Le sujet est généralement l'identifiant de l'utilisateur (email)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Date de création du token
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // Date d'expiration
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Signature avec la clé secrète et l'algorithme HS256
                .compact();
    }

    /**
     * Valide un token JWT (access ou refresh).
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
