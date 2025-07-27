package org.viniciusvirgilli.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.viniciusvirgilli.dto.LivroCriacaoDto;
import org.viniciusvirgilli.model.Livro;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class LivroService {

    @Transactional
    public Livro criarLivro(LivroCriacaoDto dto) {
        if (livroJaExiste(dto.getNome())) {
            throw new IllegalArgumentException("O livro já existe.");
        }

        List<Livro> livros = Livro.listAll();

        Livro livro;

        if (livros.isEmpty()) {
            livro = criarPrimeiroLivro(dto);
        } else {
            livro = criarLivroComListaExistente(dto, livros.size());
        }

        livro.persist();
        return livro;
    }

    private boolean livroJaExiste(String nome) {
        return Livro.find("nome", nome).firstResult() != null;
    }

    private Livro criarPrimeiroLivro(LivroCriacaoDto dto) {
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
        livro.setStatus(null);
        livro.setTerminoLeitura(null);

        return livro;
    }

    private Livro criarLivroComListaExistente(LivroCriacaoDto dto, int tamanhoLista) {
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
        livro.setStatus(null);
        livro.setTerminoLeitura(null);

        return livro;
    }

    public List<Livro> listarTodos() {
        return Livro.listAll();
    }
}
