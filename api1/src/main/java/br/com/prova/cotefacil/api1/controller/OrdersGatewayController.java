package br.com.prova.cotefacil.api1.controller;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Enumeration;

/**
 * Gateway que encaminha requisições /api/orders/** para a API 2 (CRUD de Pedidos).
 * Inclui o token JWT no header Authorization ao encaminhar.
 * Oculto do Swagger da API 1: use o Swagger da API 2 para documentação de pedidos.
 */
@Hidden
@RestController
@RequestMapping("/api/orders")
public class OrdersGatewayController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${api2.url:http://localhost:8082}")
    private String api2BaseUrl;

    @RequestMapping(value = { "", "/", "/**" }, method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH })
    public ResponseEntity<?> proxyRequest(HttpServletRequest request, @RequestBody(required = false) String body) {
        String path = extractPath(request);
        String queryString = request.getQueryString() != null ? "?" + request.getQueryString() : "";
        String targetUrl = api2BaseUrl + "/api/orders" + path + queryString;

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || authHeader.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"status\":401,\"mensagem\":\"Token JWT obrigatório\"}");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);
        copyRequestHeaders(request, headers);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        try {
            ResponseEntity<String> response = restTemplate.exchange(targetUrl, method, entity, String.class);
            return ResponseEntity.status(response.getStatusCode())
                    .headers(response.getHeaders())
                    .body(response.getBody());
        } catch (HttpStatusCodeException e) {
            // Repassa status e corpo da resposta de erro da API 2 (4xx/5xx)
            String bodyReturn = e.getResponseBodyAsString();
            return ResponseEntity.status(e.getStatusCode())
                    .headers(e.getResponseHeaders())
                    .body(bodyReturn != null && !bodyReturn.isBlank() ? bodyReturn : e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("{\"status\":502,\"mensagem\":\"Erro ao comunicar com API de Pedidos: " + e.getMessage() + "\"}");
        }
    }

    private String extractPath(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        int ordersIndex = requestUri.indexOf("/api/orders");
        if (ordersIndex >= 0) {
            String path = requestUri.substring(ordersIndex + "/api/orders".length());
            return path.isEmpty() ? "" : path;
        }
        return requestUri;
    }

    private void copyRequestHeaders(HttpServletRequest request, HttpHeaders headers) {
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                if (!name.equalsIgnoreCase(HttpHeaders.AUTHORIZATION) && !name.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)) {
                    headers.addAll(name, Collections.list(request.getHeaders(name)));
                }
            }
        }
    }
}
