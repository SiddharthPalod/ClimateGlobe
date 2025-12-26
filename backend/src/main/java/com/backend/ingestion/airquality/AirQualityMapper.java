package com.backend.ingestion.airquality;

import com.backend.common.model.ClimateEvent;
import com.backend.common.model.EventType;
import com.fasterxml.jackson.databind.*;

import java.time.Instant;
import java.util.*;

public class AirQualityMapper {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static Optional<ClimateEvent> map(String json, String city) {
        try {
            JsonNode root = mapper.readTree(json);
            JsonNode results = root.get("results");

            if (results == null || results.isEmpty()) return Optional.empty();

            JsonNode r = results.get(0);
            JsonNode measurements = r.get("measurements");

            if (measurements == null || measurements.isEmpty()) return Optional.empty();

            double pm25 = measurements.get(0).get("value").asDouble();
            double lat = r.get("coordinates").get("latitude").asDouble();
            double lon = r.get("coordinates").get("longitude").asDouble();

            double severity = AirQualitySeverity.severityFromPm25(pm25);

            ClimateEvent event = ClimateEvent.builder()
                    .id(UUID.randomUUID().toString())
                    .type(EventType.AIR_QUALITY)
                    .lat(lat)
                    .lng(lon)
                    .severity(severity)
                    .timestamp(Instant.now())
                    .metadata(Map.of(
                            "city", city,
                            "pm25", pm25,
                            "category", AirQualitySeverity.category(pm25)
                    ))
                    .build();

            return Optional.of(event);

        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
