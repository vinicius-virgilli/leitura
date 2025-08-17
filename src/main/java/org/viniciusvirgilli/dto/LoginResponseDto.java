package org.viniciusvirgilli.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.viniciusvirgilli.model.Usuario;
import org.viniciusvirgilli.enums.Perfil;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private Long id;
    private String email;
    private String nome;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private Boolean ativo;
    private Perfil perfil;
    private String token;
    private String tokenType;
    private Long expiresIn; // em segundos

    public static LoginResponseDto fromEntity(Usuario usuario, String token) {
        return new LoginResponseDto(
            usuario.id,
            usuario.getEmail(),
            usuario.getNome(),
            usuario.getDataCriacao(),
            usuario.getDataAtualizacao(),
            usuario.getAtivo(),
            usuario.getPerfil(),
            token,
            "Bearer",
            21600L // 6 horas em segundos
        );
    }
}