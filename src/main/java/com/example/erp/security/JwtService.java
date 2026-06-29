package com.example.erp.security;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.erp.entities.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    public String generateToken(Usuario usuario) {

        Map<String,Object> claims = new HashMap<>(); 

        claims.put("nombre", usuario.getNombre());
        claims.put("rol", usuario.getRol());

        UserDetails userDetails = usuario;

        return generateToken(claims, userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {

        Instant now = Instant.now();

        Date issuedAt = Date.from(now);
        Date expiration = Date.from(now.plus(1, ChronoUnit.DAYS));

        return Jwts.builder()
                .setClaims(extraClaims) // Aquí podríamos agregar datos extra al token si quisiéramos
                .setSubject(userDetails.getUsername()) // El "Subject" (Sujeto) del token es el nombre de usuario
                .setIssuedAt(issuedAt) // Fecha de creación: ¡AHORA!
                .setExpiration(expiration) // Fecha de expiración: 24 horas
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Firmamos matemáticamente el token
                .compact(); // Construye y devuelve el String final separado por puntos
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 1. Extraer el "Subject" (que en nuestro caso es el username)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 2. Método genérico para extraer cualquier dato (Claim) del token
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 3. Verificar si el token es válido (coincide el usuario y no ha expirado)
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // 4. Verificar si el token ya pasó su fecha de expiración
    public boolean isTokenExpired(String token) {
        return extractExpiration(token)
                .toInstant()
                .isBefore(Instant.now());
    }

    // 5. Extraer la fecha de expiración
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 6. El método que realmente abre el token usando nuestra SECRET_KEY
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey()) // Usamos la misma llave para abrirlo
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
