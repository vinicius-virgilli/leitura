package org.viniciusvirgilli.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.*;
import org.viniciusvirgilli.enums.CategoriaLivroEnum;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "dias_semana_leitura", joinColumns = @JoinColumn(name = "metrica_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "dia")
    private List<DayOfWeek> diasSemanaLeitura;

}
