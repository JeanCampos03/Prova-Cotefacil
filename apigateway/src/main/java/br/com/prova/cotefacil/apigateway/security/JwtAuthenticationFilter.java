package br.com.prova.cotefacil.apigateway.security;


import br.com.prova.cotefacil.apigateway.exception.TokenException;
import br.com.prova.cotefacil.apigateway.service.UserService;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final TokenService tokenService;


    private final UserService userService;


    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getToken(request);
            if (token != null) {
                String usuario = tokenService.validacaoToken(token);
                UserDetails user = userService.getUser(usuario);

                var autenticacao = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(autenticacao);
            }
            filterChain.doFilter(request, response);
        } catch(JWTVerificationException e) {
            handlerExceptionResolver.resolveException(
                    request,
                    response,
                    null,
                    new TokenException()
            );
        }

    }

    private String getToken(HttpServletRequest request) {
        String headerAutenticacao = request.getHeader("Authorization");
        if (headerAutenticacao == null || !headerAutenticacao.startsWith("Bearer ")) {
            return null;
        }
        String token = headerAutenticacao.substring(7).trim();
        return token.isEmpty() ? null : token;
    }
}
