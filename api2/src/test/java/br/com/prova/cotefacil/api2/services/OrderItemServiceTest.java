package br.com.prova.cotefacil.api2.services;

import br.com.prova.cotefacil.api2.dtos.OrderItemDTO;
import br.com.prova.cotefacil.api2.dtos.OrderItemUpdateDTO;
import br.com.prova.cotefacil.api2.entitys.enums.OrderStatus;
import br.com.prova.cotefacil.api2.entitys.orders.Order;
import br.com.prova.cotefacil.api2.entitys.orders.OrderItem;
import br.com.prova.cotefacil.api2.exceptions.NotFoundException;
import br.com.prova.cotefacil.api2.repositorys.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - OrderItemService")
class OrderItemServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderItemService orderItemService;

    private Order order;
    private OrderItem item;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        order.setCustomerName("João Silva");
        order.setCustomerEmail("joao@email.com");
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setItems(new ArrayList<>());

        item = new OrderItem();
        item.setId(1L);
        item.setProductName("Produto 1");
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("50.00"));
        item.setSubtotal(new BigDecimal("100.00"));
        item.setOrder(order);

        order.getItems().add(item);
    }

    @Test
    @DisplayName("Deve buscar itens de um pedido com sucesso")
    void deveBuscarItensDePedidoComSucesso() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        List<OrderItemDTO> result = orderItemService.buscarItensPorPedido(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Produto 1", result.get(0).productName());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando pedido não existe")
    void deveLancarNotFoundExceptionQuandoPedidoNaoExiste() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            orderItemService.buscarItensPorPedido(999L);
        });

        verify(orderRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve adicionar item ao pedido com sucesso")
    void deveAdicionarItemAoPedidoComSucesso() {
        // Arrange
        OrderItemUpdateDTO novoItem = new OrderItemUpdateDTO(
            null,
            "Produto 2",
            3,
            new BigDecimal("30.00")
        );

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        List<OrderItemDTO> result = orderItemService.adicionaItemPedido(
            List.of(novoItem), 1L
        );

        // Assert
        assertNotNull(result);
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }
}
