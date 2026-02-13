package br.com.prova.cotefacil.apipedidos.services;

import br.com.prova.cotefacil.apigateway.service.UsuarioService;
import br.com.prova.cotefacil.apipedidos.dtos.OrderItemDTO;
import br.com.prova.cotefacil.apipedidos.dtos.OrderItemUpdateDTO;
import br.com.prova.cotefacil.apipedidos.entitys.orders.Order;
import br.com.prova.cotefacil.apipedidos.entitys.orders.OrderItem;
import br.com.prova.cotefacil.apipedidos.exceptions.NotFoundException;
import br.com.prova.cotefacil.apipedidos.repositorys.OrderRepository;
import br.com.prova.cotefacil.apipedidos.utils.SecurityUtils;
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
    private final SecurityUtils securityUtils;

    @Transactional(readOnly = true)
    public List<OrderItemDTO> buscarItensPorPedido(Long orderId) {
        String username = securityUtils.getCurrentUsername();

        log.info("[ORDER-ITEM] Buscando itens do pedido id={}", orderId);

        Order order = orderRepository.findByIdAndCreatedBy(orderId, username)
                .orElseThrow(() -> {
                    log.warn("[ORDER-ITEM] Pedido não encontrado ou acesso negado id={}, user={}",
                            orderId, username);
                    return new NotFoundException("Pedido não encontrado");
                });

        return order.getItems().stream()
                .map(OrderItemDTO::fromEntity)
                .toList();
    }

    @Transactional
    public List<OrderItemDTO> adicionaItemPedido(List<OrderItemUpdateDTO> orderItemUpdateDTO, Long id) {
        String username = securityUtils.getCurrentUsername();
        log.info("[ORDER-ITEM] Adicionando itens ao pedido id={}", id);

        Order order = orderRepository.findByIdAndCreatedBy(id, username)
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