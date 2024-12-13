package com.example.http.Controller;

//import com.example.http.Utility.ParquetWriterUtil;
//import com.example.http.database.TraceData;
//import com.example.http.database.TraceDataRepository;
import ch.qos.logback.classic.Logger;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.exporters.inmemory.InMemorySpanExporter;
import io.opentelemetry.sdk.trace.data.SpanData;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class HttpController {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(HttpController.class);

    @Autowired
    private RestTemplate restTemplate;

//    @Autowired
//    private InMemorySpanExporter inMemorySpanExporter;
//
//    @Autowired
//    private Tracer tracer;
//    private Meter meter;

    @Value("${api.error.endpoint}")
    private String apiErrorEndpoint;

    @Value("${api.logic.endpoint}")
    private String apiLogicEndpoint;

    public HttpController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
//        this.inMemorySpanExporter = inMemorySpanExporter;
//        this.tracer = tracer;
//        this.meter=meter;
    }


    @RequestMapping("${api.logic.endpoint}")
    public String sayHello(@RequestParam String name) {

//        meter.counterBuilder("example_counter")
//                .setDescription("An example counter")
//                .setUnit("1")
//                .build()
//                .add(1);

        // Start a new span explicitly
//        Span span = tracer.spanBuilder("sayHello").startSpan();  // Start a new span
//        try (Scope scope = span.makeCurrent()) {  // Use the span in the current context
//
//            //logger.error("njnjnjnj");
//            span.addEvent("What is this");
//            //Log trace details
//            //logger.info("Sending request to Service 2 with trace ID: {}", span.getSpanContext().getTraceId());
//
//            // Call the second service over HTTP
//            String response = restTemplate.getForObject("http://localhost:8081/api/hello?name=" + name, String.class);
//            //logger.info("Received response from Service 2: {}", response);
//
//            // Log the finished spans from the in-memory exporter
//            List<SpanData> spans = inMemorySpanExporter.getFinishedSpanItems();
//            //logger.info("Finished spans size: {}", spans.size());
//
////            for (SpanData spanData : spans) {
////                logger.info("Span ID: {}, Trace ID: {}, Name: {}", spanData.getSpanId(), spanData.getTraceId(), spanData.getName());
////            }
//
//            return response;
//        } finally {
//            span.end();  // Make sure to end the span
//        }
//        try {
//            byte[] parquetData = ParquetWriterUtil.writeSpansToParquet(spans);
//            TraceData traceData = new TraceData(parquetData, LocalDateTime.now(), "service_1_http");
//            traceDataRepository.save(traceData);
//            logger.info("Spans written to Parquet format and saved to MySQL as blob");
//        } catch (IOException e) {
//            logger.error("Error writing spans to Parquet format", e);
//        }

          // Call the second service over HTTP
           String response = restTemplate.getForObject("http://localhost:8081/api/hello?name=" + name, String.class);
           return response;

    }

    @GetMapping("/log")
    public String generateLogs() {
        logger.info("This is an INFO log");
        logger.warn("This is a WARN log");
        logger.error("This is an ERROR log");
        return "Logs generated!";
    }

//    @RequestMapping("${api.error.endpoint}")
//    public String generateError() {
//        Span span = tracer.spanBuilder("generateError").startSpan();
//        try (Scope scope = span.makeCurrent()) {
//            logger.info("Processing the /api/error endpoint...");
//            // Intentionally throwing an exception
//            throw new RuntimeException("Intentional error for testing");
//        } catch (Exception e) {
//
//            span.recordException(e);
//            span.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, "Error occurred in /api/error");
//            // Log the error and continue running the service
//            logger.error("An error occurred in /api/error endpoint: {}", e.getMessage(), e);
//            return "Error logged, but service is still running.";
//        } finally {
//            span.end();
//        }
//    }
}
