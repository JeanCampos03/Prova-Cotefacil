package br.com.prova.cotefacil.apipedidos.controllers;

import br.com.prova.cotefacil.apipedidos.dtos.OrderDTO;
import br.com.prova.cotefacil.apipedidos.dtos.OrderUpdateDTO;
import br.com.prova.cotefacil.apipedidos.exceptions.RestMensage;
import br.com.prova.cotefacil.apipedidos.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
@Validated
@Tag(name = "Pedidos")
public class OrderController {

    private final OrderService orderService;

    // GET /api/orders – Listar todos os pedidos (com paginação)
    @GetMapping
    @Operation(summary = "Listar pedidos", security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<Page<OrderDTO>> listarPedidos(Pageable pageable) {
        Page<OrderDTO> pedidos = orderService.listarPedidos(pageable);
        return ResponseEntity.ok(pedidos);
    }

    // GET /api/orders/{id} – Buscar pedido por ID
    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID", security = @SecurityRequirement(name = "bearer-jwt"))
    public OrderDTO listarPedidosPorId(@PathVariable @Positive Long id) {
        return orderService.listarPedidosPorId(id);
    }

    // POST /api/orders – Criar novo pedido
    @PostMapping
    @Operation(summary = "Criar novo pedido", security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<RestMensage> salvarPedido(@RequestBody @Valid OrderDTO dto) {
        OrderDTO response = orderService.salvarPedido(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new RestMensage(HttpStatus.CREATED, response, LocalDateTime.now()));

    }

    // PUT /api/orders/{id} – Atualizar pedido
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pedido", security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<RestMensage> atualizarPedido(@PathVariable Long id, @RequestBody @Valid OrderUpdateDTO itemDTO) {
        OrderDTO response = orderService.atualizarPedido(id, itemDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new RestMensage(HttpStatus.OK, response, LocalDateTime.now()));
    }

    // DELETE /api/orders/{id} – Deletar pedido
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir pedido (exclusão lógica)", security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<RestMensage> deletarPedido(@PathVariable Long id) {
        orderService.excluirPedido(id);
        return ResponseEntity.status(HttpStatus.OK).body(new RestMensage(HttpStatus.NO_CONTENT, "Pedido ID '" + id + "' excluído com sucesso",LocalDateTime.now()));
    }

}
