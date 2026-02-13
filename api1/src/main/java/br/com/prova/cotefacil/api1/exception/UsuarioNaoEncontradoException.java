package br.com.prova.cotefacil.api1.exception;

public class UsuarioNaoEncontradoException extends RuntimeException {
    public UsuarioNaoEncontradoException() {
        super("Usuario ou senha incorreto");
    }

    public UsuarioNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
