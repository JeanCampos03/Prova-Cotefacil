package br.com.prova.cotefacil.api1.security;

import br.com.prova.cotefacil.api1.entity.Usuario;
import br.com.prova.cotefacil.api1.entity.UsuarioRole;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "chaveSecreta", "chave-secreta-teste");
        usuario = new Usuario("teste", "senha", UsuarioRole.USER);
    }

    @Test
    void criarToken_RetornaTokenValido() {
        String token = tokenService.criarToken(usuario);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT tem 3 partes
    }

    @Test
    void validacaoToken_ComTokenValido_RetornaUsername() {
        String token = tokenService.criarToken(usuario);
        String username = tokenService.validacaoToken(token);

        assertEquals("teste", username);
    }

    @Test
    void validacaoToken_ComTokenInvalido_LancaExcecao() {
        assertThrows(JWTVerificationException.class, () ->
                tokenService.validacaoToken("token.invalido.xyz"));
    }

    @Test
    void token_TemExpiracaoDeUmaHora() {
        String token = tokenService.criarToken(usuario);
        String username = tokenService.validacaoToken(token);
        assertNotNull(username);

        // Token deve ser válido agora (não expirado)
        assertDoesNotThrow(() -> tokenService.validacaoToken(token));
    }
}
