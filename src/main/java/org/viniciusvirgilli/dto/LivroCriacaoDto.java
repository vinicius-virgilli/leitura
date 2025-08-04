package org.viniciusvirgilli.dto;

import lombok.Data;
import org.viniciusvirgilli.enums.CategoriaLivroEnum;

@Data
public class LivroCriacaoDto {
    private String nome;
    private Integer totalPaginas;
    private Integer saldoPaginas;
    private CategoriaLivroEnum categoria;
    private Integer quantidadeVezesLido;
}
