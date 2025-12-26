package com.backend.ingestion.mock;
import com.backend.common.eventbus.EventPublisher;
import com.backend.common.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

//@Component
@RequiredArgsConstructor
public class MockIngestor {

    private final EventPublisher eventPublisher;

    @Scheduled(fixedRate = 5000)
    public void ingest() {

        ClimateEvent event = ClimateEvent.builder()
                .id(UUID.randomUUID().toString())
                .type(EventType.WILDFIRE)
                .lat(random(-60, 60))
                .lng(random(-180, 180))
                .severity(Math.random())
                .timestamp(Instant.now())
                .metadata(Map.of("source", "mock"))
                .build();

        eventPublisher.publish(event);
    }

    private double random(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }
}
