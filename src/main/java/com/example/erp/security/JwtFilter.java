package com.example.erp.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.erp.services.TokenBlackListService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final TokenBlackListService blacklistService;

    public JwtFilter(JwtService jwtService,
            CustomUserDetailsService userDetailsService,
            TokenBlackListService blacklistService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.blacklistService = blacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {

            // 1. Extraemos la cabecera "Authorization" de la petición HTTP
            String authHeader = request.getHeader("Authorization");

            // 2. Si la cabecera no existe o no empieza con "Bearer ", dejamos pasar la petición al siguiente filtro
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            // 3. Extraemos el token puro (quitando la palabra "Bearer " que mide 7 caracteres)
            String jwt = authHeader.substring(7);

            // 4. Extraemos el nombre de usuario usando nuestro JwtService
            String username = jwtService.extractUsername(jwt);

            // 5. Si encontramos un username y el usuario NO está autenticado todavía en el contexto de Spring...
            if (username == null) {
                filterChain.doFilter(request, response);
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            // Buscamos al usuario en la base de datos para validar si sigue activo, sus roles, etc.
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 6. Si el token es matemáticamente válido y pertenece a este usuario...
            if (!jwtService.isTokenValid(jwt, userDetails)) {
                filterChain.doFilter(request, response);
                return;
            }

            if (blacklistService.esTokenInvalido(jwt)) {
                filterChain.doFilter(request, response);
                return;
            }

            // Creamos el objeto de autenticación definitivo que Spring Security entiende
            UsernamePasswordAuthenticationToken authToken
                    = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities() // Aquí van sus roles (ej: ROLE_ADMIN)
                    );

            // Le añadimos detalles adicionales de la petición web (IP, sesión, etc.)
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // ¡EL PASO CLAVE!: Seteamos al usuario dentro del contexto de seguridad de Spring.
            // A partir de esta línea, para Spring, el usuario está oficialmente LOGUEADO.
            SecurityContextHolder.getContext().setAuthentication(authToken);

            // 7. Continuamos el viaje hacia el Controller
            filterChain.doFilter(request, response);

        } catch (JwtException e) {
            response.setStatus(401);
            return;
        }

    }

}
