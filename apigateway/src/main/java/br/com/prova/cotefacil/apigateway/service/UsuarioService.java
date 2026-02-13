package br.com.prova.cotefacil.apigateway.service;


import br.com.prova.cotefacil.apigateway.entity.Usuario;
import br.com.prova.cotefacil.apigateway.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;


    public boolean existeUsuario(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }

    // Retorna o usuário logado com base no JWT
    public Usuario getUsuarioLogado() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof Usuario usuario) {
            return usuario;
        } else if (principal instanceof UserDetails userDetails) {
            return usuarioRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        }

        return null;
    }

    public UserDetails pegarUsuario(String username) {
        return loadUserByUsername(username);
    }

    public void salvar(Usuario usuario) {
        usuarioRepository.save(usuario);
    }
}
