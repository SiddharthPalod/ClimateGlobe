package com.backend.risk;
import com.backend.common.eventbus.EventPublisher;
import com.backend.common.model.ClimateEvent;
import com.backend.common.model.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
@Component
@RequiredArgsConstructor
public class RiskZonePublisher {
    private final RiskAggregatorService aggregator;
    private final EventPublisher publisher;

    @Scheduled(fixedDelay = 60_000) // every 1 minute
    public void publishRiskZones() {
        aggregator.snapshot().forEach((cellId, cell) -> {
            if (cell.eventCount < 3) return; // noise filter

            String[] parts = cellId.split(":");
            int latIdx = Integer.parseInt(parts[0]);
            int lngIdx = Integer.parseInt(parts[1]);

            double centerLat = latIdx * 2 - 90 + 1;
            double centerLng = lngIdx * 2 - 180 + 1;
            ClimateEvent riskEvent = ClimateEvent.builder()
                    .id(UUID.randomUUID().toString())
                    .type(EventType.RISK_ZONE)
                    .lat(centerLat)
                    .lng(centerLng)
                    .severity(cell.score())
                    .timestamp(Instant.now())
                    .metadata(Map.of(
                            "eventCount", cell.eventCount
                    ))
                    .build();

            publisher.publish(riskEvent);
        });
    }
}