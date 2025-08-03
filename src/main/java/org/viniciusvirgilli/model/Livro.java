package org.viniciusvirgilli.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.*;
import org.viniciusvirgilli.enums.CategoriaLivroEnum;
import org.viniciusvirgilli.enums.StatusLeituraEnum;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "livros")
public class Livro extends PanacheEntity {

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private Integer totalPaginas;

    @Enumerated(EnumType.STRING)
    private StatusLeituraEnum status;

    @Enumerated(EnumType.STRING)
    private CategoriaLivroEnum categoria;

    private LocalDate inicioLeitura;
    private LocalDate terminoLeitura;
    private Integer saldoPaginas;
    private Integer paginaAtual;
    private Integer paginasLidas;
    private String progresso;
    private Integer ordemDeLeitura;
}
