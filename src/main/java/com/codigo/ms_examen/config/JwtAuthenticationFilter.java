package com.codigo.ms_examen.config;

import com.codigo.ms_examen.controller.advice.AuthenticationException;
import com.codigo.ms_examen.service.JwtService;
import com.codigo.ms_examen.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String tokenExtraidoHeader = request.getHeader("Authorization");
        final String tokenLimpio;
        final String userEmail;
        if (StringUtils.isEmpty(tokenExtraidoHeader) || !StringUtils.startsWithIgnoreCase(tokenExtraidoHeader, "Bearer ")) {
//            throw new AuthenticationException("El token proporcionado no es válido o ha expirado.");
            System.out.println("Token mal formado o ausente: " + tokenExtraidoHeader); // Log para debug
            filterChain.doFilter(request, response);
            return;
        }
//        if (StringUtils.isEmpty(tokenExtraidoHeader) ||
//                !StringUtils.startsWithIgnoreCase(tokenExtraidoHeader, "Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
        tokenLimpio = tokenExtraidoHeader.substring(7);
//        userEmail = jwtService.extractUsername(tokenLimpio);
        try {
            userEmail = jwtService.extractUsername(tokenLimpio);
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            System.out.println("Error al extraer el username del JWT: " + e.getMessage());
            return;
        }
        if (Objects.nonNull(userEmail) &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = usuarioService.userDetailsService().loadUserByUsername(userEmail);
            if (jwtService.validateToken(tokenLimpio, userDetails)) {
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null, userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                securityContext.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(securityContext);
            }
        }
        filterChain.doFilter(request, response);

    }
}
