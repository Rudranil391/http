package com.example.http.Controller;

//import com.example.http.Utility.ParquetWriterUtil;
//import com.example.http.database.TraceData;
//import com.example.http.database.TraceDataRepository;
import ch.qos.logback.classic.Logger;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.exporters.inmemory.InMemorySpanExporter;
import io.opentelemetry.sdk.trace.data.SpanData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class HttpController {
    //private static final Logger logger = (Logger) LoggerFactory.getLogger(HttpController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private InMemorySpanExporter inMemorySpanExporter;

    @Autowired
    private Tracer tracer;

    public HttpController(RestTemplate restTemplate, InMemorySpanExporter inMemorySpanExporter, Tracer tracer) {
        this.restTemplate = restTemplate;
        this.inMemorySpanExporter = inMemorySpanExporter;
        this.tracer = tracer;
    }

//    @Autowired
//    private TraceDataRepository traceDataRepository;

    @GetMapping("/api/hello")
    public String sayHello(@RequestParam String name) {

        // Start a new span explicitly
        Span span = tracer.spanBuilder("sayHello").startSpan();  // Start a new span
        try (Scope scope = span.makeCurrent()) {  // Use the span in the current context

            span.addEvent("What is this");
            // Log trace details
            //logger.info("Sending request to Service 2 with trace ID: {}", span.getSpanContext().getTraceId());

            // Call the second service over HTTP
            String response = restTemplate.getForObject("http://localhost:8081/api/hello?name=" + name, String.class);
            //logger.info("Received response from Service 2: {}", response);

            // Log the finished spans from the in-memory exporter
            List<SpanData> spans = inMemorySpanExporter.getFinishedSpanItems();
            //logger.info("Finished spans size: {}", spans.size());

            //for (SpanData spanData : spans) {
                //logger.info("Span ID: {}, Trace ID: {}, Name: {}", spanData.getSpanId(), spanData.getTraceId(), spanData.getName());
            //}

            return response;
        } finally {
            span.end();  // Make sure to end the span
        }
//        try {
//            byte[] parquetData = ParquetWriterUtil.writeSpansToParquet(spans);
//            TraceData traceData = new TraceData(parquetData, LocalDateTime.now(), "service_1_http");
//            traceDataRepository.save(traceData);
//            logger.info("Spans written to Parquet format and saved to MySQL as blob");
//        } catch (IOException e) {
//            logger.error("Error writing spans to Parquet format", e);
//        }

    }
}
