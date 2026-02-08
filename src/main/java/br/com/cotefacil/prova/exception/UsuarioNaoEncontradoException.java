package br.com.cotefacil.prova.exception;

public class UsuarioNaoEncontradoException extends RuntimeException {
    public UsuarioNaoEncontradoException() {
        super("Usuario ou senha incorreto");
    }

    public UsuarioNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
