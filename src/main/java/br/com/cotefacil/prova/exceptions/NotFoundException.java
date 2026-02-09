package br.com.cotefacil.prova.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super("Dado não encontrado.");
    }

    public NotFoundException(String message) {
        super(message);
    }
}
