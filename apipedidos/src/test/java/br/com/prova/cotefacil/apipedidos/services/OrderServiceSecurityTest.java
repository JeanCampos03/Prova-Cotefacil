package br.com.prova.cotefacil.apipedidos.services;


import br.com.prova.cotefacil.apipedidos.dtos.OrderDTO;
import br.com.prova.cotefacil.apipedidos.dtos.OrderItemDTO;
import br.com.prova.cotefacil.apipedidos.entitys.orders.Order;
import br.com.prova.cotefacil.apipedidos.exceptions.BusinessException;
import br.com.prova.cotefacil.apipedidos.exceptions.NotFoundException;
import br.com.prova.cotefacil.apipedidos.repositorys.OrderRepository;
import br.com.prova.cotefacil.apipedidos.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceSecurityTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        // Simular usuário "user1" autenticado
        when(securityUtils.getCurrentUsername()).thenReturn("user1");
    }

    @Test
    void deveCriarPedidoComCreatedByDoUsuarioLogado() {
        // Arrange
        OrderItemDTO itemDTO = new OrderItemDTO(
                null,
                "Produto Teste",
                2,
                BigDecimal.valueOf(10.00)
        );

        OrderDTO orderDTO = new OrderDTO(
                null,
                "Cliente Teste",
                "cliente@test.com",
                null,
                List.of(itemDTO),
                "Cliente Teste"
        );

        when(orderRepository.save(any(Order.class))).thenAnswer(i -> {
            Order order = i.getArgument(0);
            order.setId(1L);
            return order;
        });

        // Act
        OrderDTO result = orderService.salvarPedido(orderDTO);

        // Assert
        assertNotNull(result);
        assertEquals("user1", result.createdBy());
        verify(orderRepository).save(argThat(order ->
                "user1".equals(order.getCreatedBy())
        ));
    }

    @Test
    void naoDevePermitirAcessoPedidoDeOutroUsuario() {
        // Arrange
        Long pedidoId = 1L;
        when(orderRepository.findByIdAndCreatedBy(pedidoId, "user1"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            orderService.listarPedidosPorId(pedidoId);
        });

        verify(orderRepository).findByIdAndCreatedBy(pedidoId, "user1");
    }

    @Test
    void naoDevePermitirAtualizarPedidoDeOutroUsuario() {
        // Arrange
        Long pedidoId = 1L;

        when(orderRepository.findByIdAndCreatedBy(pedidoId, "user1"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            orderService.atualizarPedido(pedidoId, null);
        });

        verify(orderRepository).findByIdAndCreatedBy(pedidoId, "user1");
    }

    @Test
    void naoDevePermitirExcluirPedidoDeOutroUsuario() {
        // Arrange
        Long pedidoId = 1L;
        when(orderRepository.findByIdAndCreatedBy(pedidoId, "user1"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            orderService.excluirPedido(pedidoId);
        });

        verify(orderRepository).findByIdAndCreatedBy(pedidoId, "user1");
    }
}
