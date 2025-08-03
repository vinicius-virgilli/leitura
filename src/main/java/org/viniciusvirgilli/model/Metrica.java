package org.viniciusvirgilli.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.*;
import org.viniciusvirgilli.enums.CategoriaLivroEnum;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "metricas")
public class Metrica extends PanacheEntity {

    @Enumerated(EnumType.STRING)
    private CategoriaLivroEnum categoria;

    private Integer paginasPorDia;
    private Integer totalPaginasLidas;
    private Integer totalLivrosLidos;
    private LocalDate dataBase;

    }
