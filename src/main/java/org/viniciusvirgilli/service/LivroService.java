package org.viniciusvirgilli.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.viniciusvirgilli.dto.LivroCriacaoDto;
import org.viniciusvirgilli.enums.CategoriaLivroEnum;
import org.viniciusvirgilli.enums.StatusLeituraEnum;
import org.viniciusvirgilli.model.Livro;
import org.viniciusvirgilli.model.Usuario;
import io.quarkus.cache.CacheResult;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheKey;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class LivroService {

    @Inject
    UsuarioService usuarioService;

    @Transactional
    @CacheInvalidate(cacheName = "livros")
    @CacheInvalidate(cacheName = "livros-por-usuario")
    public Livro criarLivro(LivroCriacaoDto dto, @CacheKey Long usuarioId) {
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        if (livroJaExiste(dto.getNome(), usuarioId)) {
            throw new IllegalArgumentException("O livro já existe para este usuário.");
        }

        List<Livro> livros = listarPorUsuario(usuarioId);

        Livro livro;

        if (livros.isEmpty()) {
            livro = criarPrimeiroLivro(dto, usuario);
        } else {
            livro = criarLivroComListaExistente(dto, livros.size(), usuario);
        }

        livro.persist();
        return livro;
    }

    private boolean livroJaExiste(String nome, Long usuarioId) {
        return Livro.find("nome = ?1 and usuario.id = ?2", nome, usuarioId).firstResult() != null;
    }

    private Livro criarPrimeiroLivro(LivroCriacaoDto dto, Usuario usuario) {
        Livro livro = new Livro();

        livro.setNome(dto.getNome());
        livro.setTotalPaginas(dto.getTotalPaginas());
        livro.setInicioLeitura(LocalDate.now().plusDays(1));
        livro.setOrdemDeLeitura(1);

        int saldo = dto.getSaldoPaginas() != null ? dto.getSaldoPaginas() : 0;
        livro.setSaldoPaginas(saldo);

        livro.setPaginasLidas(saldo);
        livro.setPaginaAtual(null);
        livro.setProgresso(null);
        livro.setStatus(StatusLeituraEnum.A_LER);
        livro.setTerminoLeitura(null);
        livro.setCategoria(dto.getCategoria());
        livro.setQuantidadeVezesLido(dto.getQuantidadeVezesLido() != null? dto.getQuantidadeVezesLido() : 0);
        livro.setUsuario(usuario);

        return livro;
    }

    private Livro criarLivroComListaExistente(LivroCriacaoDto dto, int tamanhoLista, Usuario usuario) {
        Livro livro = new Livro();

        livro.setNome(dto.getNome());
        livro.setTotalPaginas(dto.getTotalPaginas());
        livro.setInicioLeitura(LocalDate.now().plusDays(1));
        livro.setOrdemDeLeitura(tamanhoLista + 1);

        int saldo = dto.getSaldoPaginas() != null ? dto.getSaldoPaginas() : 0;
        livro.setSaldoPaginas(saldo);

        livro.setPaginasLidas(saldo);
        livro.setPaginaAtual(null);
        livro.setProgresso(null);
        livro.setStatus(StatusLeituraEnum.A_LER);
        livro.setTerminoLeitura(null);
        livro.setCategoria(dto.getCategoria());
        livro.setQuantidadeVezesLido(dto.getQuantidadeVezesLido() != null? dto.getQuantidadeVezesLido() : 0);
        livro.setUsuario(usuario);

        return livro;
    }

    @CacheResult(cacheName = "livros")
    public List<Livro> listarLivros() {
        return Livro.listAll();
    }

    @CacheResult(cacheName = "livros")
    public List<Livro> listarTodos() {
        return Livro.listAll();
    }

    @CacheResult(cacheName = "livros-por-usuario")
    public List<Livro> listarPorUsuario(@CacheKey Long usuarioId) {
        return Livro.find("usuario.id", usuarioId).list();
    }

    @CacheResult(cacheName = "livro-por-id")
    public Livro listarLivroPorId(@CacheKey Integer livroId) {
        return Livro.find("id", livroId).firstResult();
    }

    @CacheResult(cacheName = "livro-por-id-usuario")
    public Livro buscarPorIdEUsuario(@CacheKey Long id, @CacheKey Long usuarioId) {
        return Livro.find("id = ?1 and usuario.id = ?2", id, usuarioId).firstResult();
    }

    @CacheResult(cacheName = "livros-por-status")
    public List<Livro> buscarPorStatus(@CacheKey StatusLeituraEnum status) {
        return Livro.find("status", status).list();
    }

    @CacheResult(cacheName = "livros-por-status-usuario")
    public List<Livro> buscarPorStatusEUsuario(@CacheKey StatusLeituraEnum status, @CacheKey Long usuarioId) {
        return Livro.find("status = ?1 and usuario.id = ?2", status, usuarioId).list();
    }

    @CacheResult(cacheName = "livros-por-categoria")
    public List<Livro> buscarPorCategoria(@CacheKey CategoriaLivroEnum categoria) {
        return Livro.find("categoria", categoria).list();
    }

    @CacheResult(cacheName = "livros-por-categoria-usuario")
    public List<Livro> buscarPorCategoriaEUsuario(@CacheKey CategoriaLivroEnum categoria, @CacheKey Long usuarioId) {
        return Livro.find("categoria = ?1 and usuario.id = ?2", categoria, usuarioId).list();
    }

    @Transactional
    public void atualizarLivro(Livro livro) {
        Livro livroExistente = Livro.find("nome", livro.getNome()).firstResult();

        if (livroExistente == null) {
            throw new IllegalArgumentException("Livro não encontrado.");
        }

        livroExistente.setNome(livro.getNome());
        livroExistente.setTotalPaginas(livro.getTotalPaginas());
        livroExistente.setInicioLeitura(livro.getInicioLeitura());
        livroExistente.setOrdemDeLeitura(livro.getOrdemDeLeitura());
        livroExistente.setSaldoPaginas(livro.getSaldoPaginas());
        livroExistente.setPaginasLidas(livro.getPaginasLidas());
        livroExistente.setPaginaAtual(livro.getPaginaAtual());
        livroExistente.setProgresso(livro.getProgresso());
        livroExistente.setStatus(livro.getStatus());
        livroExistente.setTerminoLeitura(livro.getTerminoLeitura());

        livroExistente.persist();
    }

    @Transactional
    public Livro atualizarStatusLivro(Integer livroId, StatusLeituraEnum status) {
        Livro livro = Livro.findById(livroId);

        if (livro == null) {
            throw new IllegalArgumentException("Livro não encontrado.");
        }

        livro.setStatus(status);

        livro.persist();

        return livro;
    }

    @Transactional
    public void deletarLivro(Integer livroId) {
        Livro livro = Livro.findById(livroId);

        if (livro == null) {
            throw new IllegalArgumentException("Livro não encontrado.");
        }

        livro.delete();
    }

    @Transactional
    public Livro atualizarSaldoLivro(Integer livroId, Integer saldo) {
        Livro livro = Livro.findById(livroId);

        if (livro == null) {
            throw new IllegalArgumentException("Livro não encontrado.");
        }

        livro.setSaldoPaginas(livro.getSaldoPaginas() + saldo);

        livro.persist();

        return livro;
    }

}
