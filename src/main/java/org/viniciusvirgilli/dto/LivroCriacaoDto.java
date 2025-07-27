package org.viniciusvirgilli.dto;

import lombok.Data;

@Data
public class LivroCriacaoDto {
    private String nome;
    private Integer totalPaginas;
    private Integer saldoPaginas;
}
