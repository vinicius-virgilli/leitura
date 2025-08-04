package org.viniciusvirgilli.dto;

import lombok.Data;
import org.viniciusvirgilli.enums.CategoriaLivroEnum;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Data
public class MetricaCriacaoDto {
    private CategoriaLivroEnum categoria;
    private LocalDate dataBase;
    private Integer paginasPorDia;
    private List<DayOfWeek> diasSemanaLeitura;

}
