package br.com.cotefacil.prova.controllers;

import br.com.cotefacil.prova.dtos.ApiResponse;
import br.com.cotefacil.prova.dtos.AuthDTO;
import br.com.cotefacil.prova.dtos.LoginResponseDTO;
import br.com.cotefacil.prova.dtos.RegisterDTO;
import br.com.cotefacil.prova.entitys.users.Usuario;
import br.com.cotefacil.prova.exceptions.UsuarioExistenteException;
import br.com.cotefacil.prova.exceptions.UsuarioNaoEncontradoException;
import br.com.cotefacil.prova.services.UsuarioService;
import br.com.cotefacil.prova.services.authService.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthDTO dadosLogin) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(dadosLogin.username(), dadosLogin.password());
            var auth = authenticationManager.authenticate(usernamePassword);

            var token = tokenService.criarToken((Usuario) auth.getPrincipal());
            return ResponseEntity.ok(new LoginResponseDTO(token));

        } catch (Exception e) {
            throw new UsuarioNaoEncontradoException();
        }
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO dadosRegistro) {

        if (usuarioService.existeUsuario(dadosRegistro.username())) throw new UsuarioExistenteException();

        String senhaEncriptada = new BCryptPasswordEncoder().encode(dadosRegistro.password());

        usuarioService.salvar(new Usuario(dadosRegistro.username(), senhaEncriptada, dadosRegistro.role()));

        ApiResponse<String> response = new ApiResponse<>(200, "Registro realizado com sucesso!");
        return ResponseEntity.ok(response);

    }

}


