package org.viniciusvirgilli.dto;

import lombok.Data;

@Data
public class UsuarioCriacaoDto {
    private String email;
    private String nome;
    private String senha;
}