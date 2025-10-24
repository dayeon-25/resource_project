package com.example.resource.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @Qualifier("imageClient")
    public WebClient imageClient() {
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs()
                                .maxInMemorySize(200 * 1024 * 1024)) // 200MB
                        .build())
                .baseUrl("http://192.168.0.63:8000")
                .build();
    }

    @Bean
    @Qualifier("raPredict")
    public WebClient raPredict() {
        return WebClient.builder()
                .baseUrl("http://192.168.0.46:8001")
                .build();
    }
}
