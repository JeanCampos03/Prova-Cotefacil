package br.com.cotefacil.prova.services;

import br.com.cotefacil.prova.dtos.order.OrderDTO;
import br.com.cotefacil.prova.dtos.order.OrderItemDTO;
import br.com.cotefacil.prova.dtos.order.OrderItemUpdateDTO;
import br.com.cotefacil.prova.entitys.orders.Order;
import br.com.cotefacil.prova.entitys.orders.OrderItem;
import br.com.cotefacil.prova.exceptions.NotFoundException;
import br.com.cotefacil.prova.repositorys.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Service
public class OrderItemService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<OrderItemDTO> buscarItensPorPedido(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Pedido não encontrado"));

        return order.getItems().stream()
                .map(OrderItemDTO::fromEntity)
                .toList();
    }


    public List<OrderItemDTO> adiconaItemPedido(List<OrderItemUpdateDTO> orderItemUpdateDTO, Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pedido não encontrado"));

        BigDecimal total = order.getTotalAmount() != null
                ? order.getTotalAmount()
                : BigDecimal.ZERO;

        for (OrderItemUpdateDTO itemDTO : orderItemUpdateDTO) {

            OrderItem item = new OrderItem();
            item.setProductName(itemDTO.productName());
            item.setQuantity(itemDTO.quantity());
            item.setUnitPrice(itemDTO.unitPrice());

            BigDecimal subtotal = itemDTO.unitPrice()
                    .multiply(BigDecimal.valueOf(itemDTO.quantity()));

            item.setSubtotal(subtotal);

            total = total.add(subtotal);

            order.addItem(item);
        }

        order.setTotalAmount(total);

        orderRepository.save(order);

        return order.getItems()
                .stream()
                .map(OrderItemDTO::fromEntity)
                .toList();

    }

}
