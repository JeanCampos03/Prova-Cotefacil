package br.com.cotefacil.prova.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class ConfigJWT {

    @Value("${api.security.token.secret}")
    private String secret;

    public String criarToken() {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("auth-api")
                    .withSubject("colocar user")
                    .withExpiresAt(tempoExpiracao())
                    .sign(algorithm);
        } catch (
                JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token de autenticação", exception);
        }
    }

    public String validacaoToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (
                JWTCreationException exception) {
            throw new RuntimeException("Erro ao validar token de autenticação", exception);
        }
    }

    private Instant tempoExpiracao() {
        return LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00"));
    }

}
