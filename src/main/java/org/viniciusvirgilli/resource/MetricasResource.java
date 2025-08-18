package org.viniciusvirgilli.resource;

import jakarta.annotation.security.RolesAllowed;
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
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.viniciusvirgilli.dto.MetricaCriacaoDto;
import org.viniciusvirgilli.enums.CategoriaLivroEnum;
import org.viniciusvirgilli.interceptor.RateLimit;
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
    @Path("/usuario/{usuarioId}")
    @RolesAllowed({"ADMIN", "MODERATOR", "USER"})
    @SecurityRequirement(name = "jwt")
    @RateLimit(maxRequests = 30, timeWindowMinutes = 1)
    @Operation(summary = "Criar uma nova métrica para um usuário", description = "Cria uma nova métrica de leitura para uma categoria específica associada a um usuário")
    @APIResponses(value = {
        @APIResponse(responseCode = "201", description = "Métrica criada com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Metrica.class))),
        @APIResponse(responseCode = "400", description = "Dados inválidos"),
        @APIResponse(responseCode = "404", description = "Usuário não encontrado"),
        @APIResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public Response criarMetrica(
            @Parameter(description = "ID do usuário", required = true)
            @PathParam("usuarioId") Long usuarioId,
            MetricaCriacaoDto metrica) {
        try {
            LOG.infof("Requisição recebida para criar métrica da categoria: %s para usuário: %d", metrica.getCategoria(), usuarioId);
            validaMetricaService.validarCriacao(metrica, usuarioId);
            Metrica criado = metricaService.criarMetrica(metrica, usuarioId);
            LOG.infof("Métrica criada com sucesso para a categoria: %s e usuário: %d", criado.getCategoria(), usuarioId);
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
    @Path("/usuario/{usuarioId}")
    @RolesAllowed({"ADMIN", "MODERATOR", "USER"})
    @SecurityRequirement(name = "jwt")
    @RateLimit
    @Operation(summary = "Listar métricas de um usuário", description = "Retorna uma lista de todas as métricas de leitura de um usuário específico")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Lista de métricas retornada com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Metrica.class))),
        @APIResponse(responseCode = "404", description = "Usuário não encontrado"),
        @APIResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public Response listarMetricasPorUsuario(
            @Parameter(description = "ID do usuário", required = true)
            @PathParam("usuarioId") Long usuarioId) {
        try {
            LOG.infof("Requisição recebida para listar métricas do usuário: %d", usuarioId);
            List<Metrica> metricas = metricaService.listarMetricasPorUsuario(usuarioId);
            LOG.infof("Retornando %d métricas para o usuário: %d", metricas.size(), usuarioId);
            return Response.ok(metricas).build();
        } catch (Exception e) {
            LOG.errorf("Erro interno ao listar métricas do usuário %d: %s", usuarioId, e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @RolesAllowed({"ADMIN", "MODERATOR"})
    @SecurityRequirement(name = "jwt")
    @RateLimit
    @Operation(summary = "Listar todas as métricas (apenas ADMIN/MODERATOR)", description = "Retorna uma lista de todas as métricas de leitura de todos os usuários")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Lista de métricas retornada com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Metrica.class))),
        @APIResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public Response listarTodasMetricas() {
        try {
            LOG.info("Requisição recebida para listar todas as métricas (admin/moderator)");
            List<Metrica> metricas = metricaService.listarTodos();
            LOG.infof("Retornando %d métricas", metricas.size());
            return Response.ok(metricas).build();
        } catch (Exception e) {
            LOG.errorf("Erro interno ao listar métricas: %s", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }


    @DELETE
    @Path("/usuario/{usuarioId}/{id}")
    @RolesAllowed({"ADMIN", "MODERATOR", "USER"})
    @SecurityRequirement(name = "jwt")
    @Operation(summary = "Deletar uma métrica de um usuário", description = "Remove uma métrica específica pelo ID associada a um usuário")
    @APIResponses(value = {
        @APIResponse(responseCode = "204", description = "Métrica deletada com sucesso"),
        @APIResponse(responseCode = "404", description = "Métrica ou usuário não encontrado"),
        @APIResponse(responseCode = "403", description = "Métrica não pertence ao usuário especificado"),
        @APIResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public Response deletarMetrica(
            @Parameter(description = "ID do usuário", required = true)
            @PathParam("usuarioId") Long usuarioId,
            @Parameter(description = "ID da métrica a ser deletada", required = true)
            @PathParam("id") Long id) {
        try {
            LOG.infof("Requisição recebida para deletar métrica com ID: %d do usuário: %d", id, usuarioId);
            metricaService.deletarMetrica(id, usuarioId);
            LOG.infof("Métrica com ID %d do usuário %d deletada com sucesso", id, usuarioId);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            LOG.warnf("Métrica com ID %d do usuário %d não encontrada: %s", id, usuarioId, e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (SecurityException e) {
            LOG.warnf("Métrica com ID %d não pertence ao usuário %d: %s", id, usuarioId, e.getMessage());
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        } catch (Exception e) {
            LOG.errorf("Erro interno ao deletar métrica com ID %d do usuário %d: %s", id, usuarioId, e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
