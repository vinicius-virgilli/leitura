package org.viniciusvirgilli.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.viniciusvirgilli.dto.MetricaCriacaoDto;
import org.viniciusvirgilli.enums.CategoriaLivroEnum;
import org.viniciusvirgilli.model.Metrica;
import org.viniciusvirgilli.model.Usuario;

import java.util.List;

@ApplicationScoped
public class MetricaService {

    @Inject
    UsuarioService usuarioService;

    @Transactional
    public Metrica criarMetrica(MetricaCriacaoDto dto) {
        if (metricaJaExiste(dto.getCategoria())) {
            throw new IllegalArgumentException("A métrica já existe.");
        }

        Metrica metrica = new Metrica();
        metrica.setCategoria(dto.getCategoria());
        metrica.setDataBase(dto.getDataBase());
        metrica.setDiasSemanaLeitura(dto.getDiasSemanaLeitura());
        metrica.setPaginasPorDia(dto.getPaginasPorDia());

        metrica.persist();

        return metrica;
    }

    @Transactional
    public Metrica criarMetrica(MetricaCriacaoDto dto, Long usuarioId) {
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        if (metricaJaExiste(dto.getCategoria(), usuarioId)) {
            throw new IllegalArgumentException("A métrica já existe para este usuário.");
        }

        Metrica metrica = new Metrica();
        metrica.setCategoria(dto.getCategoria());
        metrica.setDataBase(dto.getDataBase());
        metrica.setDiasSemanaLeitura(dto.getDiasSemanaLeitura());
        metrica.setPaginasPorDia(dto.getPaginasPorDia());
        metrica.setUsuario(usuario);

        metrica.persist();

        return metrica;
    }


    private boolean metricaJaExiste(CategoriaLivroEnum categoria) {
        return buscarPorCategoria(categoria) != null;
    }

    private boolean metricaJaExiste(CategoriaLivroEnum categoria, Long usuarioId) {
        return buscarPorCategoriaEUsuario(categoria, usuarioId) != null;
    }


    public List<Metrica> listarTodos() {
        return Metrica.listAll();
    }


    public Metrica buscarPorCategoria(CategoriaLivroEnum categoriaLivroEnum) {
        return Metrica.find("categoria", categoriaLivroEnum).firstResult();
    }

    public Metrica buscarPorCategoriaEUsuario(CategoriaLivroEnum categoriaLivroEnum, Long usuarioId) {
        return Metrica.find("categoria = ?1 and usuario.id = ?2", categoriaLivroEnum, usuarioId).firstResult();
    }

    public List<Metrica> listarPorUsuario(Long usuarioId) {
        return Metrica.find("usuario.id", usuarioId).list();
    }


    @Transactional
    public void deletarMetrica(CategoriaLivroEnum categoriaLivroEnum) {
        Metrica metrica = buscarPorCategoria(categoriaLivroEnum);
        if (metrica != null) {
            metrica.delete();
        } else {
            throw new IllegalArgumentException("Métrica não encontrada.");
        }
    }
}
