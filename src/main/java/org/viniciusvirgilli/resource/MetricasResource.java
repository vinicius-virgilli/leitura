package org.viniciusvirgilli.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
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
public class MetricasResource {
    private static final Logger LOG = Logger.getLogger(MetricasResource.class);

    @Inject
    ValidaMetricaService validaMetricaService;

    @Inject
    MetricaService metricaService;

    @POST
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
    public List<Metrica> listarMetricas() {
        LOG.info("Requisição para listar todas as métricas recebida.");
        return metricaService.listarTodos();
    }


    @DELETE
    public Response deletarMetrica(@QueryParam("categoria") CategoriaLivroEnum categoria) {
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
