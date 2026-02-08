package br.com.cotefacil.prova.exceptions;

public class UsuarioNaoEncontradoException extends RuntimeException {
    public UsuarioNaoEncontradoException() {
        super("Usuario ou senha incorreto");
    }

    public UsuarioNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
