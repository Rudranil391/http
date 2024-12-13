package com.example.http.Config;

import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.logs.data.LogRecordData;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;
import io.opentelemetry.sdk.logs.ReadWriteLogRecord;
import io.opentelemetry.sdk.logs.LogRecordProcessor;

import java.util.Collections;

public class ExportingLogRecordProcessor implements LogRecordProcessor {

    private final LogRecordExporter logRecordExporter;

    public ExportingLogRecordProcessor(LogRecordExporter logRecordExporter) {
        this.logRecordExporter = logRecordExporter;
    }

    @Override
    public void onEmit(Context context, ReadWriteLogRecord logRecord) {
        // Convert to immutable LogRecordData and export
        LogRecordData logRecordData = logRecord.toLogRecordData();
        System.out.println("Log Record Emitted: " + logRecordData.getBody().asString());

        // Export the log using the LogRecordExporter
        logRecordExporter.export(Collections.singletonList(logRecordData));
    }


    @Override
    public CompletableResultCode shutdown() {
        return logRecordExporter.shutdown();
    }
}

