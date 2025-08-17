package org.viniciusvirgilli.service;

import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.elytron.security.common.BcryptUtil;

/**
 * Serviço responsável pela criptografia e validação de senhas.
 * Utiliza BCrypt nativo do Quarkus para hash seguro das senhas.
 */
@ApplicationScoped
public class PasswordService {

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
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return BcryptUtil.matches(rawPassword, encodedPassword);
    }
}