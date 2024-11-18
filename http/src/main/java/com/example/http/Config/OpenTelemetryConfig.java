package com.example.http.Config;

//import com.example.http.Utility.MetricsParquetWriter;
//import com.example.http.Utility.MetricsParser;
//import com.example.http.database.MetricsData;
//import com.example.http.database.MetricsDataRepository;
import io.micrometer.core.instrument.Gauge;
//import io.micrometer.prometheus.PrometheusConfig;
//import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.opentelemetry.context.Context;
import io.opentelemetry.exporter.otlp.http.logs.*;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter;
import io.opentelemetry.exporters.inmemory.InMemorySpanExporter;
import io.opentelemetry.instrumentation.spring.webmvc.v5_3.SpringWebMvcTelemetry;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.LogRecordProcessor;
import io.opentelemetry.sdk.logs.ReadWriteLogRecord;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class OpenTelemetryConfig {

    @Bean
    @Primary
    public InMemorySpanExporter inMemorySpanExporter() {
        return InMemorySpanExporter.create();
    }

    @Bean
    public JaegerGrpcSpanExporter jaegerGrpcSpanExporter() {
        return JaegerGrpcSpanExporter.builder()
                .setEndpoint("http://localhost:14250") // Jaeger gRPC endpoint
                .setTimeout(Duration.ofSeconds(5))
                .build();
    }

//    @Bean
//    public PrometheusMeterRegistry prometheusMeterRegistry() {
//        return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
//    }

    // Configure OtlpHttpLogRecordExporter for exporting logs over HTTP

    @Bean
    public OtlpHttpLogRecordExporter otlpHttpLogExporter() {
        return OtlpHttpLogRecordExporter.builder()
                .setEndpoint("http://localhost:4318/v1/logs")  // OTLP HTTP endpoint for logs
                .setTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Bean
    @Primary
    public OpenTelemetry openTelemetry(JaegerGrpcSpanExporter jaegerExporter, InMemorySpanExporter spanExporter,OtlpHttpLogRecordExporter otlpHttpLogExporter) {

        var textMapPropagator = W3CTraceContextPropagator.getInstance();

        var resource = Resource.getDefault().merge(Resource.builder()
                .put(ResourceAttributes.SERVICE_NAME, "http-service")
                .put(ResourceAttributes.SERVICE_VERSION, "1.0.0")
                .build());

        LogRecordProcessor logRecordProcessor = BatchLogRecordProcessor.builder(otlpHttpLogExporter).build();

        // Create the LoggerProvider using SdkLoggerProviderBuilder
        // Build the LoggerProvider
        SdkLoggerProvider loggerProvider = SdkLoggerProvider.builder()
                .setResource(resource) // Add metadata for logs
                .addLogRecordProcessor(logRecordProcessor) // Attach processor
                .build();

        var tracerProvider = SdkTracerProvider.builder()
                .setResource(resource)
                .addSpanProcessor(BatchSpanProcessor.builder(jaegerExporter).build())
                .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
                .build();

        var openTelemetrySdk = OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .setLoggerProvider(loggerProvider)
                .setPropagators(ContextPropagators.create(textMapPropagator))
                .buildAndRegisterGlobal();

        return openTelemetrySdk;
    }

    @Bean
    @Primary
    public Tracer tracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer("com.example.http.HttpService");
    }

    @Bean
    public Meter meter(OpenTelemetry openTelemetry) {
        return openTelemetry.getMeter("com.example.http.HttpMetrics");
    }

//    @Bean
//    public Gauge registerGauges(PrometheusMeterRegistry meterRegistry) {
//        return Gauge.builder("http_service_active_requests", () -> getActiveRequestsCount())
//                .description("Number of active requests to HTTP Service")
//                .register(meterRegistry);
//    }

    private int getActiveRequestsCount() {
        return 5; // Replace with actual logic for active requests count
    }

    // Metrics Endpoint to expose Prometheus metrics
//    @RestController
//    static class MetricsEndpoint {
//
//        private final PrometheusMeterRegistry prometheusMeterRegistry;
//
//        @Autowired
//        public MetricsEndpoint(PrometheusMeterRegistry prometheusMeterRegistry) {
//            this.prometheusMeterRegistry = prometheusMeterRegistry;
//        }
//
//        @Autowired
//        private MetricsDataRepository metricsDataRepository;
//
//        @GetMapping("/metrics")
//        public String scrapeMetrics() throws IOException {
//            String metricsText = prometheusMeterRegistry.scrape();
//            List<MetricsParquetWriter.Metric> metrics = MetricsParser.parseMetrics(metricsText);
//            MetricsParquetWriter writer = new MetricsParquetWriter();
//            String path = writer.writeMetricsToParquet(metrics, "output/metrics.parquet");
//
//            byte[] metricsData = Files.readAllBytes(Path.of(path));
//            var metricsDataEntity = new MetricsData(metricsData, LocalDateTime.now(), "Http_Service");
//            metricsDataRepository.save(metricsDataEntity);
//            Files.delete(Path.of(path));
//
//            return metricsText + "# EOF";
//        }
//    }
    @Bean
    public SpringWebMvcTelemetry springWebMvcTelemetry(OpenTelemetry openTelemetry) {
        // Pass the OpenTelemetry instance to create the SpringWebMvcTelemetry instance
        return SpringWebMvcTelemetry.create(openTelemetry);
    }
}
