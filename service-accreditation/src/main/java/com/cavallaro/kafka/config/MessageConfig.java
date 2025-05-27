package com.cavallaro.kafka.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class MessageConfig {

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages"); // Nombre base de tus archivos de mensajes (messages_es.properties, messages_es.properties, etc.)
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(3600); // Tiempo en segundos para recargar los archivos de mensajes (opcional)
        messageSource.setUseCodeAsDefaultMessage(true); // Si no se encuentra el código, usa el código como mensaje
        return messageSource;
    }

    @Bean
    public LocalValidatorFactoryBean getValidator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }
}