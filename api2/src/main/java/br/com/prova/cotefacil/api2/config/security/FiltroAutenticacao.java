package br.com.prova.cotefacil.api2.config.security;

import br.com.prova.cotefacil.api2.exceptions.TokenException;
import br.com.prova.cotefacil.api2.services.authService.TokenService;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
public class FiltroAutenticacao extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;

    @Autowired
    private HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = pegarToken(request);
            if (token != null) {
                String username = tokenService.validacaoToken(token);
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
                var autenticacao = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(autenticacao);
            }
            filterChain.doFilter(request, response);
        } catch (JWTVerificationException e) {
            handlerExceptionResolver.resolveException(request, response, null, new TokenException());
        }
    }

    private String pegarToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null) return null;
        return header.replace("Bearer ", "");
    }
}
