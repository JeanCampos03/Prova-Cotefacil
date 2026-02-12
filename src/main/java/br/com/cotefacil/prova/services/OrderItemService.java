package br.com.cotefacil.prova.services;

import br.com.cotefacil.prova.dtos.order.OrderDTO;
import br.com.cotefacil.prova.dtos.order.OrderItemDTO;
import br.com.cotefacil.prova.dtos.order.OrderItemUpdateDTO;
import br.com.cotefacil.prova.entitys.orders.Order;
import br.com.cotefacil.prova.entitys.orders.OrderItem;
import br.com.cotefacil.prova.exceptions.NotFoundException;
import br.com.cotefacil.prova.repositorys.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class OrderItemService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<OrderItemDTO> buscarItensPorPedido(Long orderId) {

        log.info("[ORDER-ITEM] Buscando itens do pedido id={}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("[ORDER-ITEM] Pedido não encontrado id={}", orderId);
                    return new NotFoundException("Pedido não encontrado");
                });

        return order.getItems().stream()
                .map(OrderItemDTO::fromEntity)
                .toList();
    }

    public List<OrderItemDTO> adiconaItemPedido(List<OrderItemUpdateDTO> orderItemUpdateDTO, Long id) {

        log.info("[ORDER-ITEM] Adicionando itens ao pedido id={}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[ORDER-ITEM] Pedido não encontrado id={}", id);
                    return new NotFoundException("Pedido não encontrado");
                });

        BigDecimal total = order.getTotalAmount() != null
                ? order.getTotalAmount()
                : BigDecimal.ZERO;

        for (OrderItemUpdateDTO itemDTO : orderItemUpdateDTO) {

            log.info("[ORDER-ITEM] Novo item produto={}, quantidade={}",
                    itemDTO.productName(), itemDTO.quantity());

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

        log.info("[ORDER-ITEM] Total atualizado={}", total);

        return order.getItems()
                .stream()
                .map(OrderItemDTO::fromEntity)
                .toList();
    }
}