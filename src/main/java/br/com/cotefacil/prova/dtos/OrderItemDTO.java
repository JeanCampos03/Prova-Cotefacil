package br.com.cotefacil.prova.dtos;

import br.com.cotefacil.prova.entitys.orders.OrderItem;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderItemDTO(

        Long id,

        @NotBlank(message = "Nome do produto é obrigatório")
        String productName,

        @NotNull(message = "Quantidade é obrigatória")
        @Positive(message = "Quantidade deve ser maior que zero")
        Integer quantity,

        @NotNull(message = "Preço unitário é obrigatório")
        @Positive(message = "Preço unitário deve ser maior que zero")
        @Digits(integer = 10, fraction = 2)
        BigDecimal unitPrice,

        @NotNull(message = "Subtotal é obrigatório")
        @Positive(message = "Subtotal deve ser maior que zero")
        @Digits(integer = 10, fraction = 2)
        BigDecimal subtotal
) {
    public static OrderItemDTO fromEntity(OrderItem item) {
        return new OrderItemDTO(
                item.getId(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal()
        );
    }
}

