package br.com.cotefacil.prova.dtos;

import br.com.cotefacil.prova.entitys.enums.UsuarioRole;

public record RegisterDTO(String username, String password, UsuarioRole role) {
}
