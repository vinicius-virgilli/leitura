package org.viniciusvirgilli.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.viniciusvirgilli.dto.LivroCriacaoDto;
import org.viniciusvirgilli.model.Livro;
import org.viniciusvirgilli.service.LivroService;
import org.viniciusvirgilli.service.ValidaLivroService;

import java.util.List;

@Path("/livros")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LivroResource {

    @Inject
    LivroService livroService;

    @Inject
    ValidaLivroService validaLivroService;

    @POST
    public Response criarLivro(LivroCriacaoDto livro) {
        try {
            validaLivroService.validarCriacao(livro);
            Livro criado = livroService.criarLivro(livro);
            return Response.status(Response.Status.CREATED).entity(criado).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    public List<Livro> listarLivros() {
        return livroService.listarTodos();
    }
}
