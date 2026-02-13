package br.com.prova.cotefacil.api2.dtos;

import br.com.prova.cotefacil.api2.entitys.enums.OrderStatus;
import lombok.Builder;

@Builder
public record OrderResponseDTO(
        Long idOrder,
        String customerEmail,
        OrderStatus status
) {
}
