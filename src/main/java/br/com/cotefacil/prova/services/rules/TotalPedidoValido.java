package br.com.cotefacil.prova.services.rules;

import br.com.cotefacil.prova.dtos.order.OrderDTO;
import br.com.cotefacil.prova.exceptions.BusinessException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TotalPedidoValido implements RegraPedido{

    @Override
    public void validate(OrderDTO dto) {
        if (dto.totalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Total não pode ser negativo.");
        }
    }
}
