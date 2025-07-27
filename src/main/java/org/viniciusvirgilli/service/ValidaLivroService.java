package org.viniciusvirgilli.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.viniciusvirgilli.dto.LivroCriacaoDto;

@ApplicationScoped
public class ValidaLivroService {

    public void validarCriacao(LivroCriacaoDto livro) {
        if (livro == null) {
            throw new IllegalArgumentException("Livro não pode ser nulo.");
        }

        if (livro.getNome() == null || livro.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do livro é obrigatório.");
        }

        if (livro.getTotalPaginas() == null || livro.getTotalPaginas() <= 0) {
            throw new IllegalArgumentException("O número total de páginas é obrigatório e deve ser maior que zero.");
        }

        if (livro.getSaldoPaginas() != null) {
            if (livro.getSaldoPaginas() < 0) {
                throw new IllegalArgumentException("O número de páginas lidas (saldo) não pode ser negativo.");
            } else if (livro.getSaldoPaginas() > livro.getTotalPaginas()) {
                throw new IllegalArgumentException("O número de páginas lidas (saldo) não pode ser maior que o total de páginas.");
            }

        }

        livro.setNome(livro.getNome().trim());
    }
}