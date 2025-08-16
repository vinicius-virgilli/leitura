package org.viniciusvirgilli.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.viniciusvirgilli.dto.MetricaCriacaoDto;
import org.viniciusvirgilli.enums.CategoriaLivroEnum;
import org.viniciusvirgilli.model.Metrica;
import org.viniciusvirgilli.service.MetricaService;
import org.viniciusvirgilli.service.ValidaMetricaService;

import java.util.List;
import org.jboss.logging.Logger;

@Path("/metricas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Métricas", description = "Operações relacionadas às métricas de leitura")
public class MetricasResource {
    private static final Logger LOG = Logger.getLogger(MetricasResource.class);

    @Inject
    ValidaMetricaService validaMetricaService;

    @Inject
    MetricaService metricaService;

    @POST
    @Operation(summary = "Criar uma nova métrica", description = "Cria uma nova métrica de leitura para uma categoria específica")
    @APIResponses(value = {
        @APIResponse(responseCode = "201", description = "Métrica criada com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Metrica.class))),
        @APIResponse(responseCode = "400", description = "Dados inválidos"),
        @APIResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public Response criarMetrica(MetricaCriacaoDto metrica) {
        try {

            LOG.infof("Requisição recebida para criar métrica da categoria: %s", metrica.getCategoria());
            validaMetricaService.validarCriacao(metrica);
            Metrica criado = metricaService.criarMetrica(metrica);
            LOG.infof("Métrica criada com sucesso para a categoria: %s", criado.getCategoria());
            return Response.status(Response.Status.CREATED).entity(criado).build();

        } catch (IllegalArgumentException e) {
            LOG.warnf("Erro de validação ao criar métrica: %s", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            LOG.errorf("Erro interno ao criar métrica: %s", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }


    @GET
    @Operation(summary = "Listar todas as métricas", description = "Retorna uma lista com todas as métricas de leitura cadastradas")
    @APIResponse(responseCode = "200", description = "Lista de métricas retornada com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Metrica.class)))
    public List<Metrica> listarMetricas() {
        LOG.info("Requisição para listar todas as métricas recebida.");
        return metricaService.listarTodos();
    }


    @DELETE
    @Operation(summary = "Deletar métrica por categoria", description = "Remove uma métrica específica baseada na categoria do livro")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Métrica deletada com sucesso"),
        @APIResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public Response deletarMetrica(
            @Parameter(description = "Categoria do livro para deletar a métrica", required = true)
            @QueryParam("categoria") CategoriaLivroEnum categoria) {
        try {
            LOG.infof("Requisição para deletar métrica da categoria: %s", categoria);
            metricaService.deletarMetrica(categoria);
            LOG.infof("Métrica deletada com sucesso para a categoria: %s", categoria);
            return Response.ok("Metrica deletada com sucesso!").build();
        } catch (Exception e) {
            LOG.errorf("Erro ao deletar métrica da categoria %s: %s", categoria, e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
