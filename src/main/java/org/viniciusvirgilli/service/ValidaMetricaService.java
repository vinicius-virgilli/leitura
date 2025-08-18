package org.viniciusvirgilli.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.viniciusvirgilli.dto.MetricaCriacaoDto;
import org.viniciusvirgilli.enums.CategoriaLivroEnum;
import org.viniciusvirgilli.model.Usuario;

@ApplicationScoped
public class ValidaMetricaService {

    @Inject
    UsuarioService usuarioService;

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

    public void validarCriacao(MetricaCriacaoDto dto, Long usuarioId) {
        // Validações básicas do DTO
        validarCriacao(dto);
        
        // Validação do usuário
        if (usuarioId == null) {
            throw new IllegalArgumentException("ID do usuário não pode ser nulo.");
        }
        
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
    }
}
