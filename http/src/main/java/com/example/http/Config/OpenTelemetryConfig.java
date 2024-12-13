package com.example.http.Config;

//import com.example.http.Utility.MetricsParquetWriter;
//import com.example.http.Utility.MetricsParser;
//import com.example.http.database.MetricsData;
//import com.example.http.database.MetricsDataRepository;
//import io.micrometer.prometheus.PrometheusConfig;
//import io.micrometer.prometheus.PrometheusMeterRegistry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentelemetry.context.Context;
import io.opentelemetry.exporter.otlp.http.logs.*;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.http.metrics.OtlpHttpMetricExporter;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.exporters.inmemory.InMemorySpanExporter;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import io.opentelemetry.instrumentation.spring.webmvc.v5_3.SpringWebMvcTelemetry;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.logs.LogRecordProcessor;
import io.opentelemetry.sdk.logs.ReadWriteLogRecord;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import io.opentelemetry.instrumentation.runtimemetrics.Classes;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

import io.opentelemetry.instrumentation.runtimemetrics.*;

import java.util.HashMap;
import java.util.function.Supplier;
import io.opentelemetry.sdk.logs.data.LogRecordData;

@Configuration
public class OpenTelemetryConfig {


//    private static final Logger log = LoggerFactory.getLogger(OpenTelemetryConfig.class);
//
//    @Bean
//    public Meter initializeRuntimeMetrics(OpenTelemetry openTelemetry) {
//        // Get the Meter from OpenTelemetry
//        Meter meter = openTelemetry.getMeter("runtime-metrics");
//
//        // Register runtime metrics observers with the OpenTelemetry instance
//        MemoryPools.registerObservers(openTelemetry); // Pass OpenTelemetry, not just Meter
//        GarbageCollector.registerObservers(openTelemetry); // Pass OpenTelemetry, not just Meter
//        Threads.registerObservers(openTelemetry); // Pass OpenTelemetry, not just Meter
//        Classes.registerObservers(openTelemetry); // Pass OpenTelemetry, not just Meter
//        Cpu.registerObservers(openTelemetry); // Pass OpenTelemetry, not just Meter
//
//        return meter;  // Return the Meter bean
//    }
//
//
//    @Bean
//    @Primary
//    public InMemorySpanExporter inMemorySpanExporter() {
//        return InMemorySpanExporter.create();
//    }
//
//    @Bean
//    public OtlpHttpLogRecordExporter otlpHttpLogExporter() {
//        return OtlpHttpLogRecordExporter.builder()
//                .setEndpoint("http://localhost:4318/v1/logs")  // OTLP HTTP endpoint for logs
//                .setTimeout(Duration.ofSeconds(5))
//                .build();
//    }
////    @Bean
////    public LogRecordProcessor logRecordProcessor(OtlpHttpLogRecordExporter logExporter) {
////        return new ExportingLogRecordProcessor(logExporter);
////    }
//
//
//    @Bean
//    @Primary
//    public OpenTelemetry openTelemetry(InMemorySpanExporter spanExporter,OtlpHttpLogRecordExporter otlpHttpLogExporter ) {
//
//        W3CTraceContextPropagator textMapPropagator = W3CTraceContextPropagator.getInstance();
//
//        Resource resource = Resource.getDefault().merge(Resource.builder()
//                .put(ResourceAttributes.SERVICE_NAME, "http1-service")
//                .put(ResourceAttributes.SERVICE_VERSION, "1.0.0")
//                .build());
//
//        LogRecordProcessor logRecordProcessor = BatchLogRecordProcessor.builder(otlpHttpLogExporter).build();
//
//
//
////        LogRecordProcessor logRecordProcessor = new LogRecordProcessor() {
////
////
////            @Override
////            public void onEmit(Context context, ReadWriteLogRecord logRecord) {
////                // Print the log details for debugging
////
////                System.out.println("Log Record Emitted:");
////                System.out.println("Context: " + context);
////                System.out.println("Log Body: " + logRecord.toLogRecordData().getBodyValue());
////
////
////            }
////
////            @Override
////            public CompletableResultCode forceFlush() {
////                return CompletableResultCode.ofSuccess();
////
////            }
////
////            @Override
////            public CompletableResultCode shutdown() {
////                return CompletableResultCode.ofSuccess();
////
////            }
////        };
//
//
//
//        // Create the LoggerProvider using SdkLoggerProviderBuilder
//        // Build the LoggerProvider
//        SdkLoggerProvider loggerProvider = SdkLoggerProvider.builder()
//                .setResource(resource) // Add metadata for logs
//                .addLogRecordProcessor(logRecordProcessor) // Attach processor
//                .build();
//
//         //Create an OTLP HTTP exporter
//        OtlpHttpSpanExporter otlpHttpSpanExporter = OtlpHttpSpanExporter.builder()
//                .setEndpoint("http://localhost:4318/v1/traces")
//                .setTimeout(Duration.ofSeconds(5))
//                .setHeaders(createHeaders())
//                .build();
//
//        //SpanExporter jsonSpanExporter = createJsonSpanExporter("http://localhost:2020");
//
//
//        // Metric exporter to send metrics to the collector
//        OtlpHttpMetricExporter metricExporter =
//                OtlpHttpMetricExporter.builder()
//                        .setEndpoint("http://localhost:4318/v1/metrics") // OpenTelemetry Collector endpoint
//                        .setTimeout(Duration.ofSeconds(5))
//                        .setHeaders(createHeaders())
//                        .build();
//
//
//        // Meter Provider for runtime metrics
//        SdkMeterProvider meterProvider = SdkMeterProvider.builder().setResource(resource)
//                .registerMetricReader(PeriodicMetricReader.builder(metricExporter)
//                        .setInterval(Duration.ofSeconds(60))
//                        .build()).build();
//
//
//
//        // Initialize runtime metrics (CPU, memory, etc.)
//
//
//        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
//                .setResource(resource)
//                .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
//                .addSpanProcessor(BatchSpanProcessor.builder(otlpHttpSpanExporter).build())
//                .build();
//
//
//        OpenTelemetrySdk openTelemetrySdk = OpenTelemetrySdk.builder()
//                .setTracerProvider(tracerProvider)
//                .setLoggerProvider(loggerProvider)
//                .setMeterProvider(meterProvider)
//                .setPropagators(ContextPropagators.create(textMapPropagator))
//                .buildAndRegisterGlobal();
//
//        OpenTelemetryAppender.install(openTelemetrySdk);
//
//        return openTelemetrySdk;
//
//    }
//
//    @Bean
//    @Primary
//    public Tracer tracer(OpenTelemetry openTelemetry) {
//        return openTelemetry.getTracer("com.example.http.HttpService");
//    }
//
//    @Bean
//    public Meter meter(OpenTelemetry openTelemetry) {
//        return openTelemetry.getMeter("com.example.http.HttpMetrics");
//    }
//
//
//
//
//    @Bean
//    public SpringWebMvcTelemetry SpringWebMvcTelemetry(OpenTelemetry openTelemetry) {
//        // Pass the OpenTelemetry instance to create the SpringWebMvcTelemetry instance
//        return SpringWebMvcTelemetry.create(openTelemetry);
//    }
//
//    // Method that returns a Supplier of headers
//    private Supplier<Map<String, String>> createHeaders() {
//        return () -> {
//            Map<String, String> headers = new HashMap<>();
//            headers.put("Content-Type", "application/json");
//            return headers;
//        };
//    }
//
//    private SpanExporter createJsonSpanExporter(String endpoint) {
//        return new SpanExporter() {
//            @Override
//            public CompletableResultCode export(Collection<SpanData> spans) {
//                try {
//                    // Convert spans to JSON
//                    String jsonPayload = convertSpansToJson(spans);
//
//                    // Send JSON to Fluent Bit HTTP input
//                    HttpURLConnection connection = (HttpURLConnection) new URL(endpoint).openConnection();
//                    connection.setRequestMethod("POST");
//                    connection.setDoOutput(true);
//                    connection.setRequestProperty("Content-Type", "application/json");
//
//                    // Write JSON payload to output stream
//                    try (OutputStream os = connection.getOutputStream()) {
//                        os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
//                        os.flush();
//                    }
//
//                    // Handle response
//                    System.out.println("system code"+connection.getResponseCode());
//                    int responseCode = connection.getResponseCode();
//                    if (responseCode == 200 || responseCode == 201) {
//                        System.out.println("Spans successfully sent to Fluent Bit.");
//                    } else {
//                        System.err.println("Failed to send spans to Fluent Bit. Response code: " + responseCode);
//                    }
//
//                    connection.disconnect();
//                } catch (IOException e) {
//                    System.err.println("Error sending spans to Fluent Bit: " + e.getMessage());
//                }
//
//                return CompletableResultCode.ofSuccess();
//            }
//
//            @Override
//            public CompletableResultCode flush() {
//                return CompletableResultCode.ofSuccess();
//            }
//
//            @Override
//            public CompletableResultCode shutdown() {
//                return null;
//            }
//
//        };
//    }
//
//    private String convertSpansToJson(Collection<SpanData> spans) {
//        // Convert spans to JSON format
//        // Example: Customize this logic as per Fluent Bit expectations
//        StringBuilder jsonBuilder = new StringBuilder("[");
//        for (SpanData span : spans) {
//            jsonBuilder.append("{")
//                    .append("\"traceId\":\"").append(span.getTraceId()).append("\",")
//                    .append("\"spanId\":\"").append(span.getSpanId()).append("\",")
//                    .append("\"name\":\"").append(span.getName()).append("\",")
//                    .append("\"startTime\":\"").append(span.getStartEpochNanos()).append("\",")
//                    .append("\"endTime\":\"").append(span.getEndEpochNanos()).append("\",")
//                    .append("\"attributes\":").append(span.getAttributes().asMap())
//                    .append("},");
//        }
//        if (jsonBuilder.length() > 1) {
//            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1); // Remove trailing comma
//        }
//        jsonBuilder.append("]");
//        //System.out.println(jsonBuilder+"\n");
//        return jsonBuilder.toString();
//    }



}
