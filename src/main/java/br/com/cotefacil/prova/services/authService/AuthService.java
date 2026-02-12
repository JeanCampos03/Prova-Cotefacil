package br.com.cotefacil.prova.services.authService;

import br.com.cotefacil.prova.repositorys.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("[AUTH] Tentativa de login username={}", username);

        UserDetails user = usuarioRepository.findByUsername(username);

        if (user == null) {
            log.warn("[AUTH] Usuário não encontrado username={}", username);
        }

        return user;
    }
}