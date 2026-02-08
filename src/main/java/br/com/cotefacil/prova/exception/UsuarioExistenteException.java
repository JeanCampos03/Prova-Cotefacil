package br.com.cotefacil.prova.exception;

public class UsuarioExistenteException extends RuntimeException {
    public UsuarioExistenteException() {
        super("Username já está em uso");
    }

    public UsuarioExistenteException(String message) {
        super(message);
    }
}
