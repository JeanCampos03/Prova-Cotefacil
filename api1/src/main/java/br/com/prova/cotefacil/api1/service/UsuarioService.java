package br.com.prova.cotefacil.api1.service;


import br.com.prova.cotefacil.api1.entity.Usuario;
import br.com.prova.cotefacil.api1.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService implements UserDetailsService {
    @Autowired
    UsuarioRepository usuarioRepository;


    public boolean existeUsuario(String username) {
        return usuarioRepository.findByUsername(username) != null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = usuarioRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        }
        return user;
    }

    public UserDetails pegarUsuario(String username) {
        return loadUserByUsername(username);
    }

    public void salvar(Usuario usuario) {
        usuarioRepository.save(usuario);
    }
}
