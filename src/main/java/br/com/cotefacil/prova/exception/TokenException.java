package br.com.cotefacil.prova.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;

public class TokenException extends JWTVerificationException {
    public TokenException() {
        super("Token inválido ou expirado");
    }



    public TokenException(String message) {
        super(message);
    }
}
