package br.com.prova.cotefacil.apigateway.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Enumeration;

@Service
public class OrdersGatewayService {


    private final RestTemplate restTemplate;

    @Value("${api2.url:http://localhost:8082}")
    private String api2BaseUrl;

    public OrdersGatewayService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> proxyToApiPedidos(HttpServletRequest request, String body) {
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
                if (!name.equalsIgnoreCase(HttpHeaders.AUTHORIZATION) &&
                        !name.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)) {
                    headers.addAll(name, Collections.list(request.getHeaders(name)));
                }
            }
        }
    }
}
