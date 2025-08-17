package org.viniciusvirgilli.service;

import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.elytron.security.common.BcryptUtil;
import org.jboss.logging.Logger;

/**
 * Serviço responsável pela criptografia e validação de senhas.
 * Utiliza BCrypt nativo do Quarkus para hash seguro das senhas.
 */
@ApplicationScoped
public class PasswordService {

    private static final Logger LOG = Logger.getLogger(PasswordService.class);

    /**
     * Criptografa uma senha em texto plano.
     * 
     * @param rawPassword senha em texto plano
     * @return senha criptografada
     */
    public String encryptPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha não pode ser nula ou vazia");
        }
        return BcryptUtil.bcryptHash(rawPassword);
    }

    /**
     * Verifica se uma senha em texto plano corresponde à senha criptografada.
     * 
     * @param rawPassword senha em texto plano
     * @param encodedPassword senha criptografada
     * @return true se as senhas correspondem, false caso contrário
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        LOG.infof("[PASSWORD] Iniciando verificação de senha");
        LOG.infof("[PASSWORD] Senha raw recebida: %s", rawPassword != null ? "[PRESENTE]" : "null");
        LOG.infof("[PASSWORD] Senha encoded recebida: %s", encodedPassword != null ? "[PRESENTE]" : "null");
        
        if (rawPassword == null || encodedPassword == null) {
            LOG.warnf("[PASSWORD] Senha raw ou encoded é null");
            return false;
        }
        
        try {
            boolean matches = BcryptUtil.matches(rawPassword, encodedPassword);
            LOG.infof("[PASSWORD] Resultado da verificação: %s", matches ? "MATCH" : "NO_MATCH");
            return matches;
        } catch (Exception e) {
            LOG.errorf(e, "[PASSWORD] Erro ao verificar senha");
            return false;
        }
    }
}