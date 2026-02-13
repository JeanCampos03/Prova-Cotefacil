package br.com.prova.cotefacil.api1.controller;

import br.com.prova.cotefacil.api1.dto.ApiResponseDTO;
import br.com.prova.cotefacil.api1.dto.AuthDTO;
import br.com.prova.cotefacil.api1.dto.LoginResponseDTO;
import br.com.prova.cotefacil.api1.dto.RegistroUsuarioDTO;
import br.com.prova.cotefacil.api1.entity.Usuario;
import br.com.prova.cotefacil.api1.entity.UsuarioRole;
import br.com.prova.cotefacil.api1.exception.UsuarioNaoEncontradoException;
import br.com.prova.cotefacil.api1.security.TokenService;
import br.com.prova.cotefacil.api1.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("auth")
@Tag(name = "Autenticação")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    TokenService tokenService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "Gerar Token de autenticação")
    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> login(@RequestBody @Valid AuthDTO dadosLogin) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(dadosLogin.username(), dadosLogin.password());
            var auth = authenticationManager.authenticate(usernamePassword);

            var token = tokenService.criarToken((Usuario) auth.getPrincipal());
            ApiResponseDTO<LoginResponseDTO> responseToken = new ApiResponseDTO<>(200, new LoginResponseDTO(token), LocalDateTime.now());
            return ResponseEntity.ok(responseToken);

        } catch (Exception e) {
            throw new UsuarioNaoEncontradoException();
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário")
    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> register(@RequestBody @Valid RegistroUsuarioDTO dados) {
        if (usuarioService.existeUsuario(dados.username())) {
            throw new br.com.prova.cotefacil.api1.exception.UsuarioExistenteException();
        }
        Usuario usuario = new Usuario(dados.username(), passwordEncoder.encode(dados.password()), UsuarioRole.USER);
        usuarioService.salvar(usuario);

        var token = tokenService.criarToken(usuario);
        ApiResponseDTO<LoginResponseDTO> response = new ApiResponseDTO<>(201, new LoginResponseDTO(token), LocalDateTime.now());
        return ResponseEntity.status(201).body(response);
    }
}


