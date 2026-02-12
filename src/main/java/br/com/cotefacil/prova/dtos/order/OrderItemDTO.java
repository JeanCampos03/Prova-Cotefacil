package br.com.cotefacil.prova.dtos.order;

import br.com.cotefacil.prova.entitys.orders.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record OrderItemDTO(

        @NotBlank(message = "Nome do produto é obrigatório")
        @Size(min = 3, max = 100)
        @Schema(example = "Dipirona")
        String productName,

        @NotNull(message = "Quantidade é obrigatória")
        @Positive(message = "Quantidade deve ser maior que zero")
        @Schema(example = "4")
        Integer quantity,

        @NotNull(message = "Preço unitário é obrigatório")
        @Positive(message = "Preço unitário deve ser maior que zero")
        @Digits(integer = 10, fraction = 2)
        @Schema(example = "4.89")
        BigDecimal unitPrice

) {
    public static OrderItemDTO fromEntity(OrderItem item) {
        return new OrderItemDTO(
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice()
        );
    }
}

