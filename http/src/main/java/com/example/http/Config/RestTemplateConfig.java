package com.example.http.Config;

import com.example.http.OpenTelemetryRestTemplateInterceptor;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.spring.webmvc.v5_3.SpringWebMvcTelemetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class RestTemplateConfig {

    @Autowired
    private final OpenTelemetry openTelemetry;
    private final OpenTelemetryRestTemplateInterceptor openTelemetryRestTemplateInterceptor;

    public RestTemplateConfig(OpenTelemetry openTelemetry, OpenTelemetryRestTemplateInterceptor openTelemetryRestTemplateInterceptor) {
        this.openTelemetry = openTelemetry;
        this.openTelemetryRestTemplateInterceptor = openTelemetryRestTemplateInterceptor;
    }

//    @Bean
//    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
//        // Create the OpenTelemetry RestTemplateInterceptor
//        RestTemplateClientHttpRequestInterceptor interceptor = new RestTemplateClientHttpRequestInterceptor(openTelemetry);
//
//        // Return a RestTemplate with the interceptor
//        return restTemplateBuilder
//                .additionalInterceptors(interceptor)  // Adds the OpenTelemetry context propagation
//                .build();
//    }

    @Bean
    public RestTemplate restTemplate(OpenTelemetryRestTemplateInterceptor openTelemetryRestTemplateInterceptor) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(openTelemetryRestTemplateInterceptor);  // Register the OpenTelemetry interceptor
        return restTemplate;
    }

}

