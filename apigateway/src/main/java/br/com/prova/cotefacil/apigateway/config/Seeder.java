package br.com.prova.cotefacil.apigateway.config;

import br.com.prova.cotefacil.apigateway.entity.Usuario;
import br.com.prova.cotefacil.apigateway.entity.UsuarioRole;
import br.com.prova.cotefacil.apigateway.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class Seeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;


    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        log.info("[SEEDER] Iniciando carga inicial...");

        if (usuarioRepository.count() > 0) {
            log.info("[SEEDER] Já existem dados, pulando seed.");
            return;
        }

        Usuario usuario = Usuario.builder()
                .username("usuario")
                .password(passwordEncoder.encode("senha123"))
                .role(UsuarioRole.USER)
                .build();

        usuarioRepository.save(usuario);

        log.info("[SEEDER] Pedido inicial criado!");
    }
}
