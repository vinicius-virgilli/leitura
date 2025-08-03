package org.viniciusvirgilli.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.viniciusvirgilli.dto.MetricaCriacaoDto;
import org.viniciusvirgilli.enums.CategoriaLivroEnum;
import org.viniciusvirgilli.model.Metrica;

import java.util.List;

@ApplicationScoped
public class MetricaService {

    @Transactional
    public Metrica criarMetrica(MetricaCriacaoDto dto) {
        if (metricaJaExiste(dto.getCategoria())) {
            throw new IllegalArgumentException("A métrica já existe.");
        }

        Metrica metrica = new Metrica();
        metrica.setCategoria(dto.getCategoria());
        metrica.setDataBase(dto.getDataBase());

        metrica.persist();

        return metrica;
    }

    private boolean metricaJaExiste(CategoriaLivroEnum categoria) {
        return Metrica.find("categoria", categoria).firstResult() != null;
    }

    public List<Metrica> listarTodos() {
        return Metrica.listAll();
    }
}
