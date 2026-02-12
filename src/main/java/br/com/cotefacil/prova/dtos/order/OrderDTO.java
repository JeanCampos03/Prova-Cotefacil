package br.com.cotefacil.prova.dtos.order;

import br.com.cotefacil.prova.entitys.enums.OrderStatus;
import br.com.cotefacil.prova.entitys.orders.Order;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderDTO(

        @Schema(example = "Maria Silva")
        @NotBlank(message = "Nome do cliente é obrigatório")
        String customerName,

        @Schema(example = "maria.silva@email.com")
        @Email(message = "Email inválido")
        @NotBlank(message = "Email é obrigatório")
        String customerEmail,

        @JsonIgnore
        BigDecimal totalAmount,

        @NotEmpty(message = "O pedido deve conter ao menos um item")
        @Valid
        List<OrderItemDTO> items
) {
    public static OrderDTO fromEntity(Order order) {
        return new OrderDTO(
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getTotalAmount(),
                order.getItems().stream()
                        .map(OrderItemDTO::fromEntity)
                        .toList()
        );
    }
}