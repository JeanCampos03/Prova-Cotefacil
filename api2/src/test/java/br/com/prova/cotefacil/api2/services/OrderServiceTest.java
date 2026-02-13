package br.com.prova.cotefacil.api2.services;

import br.com.prova.cotefacil.api2.dtos.OrderDTO;
import br.com.prova.cotefacil.api2.dtos.OrderItemDTO;
import br.com.prova.cotefacil.api2.dtos.OrderUpdateDTO;
import br.com.prova.cotefacil.api2.entitys.enums.OrderStatus;
import br.com.prova.cotefacil.api2.entitys.orders.Order;
import br.com.prova.cotefacil.api2.exceptions.BusinessException;
import br.com.prova.cotefacil.api2.exceptions.NotFoundException;
import br.com.prova.cotefacil.api2.repositorys.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        order.setCustomerName("João Silva");
        order.setCustomerEmail("joao@email.com");
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItemDTO> items = List.of(
            new OrderItemDTO(null, "Produto 1", 2, new BigDecimal("50.00"))
        );

        orderDTO = new OrderDTO(
            null,
            "João Silva",
            "joao@email.com",
            null,
            items
        );
    }

    @Test
    @DisplayName("Deve listar pedidos com paginação")
    void deveListarPedidosComPaginacao() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Order> orders = List.of(order);
        Page<Order> page = new PageImpl<>(orders, pageable, 1);

        when(orderRepository.findAllByStatusNot(OrderStatus.CANCELLED, pageable))
            .thenReturn(page);

        // Act
        Page<OrderDTO> result = orderService.listarPedidos(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(orderRepository, times(1))
            .findAllByStatusNot(OrderStatus.CANCELLED, pageable);
    }

    @Test
    @DisplayName("Deve buscar pedido por ID com sucesso")
    void deveBuscarPedidoPorIdComSucesso() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        OrderDTO result = orderService.listarPedidosPorId(1L);

        // Assert
        assertNotNull(result);
        assertEquals("João Silva", result.customerName());
        assertEquals("joao@email.com", result.customerEmail());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando pedido não existe")
    void deveLancarNotFoundExceptionQuandoPedidoNaoExiste() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            orderService.listarPedidosPorId(999L);
        });

        verify(orderRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve criar pedido com sucesso")
    void deveCriarPedidoComSucesso() {
        // Arrange
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        Order result = orderService.salvarPedido(orderDTO);

        // Assert
        assertNotNull(result);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve calcular total corretamente ao criar pedido")
    void deveCalcularTotalCorretamente() {
        // Arrange
        List<OrderItemDTO> items = List.of(
            new OrderItemDTO(null,"Produto 1", 2, new BigDecimal("50.00")),
            new OrderItemDTO(null,"Produto 2", 3, new BigDecimal("30.00"))
        );

        OrderDTO dtoComItens = new OrderDTO(
            null,
            "Maria",
            "maria@email.com",
            null,
            items
        );

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            assertEquals(new BigDecimal("190.00"), savedOrder.getTotalAmount());
            return savedOrder;
        });

        // Act
        orderService.salvarPedido(dtoComItens);

        // Assert
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao tentar atualizar pedido entregue")
    void deveLancarBusinessExceptionAoAtualizarPedidoEntregue() {
        // Arrange
        Order pedidoEntregue = new Order();
        pedidoEntregue.setId(1L);
        pedidoEntregue.setStatus(OrderStatus.DELIVERED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(pedidoEntregue));

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            orderService.atualizarPedido(1L, 
                new OrderUpdateDTO(
                    null, null, OrderStatus.PENDING, null
                )
            );
        });
    }

    @Test
    @DisplayName("Deve excluir pedido (cancelar) com sucesso")
    void deveExcluirPedidoComSucesso() {
        // Arrange
        Order pedidoPendente = new Order();
        pedidoPendente.setId(1L);
        pedidoPendente.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(pedidoPendente));

        // Act
        orderService.excluirPedido(1L);

        // Assert
        verify(orderRepository, times(1)).findById(1L);
        assertEquals(OrderStatus.CANCELLED, pedidoPendente.getStatus());
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao tentar excluir pedido entregue")
    void deveLancarBusinessExceptionAoExcluirPedidoEntregue() {
        // Arrange
        Order pedidoEntregue = new Order();
        pedidoEntregue.setId(1L);
        pedidoEntregue.setStatus(OrderStatus.DELIVERED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(pedidoEntregue));

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            orderService.excluirPedido(1L);
        });
    }
}
