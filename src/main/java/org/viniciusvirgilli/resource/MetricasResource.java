package org.viniciusvirgilli.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.viniciusvirgilli.dto.MetricaCriacaoDto;
import org.viniciusvirgilli.model.Metrica;
import org.viniciusvirgilli.service.MetricaService;
import org.viniciusvirgilli.service.ValidaMetricaService;

import java.util.List;

@Path("/metricas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MetricasResource {

    @Inject
    ValidaMetricaService validaMetricaService;

    @Inject
    MetricaService metricaService;

    @POST
    public Response criarMetrica(MetricaCriacaoDto metrica) {
        try {
            validaMetricaService.validarCriacao(metrica);
            Metrica criado = metricaService.criarMetrica(metrica);
            return Response.status(Response.Status.CREATED).entity(criado).build();
        } catch (IllegalArgumentException e) {
            // System.console().printf(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }


    @GET
    public List<Metrica> listarMetricas() {
        return metricaService.listarTodos();
    }
}
