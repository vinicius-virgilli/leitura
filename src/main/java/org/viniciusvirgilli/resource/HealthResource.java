package org.viniciusvirgilli.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.viniciusvirgilli.service.LivroService;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.inject.Instance;

@Path("/health")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Health Check", description = "Endpoints para verificação de saúde da aplicação")
public class HealthResource {

    private static final Logger LOG = Logger.getLogger(HealthResource.class);

    @Inject
    LivroService livroService;
    
    @Inject
    Instance<AgroalDataSource> dataSource;
    
    @ConfigProperty(name = "quarkus.profile", defaultValue = "unknown")
    String quarkusProfile;

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
            
            // Adicionar métricas básicas de sistema
            Map<String, Object> systemInfo = new HashMap<>();
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            systemInfo.put("memory_used_mb", memoryBean.getHeapMemoryUsage().getUsed() / (1024 * 1024));
            systemInfo.put("memory_max_mb", memoryBean.getHeapMemoryUsage().getMax() / (1024 * 1024));
            systemInfo.put("uptime", formatUptime(ManagementFactory.getRuntimeMXBean().getUptime()));
            response.put("system_info", systemInfo);
            
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
            response.put("environment", quarkusProfile);
            
            // Métricas adicionais de vitalidade
            Map<String, Object> vitals = new HashMap<>();
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            vitals.put("memory_usage_percent", Math.round((double) memoryBean.getHeapMemoryUsage().getUsed() / memoryBean.getHeapMemoryUsage().getMax() * 100));
            vitals.put("thread_count", ManagementFactory.getThreadMXBean().getThreadCount());
            vitals.put("available_processors", ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors());
            response.put("vitals", vitals);
            
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
            response.put("version", "1.0.0");
            response.put("environment", quarkusProfile);
            
            // Resumo de métricas
            Map<String, Object> summary = new HashMap<>();
            RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            
            summary.put("uptime", formatUptime(runtimeBean.getUptime()));
            summary.put("memory_usage_percent", Math.round((double) memoryBean.getHeapMemoryUsage().getUsed() / memoryBean.getHeapMemoryUsage().getMax() * 100));
            summary.put("thread_count", ManagementFactory.getThreadMXBean().getThreadCount());
            
            // Adicionar informações do banco se conectado
            if (readinessStatus && dataSource.isResolvable()) {
                try {
                    AgroalDataSource agroalDS = dataSource.get();
                    if (agroalDS != null) {
                        summary.put("db_active_connections", agroalDS.getMetrics().activeCount());
                        summary.put("db_available_connections", agroalDS.getMetrics().availableCount());
                    }
                } catch (Exception e) {
                    LOG.debug("Não foi possível obter métricas do pool de conexões: " + e.getMessage());
                }
            }
            
            response.put("summary", summary);
            
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

