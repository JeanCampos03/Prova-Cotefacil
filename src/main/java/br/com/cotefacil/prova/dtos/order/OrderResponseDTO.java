package br.com.cotefacil.prova.dtos.order;

import br.com.cotefacil.prova.entitys.enums.OrderStatus;
import lombok.Builder;

@Builder
public record OrderResponseDTO(
        Long idOrder,
        String customerEmail,
        OrderStatus status
) {
}
