package org.viniciusvirgilli.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.viniciusvirgilli.dto.UsuarioAtualizacaoDto;
import org.viniciusvirgilli.dto.UsuarioCriacaoDto;

import java.util.regex.Pattern;

@ApplicationScoped
public class ValidaUsuarioService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
        "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    public void validarCriacao(UsuarioCriacaoDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo.");
        }

        validarEmail(dto.getEmail());
        validarNome(dto.getNome());
        validarSenha(dto.getSenha());

        // Normalizar dados
        dto.setEmail(dto.getEmail().trim().toLowerCase());
        dto.setNome(dto.getNome().trim());
    }

    public void validarAtualizacao(UsuarioAtualizacaoDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Dados de atualização não podem ser nulos.");
        }

        if (dto.getNome() != null) {
            validarNome(dto.getNome());
            dto.setNome(dto.getNome().trim());
        }

        if (dto.getSenha() != null) {
            validarSenha(dto.getSenha());
        }
    }

    private void validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("O email é obrigatório.");
        }

        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("O email deve ter um formato válido.");
        }

        if (email.trim().length() > 255) {
            throw new IllegalArgumentException("O email não pode ter mais de 255 caracteres.");
        }
    }

    private void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }

        if (nome.trim().length() < 2) {
            throw new IllegalArgumentException("O nome deve ter pelo menos 2 caracteres.");
        }

        if (nome.trim().length() > 100) {
            throw new IllegalArgumentException("O nome não pode ter mais de 100 caracteres.");
        }
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("A senha é obrigatória.");
        }

        if (senha.length() < 6) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 6 caracteres.");
        }

        if (senha.length() > 255) {
            throw new IllegalArgumentException("A senha não pode ter mais de 255 caracteres.");
        }
    }
}