package br.com.cotefacil.prova.services;

import br.com.cotefacil.prova.entitys.users.Usuario;
import br.com.cotefacil.prova.repositorys.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    @Autowired
    UsuarioRepository usuarioRepository;


    public boolean existeUsuario(String username) {
        return usuarioRepository.findByUsername(username) != null;
    }

    public UserDetails pegarUsuario(String username) {
        return usuarioRepository.findByUsername(username);

    }

    public void salvar(Usuario usuario) {
        usuarioRepository.save(usuario);
    }
}
