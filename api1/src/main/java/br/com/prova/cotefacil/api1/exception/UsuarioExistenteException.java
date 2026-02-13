package br.com.prova.cotefacil.api1.exception;

public class UsuarioExistenteException extends RuntimeException {
    public UsuarioExistenteException() {
        super("Username já está em uso");
    }

    public UsuarioExistenteException(String mensagem) {
        super(mensagem);
    }
}
