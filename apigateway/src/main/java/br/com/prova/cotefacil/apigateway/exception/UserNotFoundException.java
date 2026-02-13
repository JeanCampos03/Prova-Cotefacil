package br.com.prova.cotefacil.apigateway.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("Usuario ou senha incorreto");
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
