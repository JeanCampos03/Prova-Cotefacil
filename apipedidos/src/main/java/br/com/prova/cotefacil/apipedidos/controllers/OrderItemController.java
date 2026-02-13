package br.com.prova.cotefacil.apipedidos.controllers;

import br.com.prova.cotefacil.apipedidos.dtos.OrderItemDTO;
import br.com.prova.cotefacil.apipedidos.dtos.OrderItemUpdateDTO;
import br.com.prova.cotefacil.apipedidos.exceptions.RestMensage;
import br.com.prova.cotefacil.apipedidos.services.OrderItemService;
import br.com.prova.cotefacil.apipedidos.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
@Validated
@Tag(name= "Items do pedido")
public class OrderItemController {

    private final OrderItemService orderItemService;
    private final OrderService orderService;


    // GET /api/orders/{id}/items – Listar itens de um pedido
    @GetMapping("/{id}/items")
    @Operation(summary = "Listar produtos do pedido")
    public ResponseEntity<RestMensage> listarItensPedido(@PathVariable Long id) {
        List<OrderItemDTO> itens = orderItemService.buscarItensPorPedido(id);
        return ResponseEntity.ok(new RestMensage(HttpStatus.OK, itens, LocalDateTime.now()));
    }


    // POST /api/orders/{id}/items – Adicionar item ao pedido
    @PostMapping("/{id}/items")
    @Operation(summary = "Adicionar produtos ao pedido")
    public ResponseEntity<RestMensage> adicionarItemPedido(@RequestBody @Valid List< @Valid OrderItemUpdateDTO> orderItemUpdateDTO, @PathVariable Long id) {
        List<OrderItemDTO> itensCriados = orderItemService.adicionaItemPedido(orderItemUpdateDTO, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(new RestMensage(HttpStatus.CREATED, itensCriados, LocalDateTime.now()));
    }
}
