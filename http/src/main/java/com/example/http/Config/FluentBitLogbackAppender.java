package com.example.http.Config;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FluentBitLogbackAppender extends AppenderBase<LoggingEvent> {
    private static final Logger logger = LoggerFactory.getLogger(FluentBitLogbackAppender.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS); // Prevent failure on empty beans
    private String fluentBitEndpoint;

    @Override
    protected void append(LoggingEvent eventObject) {
        try {
            // Map log event to a simpler serializable format
            LogDTO logDTO = new LogDTO(eventObject.getLevel().toString(),
                    eventObject.getMessage(),
                    eventObject.getLoggerName());

            String jsonLog = objectMapper.writeValueAsString(logDTO);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(jsonLog, headers);
            restTemplate.postForObject(fluentBitEndpoint, entity, String.class);
        } catch (Exception e) {
            logger.error("Error sending log to Fluent Bit", e);
        }
    }

    public void setFluentBitEndpoint(String fluentBitEndpoint) {
        this.fluentBitEndpoint = fluentBitEndpoint;
    }

    // DTO for serializable log fields
    private static class LogDTO {
        private final String level;
        private final String message;
        private final String loggerName;

        public LogDTO(String level, String message, String loggerName) {
            this.level = level;
            this.message = message;
            this.loggerName = loggerName;
        }

        // Getters required for serialization
        public String getLevel() {
            return level;
        }

        public String getMessage() {
            return message;
        }

        public String getLoggerName() {
            return loggerName;
        }
    }
}