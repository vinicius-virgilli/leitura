package org.viniciusvirgilli.dto;

import lombok.Data;

@Data
public class UsuarioAtualizacaoDto {
    private String nome;
    private String senha;
    private Boolean ativo;
}