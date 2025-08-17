package org.viniciusvirgilli.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.viniciusvirgilli.dto.UsuarioAtualizacaoDto;
import org.viniciusvirgilli.dto.UsuarioCriacaoDto;
import org.viniciusvirgilli.model.Usuario;

import java.util.List;

@ApplicationScoped
public class UsuarioService {

    @Transactional
    public Usuario criarUsuario(UsuarioCriacaoDto dto) {
        if (usuarioJaExiste(dto.getEmail())) {
            throw new IllegalArgumentException("Usuário com este email já existe.");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setNome(dto.getNome());
        usuario.setSenha(dto.getSenha());
        usuario.setAtivo(true);

        usuario.persist();
        return usuario;
    }

    private boolean usuarioJaExiste(String email) {
        return Usuario.find("email", email).firstResult() != null;
    }

    public List<Usuario> listarTodos() {
        return Usuario.listAll();
    }

    public List<Usuario> listarAtivos() {
        return Usuario.find("ativo", true).list();
    }

    public Usuario buscarPorId(Long usuarioId) {
        return Usuario.findById(usuarioId);
    }

    public Usuario buscarPorEmail(String email) {
        return Usuario.find("email", email).firstResult();
    }

    @Transactional
    public Usuario atualizarUsuario(Long usuarioId, UsuarioAtualizacaoDto dto) {
        Usuario usuario = Usuario.findById(usuarioId);
        
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        if (dto.getNome() != null && !dto.getNome().trim().isEmpty()) {
            usuario.setNome(dto.getNome().trim());
        }

        if (dto.getSenha() != null && !dto.getSenha().trim().isEmpty()) {
            usuario.setSenha(dto.getSenha());
        }

        if (dto.getAtivo() != null) {
            usuario.setAtivo(dto.getAtivo());
        }

        usuario.persist();
        return usuario;
    }

    @Transactional
    public void deletarUsuario(Long usuarioId) {
        Usuario usuario = Usuario.findById(usuarioId);
        
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        usuario.delete();
    }

    @Transactional
    public Usuario desativarUsuario(Long usuarioId) {
        Usuario usuario = Usuario.findById(usuarioId);
        
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        usuario.setAtivo(false);
        usuario.persist();
        return usuario;
    }

    @Transactional
    public Usuario ativarUsuario(Long usuarioId) {
        Usuario usuario = Usuario.findById(usuarioId);
        
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        usuario.setAtivo(true);
        usuario.persist();
        return usuario;
    }
}