package org.viniciusvirgilli.scheduler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.Scheduled.ConcurrentExecution;
import org.viniciusvirgilli.service.AtualizaProgressoLeituraService;

@ApplicationScoped
public class AtualizaProgressoLeituraScheduler {

    private static final Logger LOGGER = Logger.getLogger(AtualizaProgressoLeituraScheduler.class);

    @Inject
    AtualizaProgressoLeituraService atualizaProgressoService;

    @ConfigProperty(name = "scheduler.progresso.identity", defaultValue = "atualiza-progresso-leitura")
    String schedulerIdentity;

    @ConfigProperty(name = "scheduler.progresso.cron", defaultValue = "0 0 8 * * ?")
    String schedulerCron;

    @Scheduled(
            identity = "${scheduler.progresso.identity}",
            cron = "${scheduler.progresso.cron}",
            concurrentExecution = ConcurrentExecution.SKIP
    )
    void atualizarProgresso() {
        LOGGER.info("Iniciando atualização de progresso dos livros...");
        atualizaProgressoService.atualizarProgressoDosLivros();
        LOGGER.info("Atualização concluída com sucesso.");
    }
}
