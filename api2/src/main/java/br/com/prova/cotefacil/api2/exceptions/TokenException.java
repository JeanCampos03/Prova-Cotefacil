package br.com.prova.cotefacil.api2.exceptions;

import com.auth0.jwt.exceptions.JWTVerificationException;

public class TokenException extends JWTVerificationException {
    public TokenException() {
        super("Token inválido ou expirado");
    }



    public TokenException(String message) {
        super(message);
    }
}
