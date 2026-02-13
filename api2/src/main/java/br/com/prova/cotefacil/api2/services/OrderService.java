package br.com.prova.cotefacil.api2.services;

import br.com.prova.cotefacil.api2.dtos.OrderDTO;
import br.com.prova.cotefacil.api2.dtos.OrderItemDTO;
import br.com.prova.cotefacil.api2.dtos.OrderItemUpdateDTO;
import br.com.prova.cotefacil.api2.dtos.OrderUpdateDTO;
import br.com.prova.cotefacil.api2.entitys.enums.OrderStatus;
import br.com.prova.cotefacil.api2.entitys.orders.Order;
import br.com.prova.cotefacil.api2.entitys.orders.OrderItem;
import br.com.prova.cotefacil.api2.exceptions.BusinessException;
import br.com.prova.cotefacil.api2.exceptions.NotFoundException;
import br.com.prova.cotefacil.api2.repositorys.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    public Page<OrderDTO> listarPedidos(@PageableDefault(page = 0, size = 10) Pageable pageable) {

        log.info("[ORDER] Listando pedidos - page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());

        Page<Order> page = orderRepository.findAllByStatusNot(OrderStatus.CANCELLED ,pageable);

        log.info("[ORDER] Total encontrados: {}", page.getTotalElements());

        return page.map(OrderDTO::fromEntity);
    }

    public OrderDTO listarPedidosPorId(Long id) {

        log.info("[ORDER] Buscando pedido por id={}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[ORDER] Pedido não encontrado id={}", id);
                    return new NotFoundException();
                });

        return OrderDTO.fromEntity(order);
    }

    @Transactional
    public Order salvarPedido(OrderDTO dto) {

        log.info("[ORDER] Criando pedido cliente={}", dto.customerName());

        Order order = new Order();
        order.setCustomerName(dto.customerName());
        order.setCustomerEmail(dto.customerEmail());
        order.setTotalAmount(BigDecimal.ZERO);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);


        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemDTO itemDto : dto.items()) {

            log.info("[ORDER-ITEM] Adicionando item produto={}, quantidade={}",
                    itemDto.productName(), itemDto.quantity());

            OrderItem item = new OrderItem();
            item.setProductName(itemDto.productName());
            item.setQuantity(itemDto.quantity());
            item.setUnitPrice(itemDto.unitPrice());

            BigDecimal subtotal =
                    itemDto.unitPrice().multiply(BigDecimal.valueOf(itemDto.quantity()));

            item.setSubtotal(subtotal);
            total = total.add(subtotal);

            order.addItem(item);
        }

        order.setTotalAmount(total);

        log.info("[ORDER] Total calculado={}", total);

        orderRepository.save(order);

        log.info("[ORDER] Pedido salvo com sucesso");

        return order;
    }

    @Transactional
    public Order atualizarPedido(Long id, OrderUpdateDTO dto) {

        log.info("[ORDER] Atualizando pedido id={}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[ORDER] Pedido não encontrado id={}", id);
                    return new NotFoundException();
                });

        if (order.getStatus() == OrderStatus.DELIVERED ||
                order.getStatus() == OrderStatus.CANCELLED) {

            log.warn("[ORDER] Tentativa de alteração bloqueada status={}", order.getStatus());
            throw new BusinessException("Status do pedido não pode ser alterado");
        }

        if (dto.status() != null) {
            log.info("[ORDER] Alterando status {} -> {}", order.getStatus(), dto.status());
            validarTransicaoStatus(order.getStatus(), dto.status());
            order.setStatus(dto.status());
        }

        if (dto.items() != null && !dto.items().isEmpty()) {

            log.info("[ORDER] Atualizando itens do pedido id={}", id);

            for (OrderItemUpdateDTO itemDTO : dto.items()) {

                OrderItem item = order.getItems().stream()
                        .filter(i -> i.getId().equals(itemDTO.itemId()))
                        .findFirst()
                        .orElseThrow(() -> {
                            log.warn("[ORDER-ITEM] itemId inválido {}", itemDTO.itemId());
                            return new BusinessException("Coloque um itemId válido");
                        });

                if (itemDTO.productName() != null) {
                    item.setProductName(itemDTO.productName());
                }

                if (itemDTO.quantity() != null) {
                    item.setQuantity(itemDTO.quantity());
                }

                if (itemDTO.unitPrice() != null) {
                    item.setUnitPrice(itemDTO.unitPrice());
                }

                item.setSubtotal(item.getUnitPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())));

                BigDecimal totalOrder = order.getItems().stream()
                        .map(OrderItem::getSubtotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                order.setTotalAmount(totalOrder);

                log.info("[ORDER] Novo total recalculado={}", totalOrder);
            }
        }

        return order;
    }

    private void validarTransicaoStatus(OrderStatus atual, OrderStatus novo) {

        if (novo == null) return;

        if (novo.ordinal() < atual.ordinal()) {
            log.warn("[ORDER] Tentativa de retroceder status {} -> {}", atual, novo);
            throw new BusinessException(
                    "Status não pode retroceder de " + atual + " para " + novo
            );
        }
    }

    @Transactional
    public void excluirPedido(Long orderId) {

        log.info("[ORDER] Cancelando pedido id={}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("[ORDER] Pedido não encontrado id={}", orderId);
                    return new NotFoundException("Pedido não encontrado");
                });

        if (order.getStatus() == OrderStatus.DELIVERED) {
            log.warn("[ORDER] Pedido já entregue não pode cancelar id={}", orderId);
            throw new BusinessException("Pedido já foi entregue, não pode ser excluido");
        }

        order.setStatus(OrderStatus.CANCELLED);

        log.info("[ORDER] Pedido cancelado id={}", orderId);
    }
}