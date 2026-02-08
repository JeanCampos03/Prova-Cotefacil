package br.com.cotefacil.prova.exceptions;

public class UsuarioExistenteException extends RuntimeException {
    public UsuarioExistenteException() {
        super("Username já está em uso");
    }

    public UsuarioExistenteException(String mensagem) {
        super(mensagem);
    }
}
