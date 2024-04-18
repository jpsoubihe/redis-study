package com.example.sqlCrud.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class MetricsHelper {

    private final MeterRegistry meterRegistry;

    public MetricsHelper(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void timer(String name, String label, long requestTime) {
        meterRegistry.timer(name, Tags.of("Method", label)).record(requestTime, TimeUnit.MILLISECONDS);
    }

    public void counter(String name, String label) {
        meterRegistry.counter(name, Tags.of("Method", label)).increment();
    }
}
