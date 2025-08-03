package org.viniciusvirgilli.dto;

import lombok.Data;
import org.viniciusvirgilli.enums.CategoriaLivroEnum;

import java.time.LocalDate;

@Data
public class MetricaCriacaoDto {
    private CategoriaLivroEnum categoria;
    private LocalDate dataBase;

}
