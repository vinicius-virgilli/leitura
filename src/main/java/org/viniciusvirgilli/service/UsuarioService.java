package org.viniciusvirgilli.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import org.viniciusvirgilli.dto.UsuarioAtualizacaoDto;
import org.viniciusvirgilli.dto.UsuarioCriacaoDto;
import org.viniciusvirgilli.model.Usuario;

import java.util.List;

@ApplicationScoped
public class UsuarioService {

    private static final Logger LOG = Logger.getLogger(UsuarioService.class);

    @Inject
    PasswordService passwordService;

    @Transactional
    public Usuario criarUsuario(UsuarioCriacaoDto dto) {
        LOG.info("[CRIAR_USUARIO] Iniciando criação de usuário com email: " + dto.getEmail());
        
        try {
            // Temporariamente comentado para debug
            // if (usuarioJaExiste(dto.getEmail())) {
            //     throw new IllegalArgumentException("Usuário com este email já existe.");
            // }

            LOG.info("[CRIAR_USUARIO] Criando objeto Usuario");
            Usuario usuario = new Usuario();
            usuario.setEmail(dto.getEmail());
            usuario.setNome(dto.getNome());
            
            LOG.info("[CRIAR_USUARIO] Criptografando senha");
            usuario.setSenha(passwordService.encryptPassword(dto.getSenha()));
            usuario.setAtivo(true);
            usuario.setPerfil(org.viniciusvirgilli.enums.Perfil.USER);

            LOG.info("[CRIAR_USUARIO] Persistindo usuário no banco");
            usuario.persist();
            LOG.info("[CRIAR_USUARIO] Usuário criado com sucesso");
            return usuario;
        } catch (Exception e) {
            LOG.error("[CRIAR_USUARIO] Erro ao criar usuário: " + e.getMessage(), e);
            throw e;
        }
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

    /**
     * Busca um usuário pelo email.
     * 
     * @param email email do usuário
     * @return usuário encontrado ou null
     */
    public Usuario buscarPorEmail(String email) {
        LOG.infof("[BUSCA] Iniciando busca por email");
        LOG.infof("[BUSCA] Email recebido: %s", email);
        
        if (email == null || email.trim().isEmpty()) {
            LOG.warnf("[BUSCA] Email é null ou vazio");
            return null;
        }
        
        String emailProcessado = email.trim().toLowerCase();
        LOG.infof("[BUSCA] Email processado: %s", emailProcessado);
        LOG.infof("[BUSCA] Executando query no banco de dados");
        
        try {
            Usuario usuario = Usuario.find("email = ?1", emailProcessado).firstResult();
            
            if (usuario != null) {
                LOG.infof("[BUSCA] Usuário encontrado: ID=%d, Email=%s", usuario.id, usuario.getEmail());
            } else {
                LOG.warnf("[BUSCA] Nenhum usuário encontrado para email: %s", emailProcessado);
            }
            
            return usuario;
        } catch (Exception e) {
            LOG.errorf(e, "[BUSCA] Erro ao buscar usuário por email: %s", emailProcessado);
            throw e;
        }
    }

    /**
     * Autentica um usuário verificando email e senha.
     * 
     * @param email email do usuário
     * @param senha senha em texto plano
     * @return usuário autenticado ou null se credenciais inválidas
     */
    public Usuario autenticar(String email, String senha) {
        LOG.infof("[AUTH] Iniciando autenticação");
        LOG.infof("[AUTH] Email recebido: %s", email);
        LOG.infof("[AUTH] Senha recebida: %s", senha != null ? "[PRESENTE]" : "null");
        
        if (email == null || senha == null) {
            LOG.error("[AUTH] Email ou senha são null");
            return null;
        }
        
        String emailProcessado = email.trim().toLowerCase();
        LOG.infof("[AUTH] Email processado: %s", emailProcessado);
        LOG.infof("[AUTH] Buscando usuário por email");
        
        Usuario usuario = buscarPorEmail(emailProcessado);
        
        if (usuario == null) {
            LOG.warnf("[AUTH] Usuário não encontrado para email: %s", emailProcessado);
            return null;
        }
        
        LOG.infof("[AUTH] Usuário encontrado: ID=%d, Email=%s, Ativo=%s", usuario.id, usuario.getEmail(), usuario.getAtivo());
        
        if (!usuario.getAtivo()) {
            LOG.warnf("[AUTH] Usuário inativo: %s", emailProcessado);
            return null;
        }
        
        LOG.infof("[AUTH] Verificando senha");
        LOG.infof("[AUTH] Senha hash armazenada: %s", usuario.getSenha() != null ? "[PRESENTE]" : "null");
        
        if (passwordService.matches(senha, usuario.getSenha())) {
            LOG.infof("[AUTH] Senha válida - autenticação bem-sucedida");
            return usuario;
        } else {
            LOG.warnf("[AUTH] Senha inválida para usuário: %s", emailProcessado);
        }
        
        return null;
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
            usuario.setSenha(passwordService.encryptPassword(dto.getSenha()));
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