package br.com.prova.cotefacil.apipedidos.dtos;

import br.com.prova.cotefacil.apipedidos.entities.enums.OrderStatus;
import lombok.Builder;

@Builder
public record OrderResponseDTO(
        Long idOrder,
        String customerEmail,
        OrderStatus status
) {
}
