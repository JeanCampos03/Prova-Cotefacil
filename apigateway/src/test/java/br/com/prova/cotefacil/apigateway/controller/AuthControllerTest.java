package br.com.prova.cotefacil.apigateway.controller;

import br.com.prova.cotefacil.apigateway.dto.AuthDTO;
import br.com.prova.cotefacil.apigateway.dto.RegisteUserDTO;
import br.com.prova.cotefacil.apigateway.entities.User;
import br.com.prova.cotefacil.apigateway.entities.enums.UsuarioRole;
import br.com.prova.cotefacil.apigateway.security.TokenService;
import br.com.prova.cotefacil.apigateway.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserService userService;

    @Test
    void login_WithValidCredentials_ReturnsToken() throws Exception {
        User user = new User("usuario", "encoded", UsuarioRole.USER);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
        when(tokenService.criarToken(any(User.class))).thenReturn("token-jwt-teste");

        AuthDTO dto = new AuthDTO("usuario", "senha123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message.token").value("token-jwt-teste"));
    }

    @Test
    void login_WithInvalidUsername_Returns401() throws Exception {
        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("Bad credentials"));

        AuthDTO dto = new AuthDTO("invalido", "senha123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_WithInvalidData_Returns400() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_WithValidData_ReturnsToken() throws Exception {
        when(userService.ifExistsUser("novousuario")).thenReturn(false);
        when(tokenService.criarToken(any(User.class))).thenReturn("token-novo");

        RegisteUserDTO dto = new RegisteUserDTO("novousuario", "senha123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message.token").value("token-novo"));

        verify(userService).salvar(any(User.class));
    }

    @Test
    void register_WithExistingUser_Returns400() throws Exception {
        when(userService.ifExistsUser("existente")).thenReturn(true);

        RegisteUserDTO dto = new RegisteUserDTO("existente", "senha123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
