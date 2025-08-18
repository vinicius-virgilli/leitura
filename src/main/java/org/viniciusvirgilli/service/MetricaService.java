package org.viniciusvirgilli.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.viniciusvirgilli.dto.MetricaCriacaoDto;
import org.viniciusvirgilli.enums.CategoriaLivroEnum;
import org.viniciusvirgilli.model.Metrica;
import org.viniciusvirgilli.model.Usuario;
import io.quarkus.cache.CacheResult;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheKey;

import java.util.List;

@ApplicationScoped
public class MetricaService {

    @Inject
    UsuarioService usuarioService;

    @Transactional
    @CacheInvalidate(cacheName = "metricas")
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
    @CacheInvalidate(cacheName = "metricas")
    @CacheInvalidate(cacheName = "metricas-por-usuario")
    public Metrica criarMetrica(MetricaCriacaoDto dto, @CacheKey Long usuarioId) {
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


    @CacheResult(cacheName = "metricas")
    public List<Metrica> listarTodos() {
        return Metrica.listAll();
    }


    @CacheResult(cacheName = "metrica-por-categoria")
    public Metrica buscarPorCategoria(@CacheKey CategoriaLivroEnum categoriaLivroEnum) {
        return Metrica.find("categoria", categoriaLivroEnum).firstResult();
    }

    @CacheResult(cacheName = "metrica-por-categoria-usuario")
    public Metrica buscarPorCategoriaEUsuario(@CacheKey CategoriaLivroEnum categoriaLivroEnum, @CacheKey Long usuarioId) {
        return Metrica.find("categoria = ?1 and usuario.id = ?2", categoriaLivroEnum, usuarioId).firstResult();
    }

    @CacheResult(cacheName = "metricas-por-usuario")
    public List<Metrica> listarPorUsuario(@CacheKey Long usuarioId) {
        return Metrica.find("usuario.id", usuarioId).list();
    }

    @CacheResult(cacheName = "metricas-por-usuario")
    public List<Metrica> listarMetricasPorUsuario(@CacheKey Long usuarioId) {
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
        return Metrica.find("usuario.id", usuarioId).list();
    }

    @CacheResult(cacheName = "metricas")
    public List<Metrica> listarMetricas() {
        return Metrica.listAll();
    }

    @Transactional
    @CacheInvalidate(cacheName = "metricas")
    @CacheInvalidate(cacheName = "metrica-por-categoria")
    public void deletarMetrica(@CacheKey CategoriaLivroEnum categoriaLivroEnum) {
        Metrica metrica = buscarPorCategoria(categoriaLivroEnum);
        if (metrica != null) {
            metrica.delete();
        } else {
            throw new IllegalArgumentException("Métrica não encontrada.");
        }
    }

    @Transactional
    @CacheInvalidate(cacheName = "metricas")
    @CacheInvalidate(cacheName = "metricas-por-usuario")
    @CacheInvalidate(cacheName = "metrica-por-categoria-usuario")
    public void deletarMetrica(@CacheKey Long metricaId, @CacheKey Long usuarioId) {
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        Metrica metrica = Metrica.findById(metricaId);
        if (metrica == null) {
            throw new IllegalArgumentException("Métrica não encontrada.");
        }

        if (!metrica.getUsuario().id.equals(usuarioId)) {
            throw new SecurityException("Métrica não pertence ao usuário especificado.");
        }

        metrica.delete();
    }
}
