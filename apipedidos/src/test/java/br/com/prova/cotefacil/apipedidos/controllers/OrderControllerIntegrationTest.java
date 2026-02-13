package br.com.prova.cotefacil.apipedidos.controllers;

import br.com.prova.cotefacil.apigateway.service.UsuarioService;
import br.com.prova.cotefacil.apipedidos.entities.enums.OrderStatus;
import br.com.prova.cotefacil.apipedidos.entities.orders.Order;
import br.com.prova.cotefacil.apipedidos.entities.orders.OrderItem;
import br.com.prova.cotefacil.apipedidos.repositorys.OrderRepository;
import br.com.prova.cotefacil.apipedidos.services.authService.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - OrderController")
class OrderControllerIntegrationTest {

    @MockBean
    private UsuarioService usuarioService;

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
        token = tokenService.criarToken("testuser");
    }

    @Test
    @DisplayName("Deve criar pedido com sucesso")
    void shouldCreateOrderSuccessfully() throws Exception {

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

        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201));
    }

    @Test
    @DisplayName("Deve retornar 401 ao criar pedido sem autenticação")
    void shouldReturn401WithoutAuthentication() throws Exception {

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

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve listar pedidos com paginação")
    void shouldListOrdersWithPagination() throws Exception {

        createTestOrder();

        mockMvc.perform(get("/api/orders?page=0&size=10")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").exists());
    }

    @Test
    @DisplayName("Deve buscar pedido por ID com sucesso")
    void shouldGetOrderByIdSuccessfully() throws Exception {

        Order pedido = createTestOrder();

        mockMvc.perform(get("/api/orders/" + pedido.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("João Silva"))
                .andExpect(jsonPath("$.customerEmail").value("joao@email.com"));
    }

    @Test
    @DisplayName("Deve retornar 404 quando pedido não existe")
    void shouldReturn404WhenOrderDoesNotExist() throws Exception {

        mockMvc.perform(get("/api/orders/999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar pedido com sucesso")
    void shouldUpdateOrderSuccessfully() throws Exception {

        Order pedido = createTestOrder();
        Long itemId = pedido.getItems().get(0).getId(); // ✅ ID real

        String updateJson = """
                {
                    "itemId": %d,
                    "status": "CONFIRMED"
                }
                """.formatted(itemId);

        mockMvc.perform(put("/api/orders/" + pedido.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("Deve excluir pedido com sucesso")
    void shouldDeleteOrderSuccessfully() throws Exception {

        Order pedido = createTestOrder();

        mockMvc.perform(delete("/api/orders/" + pedido.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve validar dados de entrada")
    void shouldValidateInputData() throws Exception {

        String orderJsonInvalido = """
                {
                    "customerName": "",
                    "customerEmail": "email-invalido"
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJsonInvalido))
                .andExpect(status().isBadRequest());
    }

        private Order createTestOrder () {

            Order order = new Order();
            order.setCustomerName("João Silva");
            order.setCustomerEmail("joao@email.com");
            order.setOrderDate(LocalDateTime.now());
            order.setStatus(OrderStatus.PENDING);
            order.setTotalAmount(new BigDecimal("100.00"));
            order.setCreatedBy("testuser");
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

