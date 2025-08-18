package org.viniciusvirgilli.interceptor;

import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para aplicar rate limiting em métodos ou classes.
 * Quando aplicada, limita o número de requisições por período de tempo.
 */
@InterceptorBinding
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    
    /**
     * Número máximo de requisições permitidas no período de tempo.
     * @return número máximo de requisições (padrão: 100)
     */
    int maxRequests() default 100;
    
    /**
     * Janela de tempo em minutos para o rate limiting.
     * @return janela de tempo em minutos (padrão: 1)
     */
    int timeWindowMinutes() default 1;
}