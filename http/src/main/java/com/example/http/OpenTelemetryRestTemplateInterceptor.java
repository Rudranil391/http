package com.example.http;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Map;


//@Component
//public class OpenTelemetryRestTemplateInterceptor implements ClientHttpRequestInterceptor {
//
////    private static final Tracer tracer = GlobalOpenTelemetry.getTracer("com.example.http.HttpService");
////
////    @Override
////    public org.springframework.http.client.ClientHttpResponse intercept(HttpRequest request, @NotNull byte[] body,
////                                                                        ClientHttpRequestExecution execution) throws IOException {
////
////        // Start a new span for the outgoing HTTP request
////        Span span = tracer.spanBuilder("HTTP Client Request")
////                .setSpanKind(SpanKind.CLIENT)
////                .startSpan();
////
////        // Attach the span to the current context
////        try (Scope scope = span.makeCurrent()) {
////            span.addEvent("client span");
////            // Inject the trace context into the HTTP headers
////            injectTraceContext(request.getHeaders(), Context.current());
////
////            // Continue with the HTTP request and response handling
////            return execution.execute(request, body);
////
////        }catch (Exception e) {
////            // Record the exception in the span
////            span.recordException(e);
////            span.setStatus(StatusCode.ERROR, "HTTP request failed: " + e.getMessage());
////            throw e;
////        }
////            finally {
////            // End the span after the request is executed
////            span.end();
////        }
////    }
////
////    private void injectTraceContext(HttpHeaders headers, Context context) {
////        // TextMapSetter implementation to set headers for trace propagation
////        TextMapSetter<HttpHeaders> setter = (carrier, key, value) -> {
////            assert carrier != null;
////            carrier.add(key, value);
////                // HttpHeaders accepts List<String> values, so we use `add` here
////        };
////
////        // Inject the current context into the request headers
////        GlobalOpenTelemetry.getPropagators().getTextMapPropagator().inject(context, headers, setter);
////    }
//}
//
