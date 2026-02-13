package br.com.prova.cotefacil.apigateway.service;

import br.com.prova.cotefacil.apigateway.dto.AuthDTO;
import br.com.prova.cotefacil.apigateway.dto.LoginResponseDTO;
import br.com.prova.cotefacil.apigateway.dto.RegistroUsuarioDTO;
import br.com.prova.cotefacil.apigateway.entity.Usuario;
import br.com.prova.cotefacil.apigateway.entity.UsuarioRole;
import br.com.prova.cotefacil.apigateway.exception.UsuarioExistenteException;
import br.com.prova.cotefacil.apigateway.exception.UsuarioNaoEncontradoException;
import br.com.prova.cotefacil.apigateway.security.TokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService;

    private final UsuarioService usuarioService;

    private final PasswordEncoder passwordEncoder;

    public LoginResponseDTO login(AuthDTO dadosLogin) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(dadosLogin.username(), dadosLogin.password());
            var auth = authenticationManager.authenticate(usernamePassword);

            return new LoginResponseDTO(tokenService.criarToken((Usuario) auth.getPrincipal()));

        } catch (Exception e) {
            throw new UsuarioNaoEncontradoException();
        }
    }

    public LoginResponseDTO register(RegistroUsuarioDTO dados) {
        if (usuarioService.existeUsuario(dados.username())) {
            throw new UsuarioExistenteException();
        }

        Usuario usuario = new Usuario(
                dados.username(),
                passwordEncoder.encode(dados.password()),
                UsuarioRole.USER
        );
        usuarioService.salvar(usuario);

        return new LoginResponseDTO(tokenService.criarToken(usuario));
    }
}
