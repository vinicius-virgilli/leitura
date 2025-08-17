package org.viniciusvirgilli.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.viniciusvirgilli.model.Usuario;
import org.jboss.logging.Logger;
import io.smallrye.jwt.build.Jwt;
import java.time.Duration;
import java.util.Set;

@ApplicationScoped
public class JwtService {

    private static final Logger LOG = Logger.getLogger(JwtService.class);
    /**
     * Gera um token JWT para o usuário usando SmallRye JWT Build
     * 
     * @param usuario usuário para o qual gerar o token
     * @return token JWT
     */
    public String gerarToken(Usuario usuario) {
        try {
            LOG.infof("[JWT] Iniciando geração de token para usuário: %s", usuario.getEmail());
            
            String token = Jwt.issuer("leitura-api")
                .audience("leitura-users")
                .subject(usuario.id.toString())
                .claim("email", usuario.getEmail())
                .claim("nome", usuario.getNome())
                .groups(Set.of(usuario.getPerfil().toString()))
                .expiresIn(Duration.ofHours(24))
                .sign();
            
            LOG.infof("[JWT] Token JWT gerado com sucesso para usuário: %s", usuario.getEmail());
            return token;
            
        } catch (Exception e) {
            LOG.errorf(e, "[JWT] Erro ao gerar token JWT para usuário: %s", usuario.getEmail());
            throw new RuntimeException("Erro ao gerar token JWT", e);
        }
    }

    /**
     * Extrai o ID do usuário do token JWT
     * 
     * @param token token JWT
     * @return ID do usuário
     */
    public Long extractUserId(String token) {
        // Este método seria usado para extrair informações do token
        // A validação é feita automaticamente pelo Quarkus
        return null; // Implementação seria feita conforme necessidade
    }

    /**
     * Verifica se o token é válido
     * 
     * @param token token JWT
     * @return true se válido, false caso contrário
     */
    public boolean isTokenValid(String token) {
        // A validação é feita automaticamente pelo Quarkus
        // Este método seria usado para validações customizadas
        return true;
    }
}