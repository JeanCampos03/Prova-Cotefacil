package br.com.prova.cotefacil.apigateway.exception;

public class UsuarioNaoEncontradoException extends RuntimeException {
    public UsuarioNaoEncontradoException() {
        super("Usuario ou senha incorreto");
    }

    public UsuarioNaoEncontradoException(String message) {
        super(message);
    }
}
