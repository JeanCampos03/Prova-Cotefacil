package br.com.cotefacil.prova;

import br.com.cotefacil.prova.dtos.order.OrderDTO;
import br.com.cotefacil.prova.dtos.order.OrderItemDTO;
import br.com.cotefacil.prova.entitys.users.Usuario;
import br.com.cotefacil.prova.services.authService.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService; // serviço que gera tokens

    private String token;

    @BeforeEach
    void setup() {
        token = gerarToken();
    }


    // -------------------------------
    // Teste Integração: Criar pedido
    // Given: DTO válido
    // When: POST /api/orders
    // Then: retorna 201 Created com id do pedido
    // -------------------------------
    @Test
    void criarPedido_DeveRetornar201() throws Exception {
        OrderDTO orderDTO = OrderDTO.builder()
                .customerName("Jean")
                .customerEmail("jean@email.com")
                .items(List.of(
                        new OrderItemDTO(null, "Produto A", 2, new BigDecimal("50.0"), new BigDecimal("100.0"))
                ))
                .build();

        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensagem.idOrder").exists())
                .andExpect(jsonPath("$.mensagem.customerEmail").value("jean@email.com"));
    }

    // -------------------------------
    // Teste Integração: Listar pedidos
    // -------------------------------
    @Test
    void listarPedidos_DeveRetornarPagina() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }


    private String gerarToken() {
        var usernamePassword =
                new UsernamePasswordAuthenticationToken("usuario", "senha123");

        var auth = authenticationManager.authenticate(usernamePassword);

        return tokenService.criarToken((Usuario) auth.getPrincipal());
    }
}
