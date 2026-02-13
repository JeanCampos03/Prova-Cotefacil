package br.com.prova.cotefacil.apipedidos.dtos;

import br.com.prova.cotefacil.apipedidos.entitys.enums.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record OrderUpdateDTO (
        @Size(min = 3, max = 100)
        String customerName,

        @Email(message = "Email inválido")
        String customerEmail,


        OrderStatus status,

        @Valid
        List<OrderItemUpdateDTO> items
){}
