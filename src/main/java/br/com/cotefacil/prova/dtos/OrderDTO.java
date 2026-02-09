package br.com.cotefacil.prova.dtos;

import br.com.cotefacil.prova.entitys.enums.OrderStatus;
import br.com.cotefacil.prova.entitys.orders.Order;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderDTO(
        Long id,

        @NotBlank(message = "Nome do cliente é obrigatório")
        String customerName,

        @Email(message = "Email inválido")
        @NotBlank(message = "Email é obrigatório")
        String customerEmail,

        @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime orderDate,

        OrderStatus status,

        BigDecimal totalAmount,

        List<OrderItemDTO> items
) {
    public static OrderDTO fromEntity(Order order) {
        return new OrderDTO(
                order.getId(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getOrderDate(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getItems().stream()
                        .map(OrderItemDTO::fromEntity)
                        .toList()
        );
    }
}