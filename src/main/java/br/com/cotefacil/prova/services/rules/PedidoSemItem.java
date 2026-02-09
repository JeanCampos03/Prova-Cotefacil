package br.com.cotefacil.prova.services.rules;

import br.com.cotefacil.prova.dtos.OrderDTO;
import br.com.cotefacil.prova.exceptions.BusinessException;
import org.springframework.stereotype.Component;

@Component
public class PedidoSemItem implements RegraPedido{

    @Override
    public void validate(OrderDTO dto) {
        if (dto.items() == null || dto.items().isEmpty()) {
            throw new BusinessException("Pedido deve possuir ao menos um item.");
        }
    }
}
