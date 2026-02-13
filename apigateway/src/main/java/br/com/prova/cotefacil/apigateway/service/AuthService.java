package br.com.prova.cotefacil.apigateway.service;

import br.com.prova.cotefacil.apigateway.dto.AuthDTO;
import br.com.prova.cotefacil.apigateway.dto.LoginResponseDTO;
import br.com.prova.cotefacil.apigateway.dto.RegisteUserDTO;
import br.com.prova.cotefacil.apigateway.entities.User;
import br.com.prova.cotefacil.apigateway.entities.enums.UsuarioRole;
import br.com.prova.cotefacil.apigateway.exception.UserAlreadyExistsException;
import br.com.prova.cotefacil.apigateway.exception.UserNotFoundException;
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

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public LoginResponseDTO login(AuthDTO dadosLogin) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(dadosLogin.username(), dadosLogin.password());
            var auth = authenticationManager.authenticate(usernamePassword);

            return new LoginResponseDTO(tokenService.criarToken((User) auth.getPrincipal()));

        } catch (Exception e) {
            throw new UserNotFoundException();
        }
    }

    public LoginResponseDTO register(RegisteUserDTO dados) {
        if (userService.existeUsuario(dados.username())) {
            throw new UserAlreadyExistsException();
        }

        User user = new User(
                dados.username(),
                passwordEncoder.encode(dados.password()),
                UsuarioRole.USER
        );
        userService.salvar(user);

        return new LoginResponseDTO(tokenService.criarToken(user));
    }
}
