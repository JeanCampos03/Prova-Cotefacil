package br.com.prova.cotefacil.api1.controller;

import br.com.prova.cotefacil.api1.dto.AuthDTO;
import br.com.prova.cotefacil.api1.dto.RegistroUsuarioDTO;
import br.com.prova.cotefacil.api1.entity.Usuario;
import br.com.prova.cotefacil.api1.entity.UsuarioRole;
import br.com.prova.cotefacil.api1.security.TokenService;
import br.com.prova.cotefacil.api1.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"spring.profiles.active=test"})
@AutoConfigureMockMvc
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
    private UsuarioService usuarioService;

    @Test
    void login_ComCredenciaisValidas_RetornaToken() throws Exception {
        Usuario usuario = new Usuario("usuario", "encoded", UsuarioRole.USER);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));
        when(tokenService.criarToken(any(Usuario.class))).thenReturn("token-jwt-teste");

        AuthDTO dto = new AuthDTO("usuario", "senha123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.mensagem.token").value("token-jwt-teste"));
    }

    @Test
    void login_ComUsernameInvalido_Retorna401() throws Exception {
        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("Bad credentials"));

        AuthDTO dto = new AuthDTO("invalido", "senha123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_ComDadosInvalidos_Retorna400() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ComDadosValidos_RetornaToken() throws Exception {
        when(usuarioService.existeUsuario("novousuario")).thenReturn(false);
        when(tokenService.criarToken(any(Usuario.class))).thenReturn("token-novo");

        RegistroUsuarioDTO dto = new RegistroUsuarioDTO("novousuario", "senha123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.mensagem.token").value("token-novo"));

        verify(usuarioService).salvar(any(Usuario.class));
    }

    @Test
    void register_ComUsuarioExistente_Retorna400() throws Exception {
        when(usuarioService.existeUsuario("existente")).thenReturn(true);

        RegistroUsuarioDTO dto = new RegistroUsuarioDTO("existente", "senha123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
