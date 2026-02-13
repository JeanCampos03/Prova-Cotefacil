package br.com.prova.cotefacil.apipedidos.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record OrderItemUpdateDTO(
        @Schema(example = "1")
        Long itemId,

        @Size(min = 3, max = 100)
        @Schema(example = "Dipirona")
        String productName,

        @Positive(message = "A quantidade deve ser maior que zero")
        @Schema(example = "3")
        Integer quantity,

        @Positive(message = "O preço deve ser positivo")
        @Schema(example = "4.89")
        BigDecimal unitPrice
) {}