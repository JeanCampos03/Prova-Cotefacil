package br.com.prova.cotefacil.apigateway.security;

import br.com.prova.cotefacil.apigateway.entities.User;
import br.com.prova.cotefacil.apigateway.exception.TokenException;
import br.com.prova.cotefacil.apigateway.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FiltrateAuthentication {

    @Mock
    private TokenService tokenService;

    @Mock
    private UserService userService; // 🔥 FALTAVA ISSO

    @Mock
    private HandlerExceptionResolver handlerExceptionResolver;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveAutenticarComTokenValido() throws Exception {

        String token = "valid.jwt.token";
        String username = "testuser";

        User user = new User();
        user.setUsername(username);

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(tokenService.validacaoToken(token))
                .thenReturn(username);

        when(userService.getUser(username))
                .thenReturn(user);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(username,
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void deveContinuarSemTokenNoHeader() throws Exception {

        when(request.getHeader("Authorization"))
                .thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void deveRejeitarTokenInvalido() throws Exception {

        String token = "invalid.jwt.token";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(tokenService.validacaoToken(token))
                .thenThrow(new TokenException("Token inválido"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(handlerExceptionResolver).resolveException(
                eq(request),
                eq(response),
                isNull(),
                any()
        );
    }


}
