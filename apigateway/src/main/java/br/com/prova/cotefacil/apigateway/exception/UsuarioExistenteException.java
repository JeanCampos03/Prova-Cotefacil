package br.com.prova.cotefacil.apigateway.exception;

public class UsuarioExistenteException extends RuntimeException {
    public UsuarioExistenteException() {
        super("Username já está em uso");
    }

    public UsuarioExistenteException(String message) {
        super(message);
    }
}
