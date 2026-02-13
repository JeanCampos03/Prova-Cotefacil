package br.com.prova.cotefacil.apigateway.controller;

import br.com.prova.cotefacil.apigateway.dto.ApiResponseDTO;
import br.com.prova.cotefacil.apigateway.dto.AuthDTO;
import br.com.prova.cotefacil.apigateway.dto.LoginResponseDTO;
import br.com.prova.cotefacil.apigateway.dto.RegisteUserDTO;
import br.com.prova.cotefacil.apigateway.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("auth")
@Tag(name = "Autenticação")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Gerar Token de autenticação")
    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> login(@RequestBody @Valid AuthDTO dadosLogin) {
        var token = authService.login(dadosLogin);
        return ResponseEntity.ok(new ApiResponseDTO<>(200, token, LocalDateTime.now()));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário")
    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> register(@RequestBody @Valid RegisteUserDTO dados) {
        var token = authService.register(dados);
        return ResponseEntity.status(201).body(new ApiResponseDTO<>(201, token, LocalDateTime.now()));
    }
}



