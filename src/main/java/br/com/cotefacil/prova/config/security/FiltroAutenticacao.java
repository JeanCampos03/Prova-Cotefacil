package br.com.cotefacil.prova.config.security;

import br.com.cotefacil.prova.exception.TokenException;
import br.com.cotefacil.prova.services.UsuarioService;
import br.com.cotefacil.prova.services.authService.TokenService;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class FiltroAutenticacao extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    private HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = pegarToken(request);
            if (token != null) {
                String usuario = tokenService.validacaoToken(token);
                UserDetails user = usuarioService.pegarUsuario(usuario);

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

    private String pegarToken(HttpServletRequest request) {
        String headerAutenticacao = request.getHeader("Authorization");
        if  (headerAutenticacao == null) return null;
        return headerAutenticacao.replace("Bearer ", "");
    }
}
