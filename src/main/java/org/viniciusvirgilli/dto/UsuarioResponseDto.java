package org.viniciusvirgilli.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.viniciusvirgilli.model.Usuario;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDto {
    private Long id;
    private String email;
    private String nome;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private Boolean ativo;

    public static UsuarioResponseDto fromEntity(Usuario usuario) {
        return new UsuarioResponseDto(
            usuario.id,
            usuario.getEmail(),
            usuario.getNome(),
            usuario.getDataCriacao(),
            usuario.getDataAtualizacao(),
            usuario.getAtivo()
        );
    }
}