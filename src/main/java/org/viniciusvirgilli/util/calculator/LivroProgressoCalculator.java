package org.viniciusvirgilli.util.calculator;

import jakarta.enterprise.context.ApplicationScoped;
import org.viniciusvirgilli.enums.StatusLeituraEnum;
import org.viniciusvirgilli.model.Livro;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@ApplicationScoped
public class LivroProgressoCalculator {

    public LocalDate calcularDataInicioLeitura(Livro livroAnterior, LocalDate dataBase, List<DayOfWeek> diasDaSemanaLeitura) {
        LocalDate dataInicio;

        if (livroAnterior == null) {
            dataInicio = dataBase;
        } else {
            dataInicio = livroAnterior.getTerminoLeitura().plusDays(1);
        }

        while (diasDaSemanaLeitura != null && !diasDaSemanaLeitura.contains(dataInicio.getDayOfWeek())) {
            dataInicio = dataInicio.plusDays(1);
        }

        return dataInicio;
    }

    public LocalDate calcularDataTerminoLeitura(Livro livro, int paginasPorDia, LocalDate dataInicio, List<DayOfWeek> diasPermitidos) {
        int totalPaginas = livro.getTotalPaginas();
        int diasNecessarios = (int) Math.round((double) totalPaginas / paginasPorDia);
        int diasContados = 0;

        LocalDate data = dataInicio;

        while (diasContados < diasNecessarios) {
            if (diasPermitidos.contains(data.getDayOfWeek())) {
                diasContados++;
            }
            data = data.plusDays(1);
        }

        return data.minusDays(1);
    }

    public int calcularPaginaAtual(int saldoPaginas, int paginasPorDia, LocalDate dataInicio, List<DayOfWeek> diasPermitidos) {
        LocalDate hoje = LocalDate.now();
        int diasValidos = 0;
        LocalDate data = dataInicio;

        if (diasPermitidos == null) {
            while (!data.isAfter(hoje)) {
                diasValidos++;
                data = data.plusDays(1);
            }
        } else {
            while (!data.isAfter(hoje)) {
                if (diasPermitidos.contains(data.getDayOfWeek())) {
                    diasValidos++;
                }
                data = data.plusDays(1);
            }
        }

        return saldoPaginas + (diasValidos * paginasPorDia);
    }


    public String gerarTextoProgresso(int paginaAtual) {
        return "Deve estar na página " + paginaAtual + " ao final do dia.";
    }

    public StatusLeituraEnum calcularStatusLeitura(Livro livro, LocalDate dataHoje) {
        if (livro.getTerminoLeitura() != null && !dataHoje.isBefore(livro.getTerminoLeitura())) {
            return StatusLeituraEnum.LIDO;
        }

        if (livro.getInicioLeitura() != null && !dataHoje.isBefore(livro.getInicioLeitura())) {
            return StatusLeituraEnum.LENDO;
        }

        return StatusLeituraEnum.A_LER;
    }

}
