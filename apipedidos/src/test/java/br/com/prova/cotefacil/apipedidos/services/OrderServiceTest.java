package br.com.prova.cotefacil.apipedidos.services;

import br.com.prova.cotefacil.apipedidos.dtos.OrderDTO;
import br.com.prova.cotefacil.apipedidos.dtos.OrderItemDTO;
import br.com.prova.cotefacil.apipedidos.dtos.OrderUpdateDTO;
import br.com.prova.cotefacil.apipedidos.entities.enums.OrderStatus;
import br.com.prova.cotefacil.apipedidos.entities.orders.Order;
import br.com.prova.cotefacil.apipedidos.exceptions.BusinessException;
import br.com.prova.cotefacil.apipedidos.exceptions.NotFoundException;
import br.com.prova.cotefacil.apipedidos.repository.OrderRepository;
import br.com.prova.cotefacil.apipedidos.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - OrderService")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {

        when(securityUtils.getCurrentUsername())
                .thenReturn("user1");

        order = new Order();
        order.setId(1L);
        order.setCustomerName("João Silva");
        order.setCustomerEmail("joao@email.com");
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedBy("user1");

        List<OrderItemDTO> items = List.of(
                new OrderItemDTO(null, "Produto 1", 2, new BigDecimal("50.00"))
        );

        orderDTO = new OrderDTO(
                null,
                "João Silva",
                "joao@email.com",
                null,
                items,
                "user1"
        );
    }

    @Test
    @DisplayName("Deve listar pedidos com paginação")
    void shouldListOrdersWithPagination() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> page = new PageImpl<>(List.of(order), pageable, 1);

        when(orderRepository.findAllByStatusNotAndCreatedBy(
                OrderStatus.CANCELLED,
                pageable,
                "user1"
        )).thenReturn(page);

        Page<OrderDTO> result = orderService.listOrders(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(orderRepository).findAllByStatusNotAndCreatedBy(
                OrderStatus.CANCELLED,
                pageable,
                "user1"
        );
    }

    @Test
    @DisplayName("Deve buscar pedido por ID com sucesso")
    void shouldGetOrderByIdSuccessfully() {

        when(orderRepository.findByIdAndCreatedBy(1L, "user1"))
                .thenReturn(Optional.of(order));

        OrderDTO result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals("João Silva", result.customerName());

        verify(orderRepository).findByIdAndCreatedBy(1L, "user1");
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando pedido não existe")
    void shouldThrowNotFoundExceptionWhenOrderDoesNotExist() {

        when(orderRepository.findByIdAndCreatedBy(999L, "user1"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> orderService.getOrderById(999L));

        verify(orderRepository).findByIdAndCreatedBy(999L, "user1");
    }

    @Test
    @DisplayName("Deve criar pedido com sucesso")
    void shouldCreateOrderSuccessfully() {

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);

        OrderDTO result = orderService.createOrder(orderDTO);

        assertNotNull(result);

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve calcular total corretamente ao criar pedido")
    void shouldCalculateTotalCorrectly() {

        List<OrderItemDTO> items = List.of(
                new OrderItemDTO(null, "Produto 1", 2, new BigDecimal("50.00")),
                new OrderItemDTO(null, "Produto 2", 3, new BigDecimal("30.00"))
        );

        OrderDTO dto = new OrderDTO(
                null,
                "Maria",
                "maria@email.com",
                null,
                items,
                "user1"
        );

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order saved = invocation.getArgument(0);
                    assertEquals(new BigDecimal("190.00"), saved.getTotalAmount());
                    return saved;
                });

        orderService.createOrder(dto);

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao atualizar pedido entregue")
    void shouldThrowBusinessExceptionWhenUpdatingDeliveredOrder() {

        Order delivered = new Order();
        delivered.setId(1L);
        delivered.setStatus(OrderStatus.DELIVERED);
        delivered.setCreatedBy("user1");

        when(orderRepository.findByIdAndCreatedBy(1L, "user1"))
                .thenReturn(Optional.of(delivered));

        assertThrows(BusinessException.class, () ->
                orderService.updateOrder(1L,
                        new OrderUpdateDTO(null, null, OrderStatus.PENDING, null)
                )
        );
    }

    @Test
    @DisplayName("Deve cancelar pedido com sucesso")
    void shouldDeleteOrderSuccessfully() {

        Order pending = new Order();
        pending.setId(1L);
        pending.setStatus(OrderStatus.PENDING);
        pending.setCreatedBy("user1");

        when(orderRepository.findByIdAndCreatedBy(1L, "user1"))
                .thenReturn(Optional.of(pending));

        orderService.deleteOrder(1L);

        assertEquals(OrderStatus.CANCELLED, pending.getStatus());
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao cancelar pedido entregue")
    void shouldThrowBusinessExceptionWhenDeletingDeliveredOrder() {

        Order delivered = new Order();
        delivered.setId(1L);
        delivered.setStatus(OrderStatus.DELIVERED);
        delivered.setCreatedBy("user1");

        when(orderRepository.findByIdAndCreatedBy(1L, "user1"))
                .thenReturn(Optional.of(delivered));

        assertThrows(BusinessException.class,
                () -> orderService.deleteOrder(1L));
    }
}
