package br.com.cotefacil.prova.dtos.order;

import br.com.cotefacil.prova.entitys.enums.OrderStatus;
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
