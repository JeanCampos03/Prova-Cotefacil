package br.com.prova.cotefacil.apigateway.entity;

public enum UsuarioRole {
    USER("USER");

    private final String role;

    UsuarioRole(String role) {
        this.role = role;
    }


    public String getRole() {
        return role;
    }
}
