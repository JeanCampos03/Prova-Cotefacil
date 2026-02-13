package br.com.prova.cotefacil.apigateway.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("Username já está em uso");
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
