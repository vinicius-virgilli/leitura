package org.viniciusvirgilli.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.viniciusvirgilli.service.LivroService;

import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Path("/health")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Health Check", description = "Endpoints para verificação de saúde da aplicação")
public class HealthResource {

    private static final Logger LOG = Logger.getLogger(HealthResource.class);

    @Inject
    LivroService livroService;

    @GET
    @Path("/ready")
    @Operation(
        summary = "Verificação de prontidão", 
        description = "Verifica se a aplicação está pronta para receber requisições. Inclui verificação de conectividade com o banco de dados."
    )
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200", 
            description = "Aplicação está pronta e operacional",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Resposta de sucesso",
                    value = "{\"status\": \"UP\", \"timestamp\": \"2024-01-15T10:30:00\", \"checks\": {\"database\": \"UP\", \"application\": \"UP\"}}"
                )
            )
        ),
        @APIResponse(
            responseCode = "503", 
            description = "Aplicação não está pronta (problemas de conectividade)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Resposta de erro",
                    value = "{\"status\": \"DOWN\", \"timestamp\": \"2024-01-15T10:30:00\", \"checks\": {\"database\": \"DOWN\", \"application\": \"UP\"}}"
                )
            )
        )
    })
    public Response readinessCheck() {
        try {
            LOG.info("Executando verificação de prontidão (readiness check)");
            
            Map<String, Object> response = new HashMap<>();
            Map<String, String> checks = new HashMap<>();
            
            // Verificar conectividade com o banco de dados
            boolean databaseUp = checkDatabaseConnection();
            checks.put("database", databaseUp ? "UP" : "DOWN");
            checks.put("application", "UP");
            
            // Status geral
            boolean overallStatus = databaseUp;
            response.put("status", overallStatus ? "UP" : "DOWN");
            response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            response.put("checks", checks);
            
            if (overallStatus) {
                LOG.info("Verificação de prontidão: SUCCESS - Aplicação pronta");
                return Response.ok(response).build();
            } else {
                LOG.warn("Verificação de prontidão: FAILED - Aplicação não está pronta");
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(response).build();
            }
            
        } catch (Exception e) {
            LOG.error("Erro durante verificação de prontidão", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            Map<String, String> checks = new HashMap<>();
            checks.put("database", "DOWN");
            checks.put("application", "DOWN");
            
            errorResponse.put("status", "DOWN");
            errorResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            errorResponse.put("checks", checks);
            errorResponse.put("error", e.getMessage());
            
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(errorResponse).build();
        }
    }

    @GET
    @Path("/live")
    @Operation(
        summary = "Verificação de vitalidade", 
        description = "Verifica se a aplicação está viva e respondendo. Este endpoint é usado para detectar se a aplicação precisa ser reiniciada."
    )
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200", 
            description = "Aplicação está viva e respondendo",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Resposta de sucesso",
                    value = "{\"status\": \"UP\", \"timestamp\": \"2024-01-15T10:30:00\", \"uptime\": \"2h 30m\", \"application\": \"leitura-api\"}"
                )
            )
        ),
        @APIResponse(
            responseCode = "503", 
            description = "Aplicação não está respondendo adequadamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Resposta de erro",
                    value = "{\"status\": \"DOWN\", \"timestamp\": \"2024-01-15T10:30:00\", \"error\": \"Application not responding\"}"
                )
            )
        )
    })
    public Response livenessCheck() {
        try {
            LOG.debug("Executando verificação de vitalidade (liveness check)");
            
            Map<String, Object> response = new HashMap<>();
            
            // Verificações básicas de vitalidade
            long uptimeMs = ManagementFactory.getRuntimeMXBean().getUptime();
            String uptime = formatUptime(uptimeMs);
            
            response.put("status", "UP");
            response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            response.put("uptime", uptime);
            response.put("application", "leitura-api");
            response.put("version", "1.0.0");
            
            LOG.debug("Verificação de vitalidade: SUCCESS - Aplicação viva");
            return Response.ok(response).build();
            
        } catch (Exception e) {
            LOG.error("Erro durante verificação de vitalidade", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "DOWN");
            errorResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            errorResponse.put("error", e.getMessage());
            
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(errorResponse).build();
        }
    }

    @GET
    @Operation(
        summary = "Verificação geral de saúde", 
        description = "Endpoint geral que combina verificações de prontidão e vitalidade da aplicação"
    )
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200", 
            description = "Aplicação está saudável",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Resposta de sucesso",
                    value = "{\"status\": \"UP\", \"timestamp\": \"2024-01-15T10:30:00\", \"readiness\": \"UP\", \"liveness\": \"UP\"}"
                )
            )
        ),
        @APIResponse(
            responseCode = "503", 
            description = "Aplicação apresenta problemas de saúde"
        )
    })
    public Response generalHealthCheck() {
        try {
            LOG.info("Executando verificação geral de saúde");
            
            Map<String, Object> response = new HashMap<>();
            
            // Verificar prontidão
            boolean readinessStatus = checkDatabaseConnection();
            
            // Verificar vitalidade (sempre UP se chegou até aqui)
            boolean livenessStatus = true;
            
            boolean overallStatus = readinessStatus && livenessStatus;
            
            response.put("status", overallStatus ? "UP" : "DOWN");
            response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            response.put("readiness", readinessStatus ? "UP" : "DOWN");
            response.put("liveness", livenessStatus ? "UP" : "DOWN");
            response.put("application", "leitura-api");
            
            if (overallStatus) {
                LOG.info("Verificação geral de saúde: SUCCESS");
                return Response.ok(response).build();
            } else {
                LOG.warn("Verificação geral de saúde: FAILED");
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(response).build();
            }
            
        } catch (Exception e) {
            LOG.error("Erro durante verificação geral de saúde", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "DOWN");
            errorResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            errorResponse.put("error", e.getMessage());
            
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(errorResponse).build();
        }
    }

    /**
     * Verifica a conectividade com o banco de dados
     */
    private boolean checkDatabaseConnection() {
        try {
            // Tenta fazer uma operação simples no banco para verificar conectividade
            livroService.listarTodos();
            return true;
        } catch (Exception e) {
            LOG.warn("Falha na verificação de conectividade com o banco de dados: " + e.getMessage());
            return false;
        }
    }

    /**
     * Formata o tempo de uptime em formato legível
     */
    private String formatUptime(long uptimeMs) {
        long seconds = uptimeMs / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return String.format("%dd %dh %dm", days, hours % 24, minutes % 60);
        } else if (hours > 0) {
            return String.format("%dh %dm", hours, minutes % 60);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds % 60);
        } else {
            return String.format("%ds", seconds);
        }
    }
}