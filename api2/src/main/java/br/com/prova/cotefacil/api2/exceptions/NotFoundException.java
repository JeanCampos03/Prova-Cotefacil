package br.com.prova.cotefacil.api2.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super("Dado não encontrado.");
    }

    public NotFoundException(String message) {
        super(message);
    }
}
