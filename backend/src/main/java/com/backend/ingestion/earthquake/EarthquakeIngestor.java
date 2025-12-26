package com.backend.ingestion.earthquake;
import com.backend.common.eventbus.EventPublisher;
import com.backend.common.model.ClimateEvent;
import com.backend.common.model.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;


@Slf4j
@Component
@RequiredArgsConstructor
public class EarthquakeIngestor {

    private final UsgsClient client;
    private final EventPublisher publisher;

    // keep track of already-published quake IDs
    private final Set<String> seenIds = new HashSet<>();

    @Scheduled(fixedDelay = 2 * 60 * 1000)
    public void ingest() {
        try {
            String geoJson = client.fetchGeoJson();
            List<ClimateEvent> events = EarthquakeMapper.map(geoJson);
            int published = 0;
            for (ClimateEvent e : events) {
                if (seenIds.add(e.getId())) {
                    publisher.publish(e);
                    published++;
                }
            }
            if (published > 0) {
                log.info("EarthquakeIngestor: published {} new earthquake events", published);
            } else {
                log.debug("EarthquakeIngestor: no new earthquakes");
            }

        } catch (Exception e) {
            log.error("EarthquakeIngestor: failed to fetch or map USGS data, emitting synthetic demo quakes", e);
            emitSyntheticQuakes();
        }
    }

    private void emitSyntheticQuakes() {
        ClimateEvent[] demo = new ClimateEvent[] {
                quake(35.6895, 139.6917, 0.6, "Tokyo demo"),
                quake(37.7749, -122.4194, 0.7, "San Francisco demo"),
                quake(-33.8688, 151.2093, 0.5, "Sydney demo"),
                quake(19.4326, -99.1332, 0.8, "Mexico City demo")
        };

        for (ClimateEvent e : demo) {
            publisher.publish(e);
        }
    }

    private ClimateEvent quake(double lat, double lng, double severity, String label) {
        return ClimateEvent.builder()
                .id(UUID.randomUUID().toString())
                .type(EventType.EARTHQUAKE)
                .lat(lat)
                .lng(lng)
                .severity(severity)
                .timestamp(Instant.now())
                .metadata(Map.of(
                        "source", "synthetic",
                        "label", label
                ))
                .build();
    }
}
