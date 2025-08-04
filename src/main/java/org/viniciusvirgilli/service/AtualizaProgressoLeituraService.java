package org.viniciusvirgilli.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.viniciusvirgilli.enums.StatusLeituraEnum;
import org.viniciusvirgilli.model.Livro;
import org.viniciusvirgilli.model.Metrica;
import org.viniciusvirgilli.util.calculator.LivroProgressoCalculator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

@ApplicationScoped
public class AtualizaProgressoLeituraService {

    @Inject
    LivroService livroService;

    @Inject
    MetricaService metricaService;

    @Inject
    LivroProgressoCalculator livroProgressoCalculator;

    @Transactional
    public void atualizarProgressoDosLivros() {
        List<Metrica> metricas = metricaService.listarTodos();

        if (metricas.isEmpty()) return;

        for (Metrica metrica : metricas) {
            processarLivrosDaCategoria(metrica);
        }
    }

    private void processarLivrosDaCategoria(Metrica metrica) {
        if (metrica.getPaginasPorDia() == null || metrica.getDataBase() == null) return;

        List<DayOfWeek> diasDaSemanaLeitura = metrica.getDiasSemanaLeitura();

        List<Livro> livros = livroService.buscarPorCategoria(metrica.getCategoria());
        livros.sort(Comparator.comparing(Livro::getOrdemDeLeitura));

        int totalPaginasLidas = 0;
        int totalLivrosLidos = 0;

        for (int i = 0; i < livros.size(); i++) {
            Livro livro = livros.get(i);

            LocalDate dataInicio = livroProgressoCalculator.calcularDataInicioLeitura(i == 0 ? null : livros.get(i - 1), metrica.getDataBase(), diasDaSemanaLeitura);
            LocalDate dataTermino = livroProgressoCalculator.calcularDataTerminoLeitura(livro, metrica.getPaginasPorDia(), dataInicio, diasDaSemanaLeitura);
            int paginaAtual = livroProgressoCalculator.calcularPaginaAtual(livro.getSaldoPaginas(), metrica.getPaginasPorDia(), dataInicio, diasDaSemanaLeitura);

            atualizarLivroComProgresso(livro, dataInicio, dataTermino, paginaAtual, diasDaSemanaLeitura);

            int vezesLido = livro.getQuantidadeVezesLido() != null ? livro.getQuantidadeVezesLido() : 0;
            int totalPaginasLivro = livro.getTotalPaginas() != null ? livro.getTotalPaginas() : 0;

            totalPaginasLidas += vezesLido * totalPaginasLivro;
            totalLivrosLidos += vezesLido;
        }

        metrica.setTotalPaginasLidas(totalPaginasLidas);
        metrica.setTotalLivrosLidos(totalLivrosLidos);
    }

    private void atualizarLivroComProgresso(Livro livro, LocalDate dataInicio, LocalDate dataTermino, int paginaAtual, List<DayOfWeek> diasDaSemanaLeitura) {
        livro.setInicioLeitura(dataInicio);
        livro.setTerminoLeitura(dataTermino);
        atualizarPaginas(livro, paginaAtual);
        atualizarProgressoTexto(livro, paginaAtual, diasDaSemanaLeitura);
        atualizarStatus(livro);
    }

    private void atualizarPaginas(Livro livro, int paginaAtual) {
        if (paginaAtual >= livro.getTotalPaginas()) {
            livro.setPaginaAtual(livro.getTotalPaginas());
            livro.setPaginasLidas(livro.getTotalPaginas());
        } else {
            livro.setPaginaAtual(paginaAtual);
            livro.setPaginasLidas(paginaAtual);
        }
    }

    private void atualizarProgressoTexto(Livro livro, int paginaAtual, List<DayOfWeek> diasSemanaLeitura) {
        LocalDate hoje = LocalDate.now();
        DayOfWeek diaSemanaHoje = hoje.getDayOfWeek();
        LocalDate inicio = livro.getInicioLeitura();
        LocalDate fim = livro.getTerminoLeitura();
        boolean hojeEhDiaDeLeitura = diasSemanaLeitura != null && diasSemanaLeitura.contains(diaSemanaHoje);

        if (inicio != null && fim != null && !hoje.isBefore(inicio) && !hoje.isAfter(fim) && hojeEhDiaDeLeitura) {
            if (paginaAtual >= livro.getTotalPaginas()) {
                livro.setProgresso("Parabéns, hoje você termina ou terminou de ler o livro " + livro.getNome());
            } else {
                livro.setProgresso("Deve estar na página " + paginaAtual + " ao final do dia.");
            }
        } else {
            int vezesLido = livro.getQuantidadeVezesLido() != null ? livro.getQuantidadeVezesLido() : 0;

            if (vezesLido > 0) {
                livro.setProgresso("Já foi lido " + vezesLido + " vez(es).");
            } else {
                livro.setProgresso("Aguardando ser lido pela primeira vez.");
            }
        }
    }


    private void atualizarStatus(Livro livro) {
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        StatusLeituraEnum status = livroProgressoCalculator.calcularStatusLeitura(livro, hoje);
        livro.setStatus(status);

        if (status == StatusLeituraEnum.LIDO) {
            Integer vezesLido = livro.getQuantidadeVezesLido();
            livro.setQuantidadeVezesLido((vezesLido == null ? 1 : vezesLido + 1));
        }
    }



}
