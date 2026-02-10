package br.com.cotefacil.prova.services.rules;

import br.com.cotefacil.prova.dtos.order.OrderDTO;

public interface RegraPedido {
    void validate(OrderDTO dto);
}
