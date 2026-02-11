package br.com.cotefacil.prova.controllers;

import br.com.cotefacil.prova.dtos.order.OrderDTO;
import br.com.cotefacil.prova.dtos.order.OrderItemUpdateDTO;
import br.com.cotefacil.prova.dtos.order.OrderResponseDTO;
import br.com.cotefacil.prova.dtos.order.OrderUpdateDTO;
import br.com.cotefacil.prova.entitys.orders.Order;
import br.com.cotefacil.prova.exceptions.RestMensagem;
import br.com.cotefacil.prova.services.OrderService;
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
public class OrderController {

    private final OrderService orderService;

    // GET /api/orders – Listar todos os pedidos (com paginação)
    @GetMapping
    public Page<OrderDTO> listarPedidos(Pageable pageable) {
        return orderService.listarPedidos(pageable);
    }

    // GET /api/orders/{id} – Buscar pedido por ID
    @GetMapping("/{id}")
    public OrderDTO listarPedidosPorId(@PathVariable @Positive Long id) {
        return orderService.listarPedidosPorId(id);
    }

    // POST /api/orders – Criar novo pedido
    @PostMapping
    public ResponseEntity<RestMensagem> salvarPedido(@RequestBody @Valid OrderDTO dto) {
        Order order = orderService.salvarPedido(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new RestMensagem(HttpStatus.CREATED,
                        OrderResponseDTO.builder()
                                .idOrder(order.getId())
                                .customerEmail(order.getCustomerEmail())
                                .status(order.getStatus())
                                .build()
                        , LocalDateTime.now()));

    }

    // PUT /api/orders/{id} – Atualizar pedido
    @PutMapping("/{id}")
    public ResponseEntity<RestMensagem> atualizarPedido(@PathVariable Long id, @RequestBody @Valid OrderUpdateDTO itemDTO) {
        Order order = orderService.atualizarPedido(id, itemDTO);

        return ResponseEntity.status(HttpStatus.OK).body(
                new RestMensagem(HttpStatus.OK,
                        OrderResponseDTO.builder()
                                .idOrder(order.getId())
                                .customerEmail(order.getCustomerEmail())
                                .status(order.getStatus())
                                .build()
                        , LocalDateTime.now()));
    }


    // DELETE /api/orders/{id} – Deletar pedido
    @DeleteMapping("/{id}")
    public ResponseEntity<RestMensagem> deletarPedido(@PathVariable Long id) {
        orderService.excluirPedido(id);
        return ResponseEntity.status(HttpStatus.OK).body(new RestMensagem(HttpStatus.NO_CONTENT, "Pedido ID '" + id + "' excluído com sucesso",LocalDateTime.now()));
    }

}
