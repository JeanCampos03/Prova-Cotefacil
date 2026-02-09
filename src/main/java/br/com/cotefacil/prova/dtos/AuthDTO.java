package br.com.cotefacil.prova.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthDTO(
        @NotBlank(message = "Username é obrigatório")
        @Size(min = 3, max = 50, message = "Username deve ter entre 3 e 50 caracteres")
        String username,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, max = 100, message = "Senha deve ter no mínimo 6 caracteres")
        String password
) {}
