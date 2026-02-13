package br.com.prova.cotefacil.apipedidos.services;

import br.com.prova.cotefacil.apipedidos.dtos.OrderItemDTO;
import br.com.prova.cotefacil.apipedidos.dtos.OrderItemUpdateDTO;
import br.com.prova.cotefacil.apipedidos.entities.enums.OrderStatus;
import br.com.prova.cotefacil.apipedidos.entities.orders.Order;
import br.com.prova.cotefacil.apipedidos.entities.orders.OrderItem;
import br.com.prova.cotefacil.apipedidos.exceptions.NotFoundException;
import br.com.prova.cotefacil.apipedidos.repositorys.OrderRepository;
import br.com.prova.cotefacil.apipedidos.utils.SecurityUtils;
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

    @Mock
    private SecurityUtils securityUtils;


    @InjectMocks
    private OrderItemService orderItemService;

    private Order order;
    private OrderItem item;

    @BeforeEach
    void setUp() {
        when(securityUtils.getCurrentUsername())
                .thenReturn("teste");

        order = new Order();
        order.setId(1L);
        order.setCustomerName("João Silva");
        order.setCustomerEmail("joao@email.com");
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setItems(new ArrayList<>());
        order.setTotalAmount(BigDecimal.ZERO);
        order.setCreatedBy("teste");

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
    void shouldGetOrderItemsSuccessfully() {

        when(orderRepository.findByIdAndCreatedBy(1L, "teste"))
                .thenReturn(Optional.of(order));

        List<OrderItemDTO> result =
                orderItemService.buscarItensPorPedido(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Produto 1", result.get(0).productName());

        verify(orderRepository, times(1))
                .findByIdAndCreatedBy(1L, "teste");
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando pedido não existe")
    void shouldThrowNotFoundExceptionWhenOrderDoesNotExist() {

        when(orderRepository.findByIdAndCreatedBy(999L, "teste"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                orderItemService.buscarItensPorPedido(999L)
        );

        verify(orderRepository, times(1))
                .findByIdAndCreatedBy(999L, "teste");
    }

    @Test
    @DisplayName("Deve adicionar item ao pedido com sucesso")
    void shouldAddItemToOrderSuccessfully() {

        OrderItemUpdateDTO novoItem = new OrderItemUpdateDTO(
                null,
                "Produto 2",
                3,
                new BigDecimal("30.00")
        );

        when(orderRepository.findByIdAndCreatedBy(1L, "teste"))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);

        List<OrderItemDTO> result =
                orderItemService.adicionaItemPedido(
                        List.of(novoItem), 1L
                );

        assertNotNull(result);

        verify(orderRepository, times(1))
                .findByIdAndCreatedBy(1L, "teste");

        verify(orderRepository, times(1))
                .save(any(Order.class));
    }
}