    @GET
    @Path("/metrics")
    @Operation(
        summary = "Métricas detalhadas do sistema", 
        description = "Retorna métricas detalhadas sobre sistema, memória, CPU, disco e banco de dados"
    )
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200", 
            description = "Métricas coletadas com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Métricas do sistema",
                    value = "{\"status\": \"UP\", \"timestamp\": \"2024-01-15T10:30:00\", \"system\": {\"memory\": {}, \"cpu\": {}, \"disk\": {}}, \"database\": {}, \"application\": {}}"
                )
            )
        )
    })
    public Response detailedMetrics() {
        try {
            LOG.info("Coletando métricas detalhadas do sistema");
            
            Map<String, Object> response = new HashMap<>();
            
            // Métricas básicas
            response.put("status", "UP");
            response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            // Métricas do sistema
            response.put("system", getSystemMetrics());
            
            // Métricas do banco de dados
            response.put("database", getDatabaseMetrics());
            
            // Métricas da aplicação
            response.put("application", getApplicationMetrics());
            
            LOG.info("Métricas detalhadas coletadas com sucesso");
            return Response.ok(response).build();
            
        } catch (Exception e) {
            LOG.error("Erro ao coletar métricas detalhadas", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            errorResponse.put("error", e.getMessage());
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
        }
    }

    /**
     * Coleta métricas do sistema (memória, CPU, disco)
     */
    private Map<String, Object> getSystemMetrics() {
        Map<String, Object> systemMetrics = new HashMap<>();
        
        try {
            // Métricas de memória
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            Map<String, Object> memoryMetrics = new HashMap<>();
            
            long heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
            long heapMax = memoryBean.getHeapMemoryUsage().getMax();
            long heapCommitted = memoryBean.getHeapMemoryUsage().getCommitted();
            long nonHeapUsed = memoryBean.getNonHeapMemoryUsage().getUsed();
            long nonHeapMax = memoryBean.getNonHeapMemoryUsage().getMax();
            
            memoryMetrics.put("heap_used_mb", heapUsed / (1024 * 1024));
            memoryMetrics.put("heap_max_mb", heapMax / (1024 * 1024));
            memoryMetrics.put("heap_committed_mb", heapCommitted / (1024 * 1024));
            memoryMetrics.put("heap_usage_percent", Math.round((double) heapUsed / heapMax * 100));
            memoryMetrics.put("non_heap_used_mb", nonHeapUsed / (1024 * 1024));
            memoryMetrics.put("non_heap_max_mb", nonHeapMax > 0 ? nonHeapMax / (1024 * 1024) : -1);
            
            systemMetrics.put("memory", memoryMetrics);
            
            // Métricas de CPU
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            Map<String, Object> cpuMetrics = new HashMap<>();
            
            cpuMetrics.put("available_processors", osBean.getAvailableProcessors());
            cpuMetrics.put("system_load_average", osBean.getSystemLoadAverage());
            cpuMetrics.put("arch", osBean.getArch());
            cpuMetrics.put("os_name", osBean.getName());
            cpuMetrics.put("os_version", osBean.getVersion());
            
            systemMetrics.put("cpu", cpuMetrics);
            
            // Métricas de disco
            Map<String, Object> diskMetrics = new HashMap<>();
            File root = new File("/");
            if (!root.exists()) {
                root = new File("C:\\"); // Windows
            }
            
            long totalSpace = root.getTotalSpace();
            long freeSpace = root.getFreeSpace();
            long usedSpace = totalSpace - freeSpace;
            
            diskMetrics.put("total_space_gb", totalSpace / (1024 * 1024 * 1024));
            diskMetrics.put("free_space_gb", freeSpace / (1024 * 1024 * 1024));
            diskMetrics.put("used_space_gb", usedSpace / (1024 * 1024 * 1024));
            diskMetrics.put("usage_percent", Math.round((double) usedSpace / totalSpace * 100));
            
            systemMetrics.put("disk", diskMetrics);
            
        } catch (Exception e) {
            LOG.warn("Erro ao coletar métricas do sistema: " + e.getMessage());
            systemMetrics.put("error", "Falha ao coletar métricas do sistema");
        }
        
        return systemMetrics;
    }

    /**
     * Coleta métricas do banco de dados
     */
    private Map<String, Object> getDatabaseMetrics() {
        Map<String, Object> dbMetrics = new HashMap<>();
        
        try {
            // Status de conectividade
            boolean isConnected = checkDatabaseConnection();
            dbMetrics.put("status", isConnected ? "UP" : "DOWN");
            
            // Métricas do pool de conexões (se disponível)
            if (dataSource.isResolvable()) {
                AgroalDataSource agroalDS = dataSource.get();
                if (agroalDS != null) {
                    Map<String, Object> poolMetrics = new HashMap<>();
                    
                    try {
                        poolMetrics.put("active_connections", agroalDS.getMetrics().activeCount());
                        poolMetrics.put("available_connections", agroalDS.getMetrics().availableCount());
                        poolMetrics.put("max_used_connections", agroalDS.getMetrics().maxUsedCount());
                        poolMetrics.put("total_connections", agroalDS.getMetrics().activeCount() + agroalDS.getMetrics().availableCount());
                        poolMetrics.put("creation_count", agroalDS.getMetrics().creationCount());
                        poolMetrics.put("destroy_count", agroalDS.getMetrics().destroyCount());
                        poolMetrics.put("leak_detection_count", agroalDS.getMetrics().leakDetectionCount());
                        
                        dbMetrics.put("connection_pool", poolMetrics);
                    } catch (Exception e) {
                        LOG.warn("Erro ao coletar métricas do pool de conexões: " + e.getMessage());
                        dbMetrics.put("connection_pool_error", "Falha ao coletar métricas do pool");
                    }
                }
            }
            
        } catch (Exception e) {
            LOG.warn("Erro ao coletar métricas do banco de dados: " + e.getMessage());
            dbMetrics.put("error", "Falha ao coletar métricas do banco");
        }
        
        return dbMetrics;
    }

    /**
     * Coleta métricas da aplicação
     */
    private Map<String, Object> getApplicationMetrics() {
        Map<String, Object> appMetrics = new HashMap<>();
        
        try {
            RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
            
            // Informações básicas
            appMetrics.put("name", "leitura-api");
            appMetrics.put("version", "1.0.0");
            appMetrics.put("environment", quarkusProfile);
            
            // Tempo de execução
            long uptimeMs = runtimeBean.getUptime();
            appMetrics.put("uptime_ms", uptimeMs);
            appMetrics.put("uptime_formatted", formatUptime(uptimeMs));
            appMetrics.put("start_time", runtimeBean.getStartTime());
            
            // Informações da JVM
            appMetrics.put("jvm_name", runtimeBean.getVmName());
            appMetrics.put("jvm_version", runtimeBean.getVmVersion());
            appMetrics.put("jvm_vendor", runtimeBean.getVmVendor());
            appMetrics.put("java_version", System.getProperty("java.version"));
            
            // Argumentos da JVM
            appMetrics.put("jvm_arguments", runtimeBean.getInputArguments());
            
            // Timezone
            appMetrics.put("timezone", System.getProperty("user.timezone"));
            
            // Threads
            appMetrics.put("thread_count", ManagementFactory.getThreadMXBean().getThreadCount());
            appMetrics.put("peak_thread_count", ManagementFactory.getThreadMXBean().getPeakThreadCount());
            
        } catch (Exception e) {
            LOG.warn("Erro ao coletar métricas da aplicação: " + e.getMessage());
            appMetrics.put("error", "Falha ao coletar métricas da aplicação");
        }
        
        return appMetrics;
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