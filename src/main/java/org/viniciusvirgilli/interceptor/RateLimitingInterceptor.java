package org.viniciusvirgilli.interceptor;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheManager;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RateLimit
@Interceptor
@Priority(1000)
public class RateLimitingInterceptor {

    private static final Logger LOG = Logger.getLogger(RateLimitingInterceptor.class);
    
    // Mapa para controlar requisições por IP/usuário
    private final ConcurrentHashMap<String, RequestCounter> requestCounters = new ConcurrentHashMap<>();
    
    // Configurações padrão de rate limiting
    private static final int DEFAULT_MAX_REQUESTS = 100; // 100 requests
    private static final int DEFAULT_TIME_WINDOW_MINUTES = 1; // por minuto
    
    @Inject
    CacheManager cacheManager;

    @AroundInvoke
    public Object rateLimit(InvocationContext context) throws Exception {
        String clientId = getClientIdentifier(context);
        
        if (isRateLimited(clientId)) {
            LOG.warnf("Rate limit exceeded for client: %s", clientId);
            throw new RateLimitExceededException("Rate limit exceeded. Try again later.");
        }
        
        return context.proceed();
    }
    
    private String getClientIdentifier(InvocationContext context) {
        // Em um cenário real, você pegaria o IP do cliente ou ID do usuário
        // Por simplicidade, usamos o nome do método como identificador
        return context.getMethod().getDeclaringClass().getSimpleName() + "." + context.getMethod().getName();
    }
    
    private boolean isRateLimited(String clientId) {
        RequestCounter counter = requestCounters.computeIfAbsent(clientId, k -> new RequestCounter());
        
        LocalDateTime now = LocalDateTime.now();
        
        // Limpa contadores antigos
        if (counter.windowStart.isBefore(now.minus(DEFAULT_TIME_WINDOW_MINUTES, ChronoUnit.MINUTES))) {
            counter.reset(now);
        }
        
        // Incrementa contador
        int currentCount = counter.count.incrementAndGet();
        
        // Verifica se excedeu o limite
        return currentCount > DEFAULT_MAX_REQUESTS;
    }
    
    private static class RequestCounter {
        private final AtomicInteger count = new AtomicInteger(0);
        private volatile LocalDateTime windowStart = LocalDateTime.now();
        
        public void reset(LocalDateTime newStart) {
            count.set(0);
            windowStart = newStart;
        }
    }
    
    public static class RateLimitExceededException extends RuntimeException {
        public RateLimitExceededException(String message) {
            super(message);
        }
    }
}