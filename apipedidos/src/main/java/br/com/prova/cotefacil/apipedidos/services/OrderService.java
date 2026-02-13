package br.com.prova.cotefacil.apipedidos.services;

import br.com.prova.cotefacil.apigateway.entity.Usuario;
import br.com.prova.cotefacil.apigateway.service.UsuarioService;
import br.com.prova.cotefacil.apipedidos.dtos.OrderDTO;
import br.com.prova.cotefacil.apipedidos.dtos.OrderItemDTO;
import br.com.prova.cotefacil.apipedidos.dtos.OrderItemUpdateDTO;
import br.com.prova.cotefacil.apipedidos.dtos.OrderUpdateDTO;
import br.com.prova.cotefacil.apipedidos.entitys.enums.OrderStatus;
import br.com.prova.cotefacil.apipedidos.entitys.orders.Order;
import br.com.prova.cotefacil.apipedidos.entitys.orders.OrderItem;
import br.com.prova.cotefacil.apipedidos.exceptions.BusinessException;
import br.com.prova.cotefacil.apipedidos.exceptions.NotFoundException;
import br.com.prova.cotefacil.apipedidos.repositorys.OrderRepository;
import br.com.prova.cotefacil.apipedidos.utils.SecurityUtils;
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
    private final SecurityUtils securityUtils;

    public Page<OrderDTO> listarPedidos(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        String username = securityUtils.getCurrentUsername();
        log.info("[ORDER] Listando pedidos - page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Order> page = orderRepository.findAllByStatusNotAndCreatedBy(OrderStatus.CANCELLED ,pageable, username);
        log.info("[ORDER] Total encontrados: {}", page.getTotalElements());
        return page.map(OrderDTO::fromEntity);
    }

    public OrderDTO listarPedidosPorId(Long id) {

        String username = securityUtils.getCurrentUsername();

        log.info("[ORDER] Buscando pedido por id={}", id);

        Order order = orderRepository.findByIdAndCreatedBy(id, username)
                .orElseThrow(() -> {
                    log.warn("[ORDER] Pedido não encontrado id={}", id);
                    return new NotFoundException();
                });

        return OrderDTO.fromEntity(order);
    }

    @Transactional
    public OrderDTO salvarPedido(OrderDTO dto) {

        String username = securityUtils.getCurrentUsername();
        log.info("[ORDER] Criando pedido cliente={}", dto.customerName());

        Order order = new Order();
        order.setCustomerName(dto.customerName());
        order.setCustomerEmail(dto.customerEmail());
        order.setTotalAmount(BigDecimal.ZERO);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedBy(username);


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

        return OrderDTO.fromEntity(order);
    }

    @Transactional
    public OrderDTO atualizarPedido(Long id, OrderUpdateDTO dto) {
        String username = securityUtils.getCurrentUsername();
        log.info("[ORDER] Atualizando pedido id={}", id);

        Order order = orderRepository.findByIdAndCreatedBy(id, username)
                .orElseThrow(() -> {
                    log.warn("[ORDER] Pedido não encontrado id={}", id);
                    return new BusinessException("Pedido não encontrado");
                });

        if (order.getStatus() == OrderStatus.DELIVERED ||
                order.getStatus() == OrderStatus.CANCELLED) {

            log.warn("[ORDER] Tentativa de alteração bloqueada status={}", order.getStatus());
            throw new BusinessException("Status do pedido não pode ser alterado");
        }

        if (dto.status() != null) {
            log.info("[ORDER] Alterando status {} -> {}", order.getStatus(), dto.status());
            validateStatusTransition(order.getStatus(), dto.status());
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

        return OrderDTO.fromEntity(order);
    }

    @Transactional
    public void excluirPedido(Long orderId) {
        String username = securityUtils.getCurrentUsername();
        log.info("[ORDER] Cancelando pedido id={}", orderId);



        Order order = orderRepository.findByIdAndCreatedBy(orderId, username)
                .orElseThrow(() -> {
                    log.warn("[ORDER] Pedido não encontrado ou acesso negado id={}, user={}",
                            orderId, username);
                    return new NotFoundException("Pedido não encontrado");
                });

        if (order.getStatus() == OrderStatus.DELIVERED) {
            log.warn("[ORDER] Pedido já entregue não pode cancelar id={}", orderId);
            throw new BusinessException("Pedido já foi entregue, não pode ser excluido");
        }

        order.setStatus(OrderStatus.CANCELLED);


        log.info("[ORDER] Pedido cancelado id={}", orderId);
    }

    private void validateStatusTransition(OrderStatus atual, OrderStatus novo) {
        if (novo == null) return;

        if (!atual.canTransitionTo(novo)) {
            log.warn("[ORDER] Transição inválida {} -> {}", atual, novo);
            throw new BusinessException(
                    String.format(
                            "Transição de status inválida de %s para %s. Status possíveis: %s",
                            atual,
                            novo,
                            atual.getNextPossibleStatuses()
                    )
            );
        }
    }

}