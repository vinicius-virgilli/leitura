package org.viniciusvirgilli.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.viniciusvirgilli.dto.MetricaCriacaoDto;
import org.viniciusvirgilli.enums.CategoriaLivroEnum;

@ApplicationScoped
public class ValidaMetricaService {

    public void validarCriacao(MetricaCriacaoDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Metrica não pode ser nula.");
        }

        if (dto.getCategoria() == null) {
            throw new IllegalArgumentException("A categoria da métrica não pode ser nula.");
        }else if (dto.getCategoria() != CategoriaLivroEnum.ESPIRITUAL & dto.getCategoria() != CategoriaLivroEnum.INTELECTUAL) {
            throw new IllegalArgumentException("categoria deve ser INTELECTUAL ou ESPIRITUAL");
        }

        if (dto.getDataBase() == null) {
            throw new IllegalArgumentException("A dataBase da métrica não pode ser nula.");
        }
    }
}
