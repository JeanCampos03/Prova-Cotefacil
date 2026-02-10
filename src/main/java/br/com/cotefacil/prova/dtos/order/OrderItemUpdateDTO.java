package br.com.cotefacil.prova.dtos.order;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record OrderItemUpdateDTO(
        Long itemId,

        @Size(min = 3, max = 100)
        String productName,

        @Positive(message = "A quantidade deve ser maior que zero")
        Integer quantity,

        @Positive(message = "O preço deve ser positivo")
        BigDecimal unitPrice
) {}