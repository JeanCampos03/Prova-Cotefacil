package br.com.cotefacil.prova.dtos;

import br.com.cotefacil.prova.entitys.users.UsuarioRole;

public record RegisterDTO(String username, String password, UsuarioRole role) {
}
