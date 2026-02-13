package br.com.prova.cotefacil.apigateway.config;

import br.com.prova.cotefacil.apigateway.entities.User;
import br.com.prova.cotefacil.apigateway.entities.enums.UsuarioRole;
import br.com.prova.cotefacil.apigateway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class Seeder implements CommandLineRunner {

    private final UserRepository userRepository;


    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        log.info("[SEEDER] Iniciando carga inicial...");

        if (userRepository.count() > 0) {
            log.info("[SEEDER] Já existem dados, pulando seed.");
            return;
        }

        User user = User.builder()
                .username("usuario")
                .password(passwordEncoder.encode("senha123"))
                .role(UsuarioRole.USER)
                .build();

        userRepository.save(user);

        log.info("[SEEDER] Pedido inicial criado!");
    }
}
