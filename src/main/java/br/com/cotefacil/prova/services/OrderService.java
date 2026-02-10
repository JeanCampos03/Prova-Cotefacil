package br.com.cotefacil.prova.services;

import br.com.cotefacil.prova.dtos.order.OrderDTO;
import br.com.cotefacil.prova.dtos.order.OrderItemDTO;
import br.com.cotefacil.prova.dtos.order.OrderItemUpdateDTO;
import br.com.cotefacil.prova.dtos.order.OrderUpdateDTO;
import br.com.cotefacil.prova.entitys.enums.OrderStatus;
import br.com.cotefacil.prova.entitys.orders.Order;
import br.com.cotefacil.prova.entitys.orders.OrderItem;
import br.com.cotefacil.prova.exceptions.BusinessException;
import br.com.cotefacil.prova.exceptions.NotFoundException;
import br.com.cotefacil.prova.repositorys.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public Page<OrderDTO> listarPedidos(@PageableDefault(page = 0, size = 10) Pageable pageable) {

        Page<Order> page = orderRepository.findAll(pageable);

        return page.map(order ->
                new OrderDTO(
                        order.getId(),
                        order.getCustomerName(),
                        order.getCustomerEmail(),
                        order.getOrderDate(),
                        order.getStatus(),
                        order.getTotalAmount(),
                        order.getItems().stream()
                                .map(i -> new OrderItemDTO(
                                        i.getId(),
                                        i.getProductName(),
                                        i.getQuantity(),
                                        i.getUnitPrice(),
                                        i.getSubtotal()
                                ))
                                .toList()
                )
        );
    }

    public OrderDTO listarPedidosPorId(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        return OrderDTO.fromEntity(order);
    }

    @Transactional
    public Order salvarPedido(OrderDTO dto) {
        Order order = new Order();
        order.setCustomerName(dto.customerName());
        order.setCustomerEmail(dto.customerEmail());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemDTO itemDto : dto.items()) {
            OrderItem item = new OrderItem();
            item.setProductName(itemDto.productName());
            item.setQuantity(itemDto.quantity());
            item.setUnitPrice(itemDto.unitPrice());

            BigDecimal subtotal =
                    itemDto.unitPrice().multiply(BigDecimal.valueOf(itemDto.quantity()));

            item.setSubtotal(subtotal);
            total = total.add(subtotal);

            order.addItem(item); // já faz o vínculo
        }
        order.setTotalAmount(total);
        orderRepository.save(order);

        return order;

    }

    @Transactional
    public Order atualizarPedido(Long id, OrderUpdateDTO dto) {

        Order order = orderRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        if (order.getStatus() == OrderStatus.DELIVERED ||
            order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("Status do pedido não pode ser alterado");
        }

        if (dto.customerEmail() != null) {
            order.setCustomerEmail(dto.customerEmail());
        }

        if (dto.customerName() != null) {
            order.setCustomerName(dto.customerName());
        }

        if (dto.status() != null) {
            validarTransicaoStatus(order.getStatus(), dto.status());
            order.setStatus(dto.status());
        }

        if (dto.items() != null && !dto.items().isEmpty()) {
            for (OrderItemUpdateDTO itemDTO : dto.items()) {

                OrderItem item = order.getItems().stream()
                        .filter(i -> i.getId().equals(itemDTO.itemId()))
                        .findFirst()
                        .orElseThrow(() -> new BusinessException("Item não encontrado"));

                if (itemDTO.productName() != null) {
                    item.setProductName(itemDTO.productName());
                }

                if (itemDTO.quantity() != null) {
                    item.setQuantity(itemDTO.quantity());
                }

                if (itemDTO.unitPrice() != null) {
                    item.setUnitPrice(itemDTO.unitPrice());
                }

                item.setSubtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

                BigDecimal totalOrder = order.getItems().stream()
                        .map(OrderItem::getSubtotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                order.setTotalAmount(totalOrder);
            }

        }
        return order;
    }


    private void validarTransicaoStatus(OrderStatus atual, OrderStatus novo) {

        if (novo == null) return;

        if (novo.ordinal() < atual.ordinal()) {
            throw new BusinessException(
                    "Status não pode retroceder de " + atual + " para " + novo
            );
        }
    }



}

