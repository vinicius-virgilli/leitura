package org.viniciusvirgilli.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.viniciusvirgilli.dto.LivroCriacaoDto;
import org.viniciusvirgilli.enums.StatusLeituraEnum;
import org.viniciusvirgilli.model.Livro;
import org.viniciusvirgilli.service.AtualizaProgressoLeituraService;
import org.viniciusvirgilli.service.LivroService;
import org.viniciusvirgilli.service.ValidaLivroService;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/livros")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LivroResource {

    private static final Logger LOG = Logger.getLogger(LivroResource.class);

    @Inject
    LivroService livroService;

    @Inject
    ValidaLivroService validaLivroService;

    @Inject
    AtualizaProgressoLeituraService atualizaProgressoLeituraService;

    @POST
    public Response criarLivro(LivroCriacaoDto livro) {
        try {
            LOG.infof("Requisição para criar livro recebida: %s", livro.getNome());

            validaLivroService.validarCriacao(livro);
            Livro criado = livroService.criarLivro(livro);
            LOG.infof("Livro criado com sucesso: ID=%d, Nome=%s", criado.id, criado.getNome());

            atualizarProgressoAposCriarAtualizarLivro();

            return Response.status(Response.Status.CREATED).entity(criado).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    public List<Livro> listarLivros() {
        LOG.info("Requisição para listar todos os livros");

        return livroService.listarTodos();
    }

    @GET
    @Path("/{livroId}")
    public Livro buscarLivroPorId(@PathParam("livroId") Integer livroId) {
        LOG.infof("Requisição para listar livro com ID=%d", livroId);

        try {
            return livroService.listarLivroPorId(livroId);
        } catch (Exception e) {
            LOG.error("Erro ao listar livro por ID", e);

            throw new InternalServerErrorException("Erro interno ao listar livro por ID");
        }
    }

    @PUT
    @Path("/status/atualizar")
    public Livro atualizarStatusLivro(@QueryParam("livroId") Integer livroId, @QueryParam("status") StatusLeituraEnum status) {
        try {
            LOG.infof("Requisição para atualizar status do livro: ID=%d, Status=%s", livroId, status);

            atualizarProgressoAposCriarAtualizarLivro();

            return livroService.atualizarStatusLivro(livroId, status);
        } catch (Exception e) {
            LOG.error("Erro ao atualizar status do livro", e);

            throw new InternalServerErrorException("Erro interno ao atualizar status do livro");
        }
    }

    @POST
    @Path("/progresso/atualizar")
    public Response atualizarProgressoManual() {
        try {
            LOG.info("Requisição manual para atualizar progresso dos livros");

            atualizaProgressoLeituraService.atualizarProgressoDosLivros();

            LOG.info("Progresso dos livros atualizado com sucesso");

            return Response.ok("Progresso dos livros atualizado com sucesso!").build();
        } catch (Exception e) {
            LOG.error("Erro ao atualizar progresso dos livros manualmente", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    public void atualizarProgressoAposCriarAtualizarLivro() {
        try {
            LOG.info("Requisição para atualizar progresso dos livros após criar ou atualizar livro");

            atualizaProgressoLeituraService.atualizarProgressoDosLivros();

            LOG.info("Progresso dos livros atualizado com sucesso");
        } catch (Exception e) {
            LOG.error("Erro ao atualizar progresso dos livros após criar ou atualizar livro", e);
        }
    }

    @DELETE
    public Response deletarLivro(@QueryParam("livroId") Integer livroId) {
        try {
            LOG.infof("Requisição para deletar livro recebida: ID=%d", livroId);

            livroService.deletarLivro(livroId);
            LOG.info("Livro deletado com sucesso!");

            atualizarProgressoAposCriarAtualizarLivro();

            return Response.noContent().build();
        } catch (Exception e) {
            LOG.error("Erro ao deletar livro", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/saldo/atualizar")
    public Livro atualizarSaldoLivro(@QueryParam("livroId") Integer livroId, @QueryParam("saldo") Integer saldo) {
        try {
            LOG.infof("Requisição para atualizar saldo do livro: ID=%d, Status=%d", livroId, saldo);

            livroService.atualizarSaldoLivro(livroId, saldo);

            LOG.info("Saldo do livro atualizado com sucesso");

            atualizarProgressoAposCriarAtualizarLivro();

            return livroService.listarLivroPorId(livroId);
        } catch (Exception e) {
            LOG.error("Erro ao atualizar saldo do livro", e);

            throw new InternalServerErrorException("Erro interno ao atualizar saldo do livro");
        }
    }
}
