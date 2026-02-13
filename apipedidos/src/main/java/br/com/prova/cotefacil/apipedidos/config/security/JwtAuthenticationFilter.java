package br.com.prova.cotefacil.apipedidos.config.security;

import br.com.prova.cotefacil.apipedidos.exceptions.TokenException;
import br.com.prova.cotefacil.apipedidos.services.authService.TokenService;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();;

    private final TokenService tokenService;

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getToken(request);
            if (token != null) {
                String username = tokenService.validateToken(token);
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
                var autenticacao = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(autenticacao);
            }
            filterChain.doFilter(request, response);
        } catch (JWTVerificationException e) {
            handlerExceptionResolver.resolveException(request, response, null, new TokenException());
        }
    }

    private String getToken(HttpServletRequest request) {
        String headerAutenticacao = request.getHeader("Authorization");
        if (headerAutenticacao == null || !headerAutenticacao.startsWith(BEARER_PREFIX)) {
            return null;
        }
        String token = headerAutenticacao.substring(BEARER_PREFIX_LENGTH).trim();
        return token.isEmpty() ? null : token;
    }
}
