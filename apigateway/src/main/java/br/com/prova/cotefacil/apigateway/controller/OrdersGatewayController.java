package br.com.prova.cotefacil.apigateway.controller;

import br.com.prova.cotefacil.apigateway.service.OrdersGatewayService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Hidden
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrdersGatewayController {

    private final OrdersGatewayService gatewayService;

    @RequestMapping(
            value = { "", "/", "/**" },
            method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH }
    )
    public ResponseEntity<?> proxyRequest(
            HttpServletRequest request,
            @RequestBody(required = false) String body
    ) {
        return gatewayService.proxyToApiPedidos(request, body);
    }
}
