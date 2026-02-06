package br.com.cotefacil.prova.controllers;

import br.com.cotefacil.prova.dtos.AuthDTO;
import br.com.cotefacil.prova.dtos.RegisterDTO;
import br.com.cotefacil.prova.entitys.users.Usuario;
import br.com.cotefacil.prova.entitys.users.UsuarioRole;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthDTO dadosAutenticacao) {
        var access = new UsernamePasswordAuthenticationToken(dadosAutenticacao.username(), dadosAutenticacao.password());

        var auth = this.authenticationManager.authenticate(access);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO dadosAutenticacao) {
        return ResponseEntity.ok().build();
    }

}
