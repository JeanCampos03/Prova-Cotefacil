package br.com.cotefacil.prova;

import br.com.cotefacil.prova.dtos.order.OrderDTO;
import br.com.cotefacil.prova.dtos.order.OrderItemDTO;
import br.com.cotefacil.prova.dtos.order.OrderUpdateDTO;
import br.com.cotefacil.prova.entitys.enums.OrderStatus;
import br.com.cotefacil.prova.entitys.orders.Order;
import br.com.cotefacil.prova.exceptions.BusinessException;
import br.com.cotefacil.prova.exceptions.NotFoundException;
import br.com.cotefacil.prova.repositorys.OrderRepository;
import br.com.cotefacil.prova.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        orderDTO = OrderDTO.builder()
                .customerName("Jean")
                .customerEmail("jean@email.com")
                .items(List.of(
                        new OrderItemDTO(null, "Produto A", 2, new BigDecimal("50.0"), new BigDecimal("100.0"))
                ))
                .build();
    }

    // -------------------------------
    // Teste Unitário: salvarPedido
    // Given: um DTO válido
    // When: chamar salvarPedido
    // Then: pedido é salvo e total calculado
    // -------------------------------
    @Test
    void salvarPedido_DeveRetornarPedidoComTotalCorreto() {
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = orderService.salvarPedido(orderDTO);

        verify(orderRepository).save(captor.capture());
        Order captured = captor.getValue();

        assertEquals("Jean", captured.getCustomerName());
        assertEquals("jean@email.com", captured.getCustomerEmail());
        assertEquals(OrderStatus.PENDING, captured.getStatus());
        assertEquals(new BigDecimal("100.0"), captured.getTotalAmount());
    }

    // -------------------------------
    // Teste Unitário: listarPedidosPorId
    // Given: pedido existe
    // When: buscar por id
    // Then: retorna DTO
    // -------------------------------
    @Test
    void listarPedidosPorId_DeveRetornarPedidoDTO() {
        Order order = new Order();
        order.setId(1L);
        order.setCustomerName("Jean");
        order.setCustomerEmail("jean@email.com");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderDTO result = orderService.listarPedidosPorId(1L);

        assertEquals("Jean", result.customerName());
        assertEquals("jean@email.com", result.customerEmail());
    }

    // -------------------------------
    // Teste Unitário: listarPedidosPorId não encontrado
    // -------------------------------
    @Test
    void listarPedidosPorId_NaoEncontrado_DeveLancarNotFoundException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.listarPedidosPorId(1L));
    }

    // -------------------------------
    // Teste Unitário: atualizarPedido com status DELIVERED
    // -------------------------------
    @Test
    void atualizarPedido_ComStatusEntregue_DeveLancarBusinessException() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.DELIVERED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderUpdateDTO updateDTO = new OrderUpdateDTO(null, null, OrderStatus.CANCELLED, null);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                orderService.atualizarPedido(1L, updateDTO)
        );

        assertTrue(exception.getMessage().contains("não pode ser alterado"));
    }

    // -------------------------------
    // Teste Unitário: excluirPedido já entregue
    // -------------------------------
    @Test
    void excluirPedido_ComStatusDelivered_DeveLancarBusinessException() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.DELIVERED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                orderService.excluirPedido(1L)
        );

        assertEquals("Pedido já foi entregue, não pode ser excluido", exception.getMessage());
    }

    // -------------------------------
    // Teste Unitário: listarPedidos com paginação
    // -------------------------------
    @Test
    void listarPedidos_DeveRetornarPaginaComPedidos() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);

        Page<Order> page = new PageImpl<>(List.of(order));

        when(orderRepository.findAllByStatusNot(OrderStatus.CANCELLED, Pageable.unpaged()))
                .thenReturn(page);

        Page<OrderDTO> result = orderService.listarPedidos(Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0));
    }
}
