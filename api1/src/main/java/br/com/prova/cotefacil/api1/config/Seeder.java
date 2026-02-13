package br.com.prova.cotefacil.api1.config;

import br.com.prova.cotefacil.api1.entity.Usuario;
import br.com.prova.cotefacil.api1.entity.UsuarioRole;
import br.com.prova.cotefacil.api1.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class Seeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

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
