package org.viniciusvirgilli.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "metaLeitura")
public class MetaLeitura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer paginasPorDia;
    private Integer totalPaginasLidas;
    private Integer totalLivrosLidos;
    private LocalDate dataBase;

    }
