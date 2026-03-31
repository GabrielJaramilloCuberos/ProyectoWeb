package com.example.vigilapp.security.filter;

import static com.example.vigilapp.security.TokenJwtConfig.CONTENT_TYPE;
import static com.example.vigilapp.security.TokenJwtConfig.HEADER_AUTHORIZATION;
import static com.example.vigilapp.security.TokenJwtConfig.PREFIX_TOKEN;
import static com.example.vigilapp.security.TokenJwtConfig.SECRET_KEY;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

public class JwtValidationFilter extends BasicAuthenticationFilter {

    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {

        String header = request.getHeader(HEADER_AUTHORIZATION);

        //Si no hay token, continúa el flujo
        if (header == null || !header.startsWith(PREFIX_TOKEN)) {
            chain.doFilter(request, response);
            return;
        }

        //Quitar prefijo "Bearer "
        String token = header.replace(PREFIX_TOKEN, "");

        try {
            //Obtener claims
            Claims claims = Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Username (claim estándar)
            String username = claims.getSubject();

            List<?> authoritiesRaw = (List<?>) claims.get("authorities");

            List<String> authoritiesList = authoritiesRaw.stream()
            .map(Object::toString)
            .toList();

            Collection<? extends GrantedAuthority> authorities = authoritiesList.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            // Crear autenticación
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            // Guardar en contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            chain.doFilter(request, response);

        } catch (JwtException e) {
            Map<String, String> body = new HashMap<>();
            body.put("error",e.getMessage());
            body.put("message","El token JWT no es valido");

            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(CONTENT_TYPE);
        }
    }
}