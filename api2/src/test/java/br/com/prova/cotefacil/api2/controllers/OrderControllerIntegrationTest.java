package br.com.prova.cotefacil.api2.controllers;

import br.com.prova.cotefacil.api2.entitys.enums.OrderStatus;
import br.com.prova.cotefacil.api2.entitys.orders.Order;
import br.com.prova.cotefacil.api2.entitys.orders.OrderItem;
import br.com.prova.cotefacil.api2.repositorys.OrderRepository;
import br.com.prova.cotefacil.api2.services.authService.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - OrderController")
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TokenService tokenService;

    private String token;

    @BeforeEach
    void setUp() {
        // Token como se viesse da API 1 (gateway)
        token = tokenService.criarToken("testuser");
    }

    @Test
    @DisplayName("Deve criar pedido com sucesso")
    void deveCriarPedidoComSucesso() throws Exception {
        // Arrange
        String orderJson = """
                {
                    "customerName": "João Silva",
                    "customerEmail": "joao@email.com",
                    "items": [
                        {
                            "productName": "Produto 1",
                            "quantity": 2,
                            "unitPrice": 50.00
                        }
                    ]
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201));
    }

    @Test
    @DisplayName("Deve retornar 401 ao criar pedido sem autenticação")
    void deveRetornar401SemAutenticacao() throws Exception {
        // Arrange
        String orderJson = """
                {
                    "customerName": "João Silva",
                    "customerEmail": "joao@email.com",
                    "items": [
                        {
                            "productName": "Produto 1",
                            "quantity": 2,
                            "unitPrice": 50.00
                        }
                    ]
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve listar pedidos com paginação")
    void deveListarPedidosComPaginacao() throws Exception {
        // Arrange
        criarPedidoTeste();

        // Act & Assert
        mockMvc.perform(get("/api/orders?page=0&size=10")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").exists());
    }

    @Test
    @DisplayName("Deve buscar pedido por ID com sucesso")
    void deveBuscarPedidoPorIdComSucesso() throws Exception {
        // Arrange
        Order pedido = criarPedidoTeste();

        // Act & Assert
        mockMvc.perform(get("/api/orders/" + pedido.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("João Silva"))
                .andExpect(jsonPath("$.customerEmail").value("joao@email.com"));
    }

    @Test
    @DisplayName("Deve retornar 404 quando pedido não existe")
    void deveRetornar404QuandoPedidoNaoExiste() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/orders/999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar pedido com sucesso")
    void deveAtualizarPedidoComSucesso() throws Exception {
        // Arrange
        Order pedido = criarPedidoTeste();
        String updateJson = """
                    {
                        "itemId":76,
                                    "status": "CONFIRMED"
                                }
                """;

        // Act & Assert
        mockMvc.perform(put("/api/orders/" + pedido.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value(200));
    }

    @Test
    @DisplayName("Deve excluir pedido com sucesso")
    void deveExcluirPedidoComSucesso() throws Exception {
        // Arrange
        Order pedido = criarPedidoTeste();

        // Act & Assert
        mockMvc.perform(delete("/api/orders/" + pedido.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve validar dados de entrada")
    void deveValidarDadosDeEntrada() throws Exception {
        // Arrange - JSON sem campos obrigatórios
        String orderJsonInvalido = """
                {
                    "customerName": "",
                    "customerEmail": "email-invalido"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJsonInvalido))
                .andExpect(status().isInternalServerError());
    }

    private Order criarPedidoTeste() {
        Order order = new Order();
        order.setCustomerName("João Silva");
        order.setCustomerEmail("joao@email.com");
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("100.00"));
        order.setItems(new ArrayList<>());

        OrderItem item = new OrderItem();
        item.setProductName("Produto 1");
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("50.00"));
        item.setSubtotal(new BigDecimal("100.00"));
        item.setOrder(order);

        order.getItems().add(item);
        return orderRepository.save(order);
    }
}
