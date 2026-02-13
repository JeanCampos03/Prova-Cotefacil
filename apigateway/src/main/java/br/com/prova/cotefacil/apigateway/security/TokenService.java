package br.com.prova.cotefacil.apigateway.security;


import br.com.prova.cotefacil.apigateway.entities.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {

    @Value("${api.chave.secreta}")
    private String chaveSecreta;

    public String criarToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(chaveSecreta);
            return JWT.create()
                    .withIssuer("authentication-api")
                    .withSubject(usuario.getUsername())
                    .withExpiresAt(tempoExpiracao())
                    .sign(algorithm);
        } catch (
                JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token", exception);
        }
    }

    public String validacaoToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(chaveSecreta);
            return JWT.require(algorithm)
                    .withIssuer("authentication-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (
                JWTCreationException exception) {
            throw new RuntimeException("Erro na validação do token", exception);
        }
    }

    private Instant tempoExpiracao() {
        return Instant.now().plus(1, ChronoUnit.HOURS);
    }

}
