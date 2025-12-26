package com.backend.ingestion.earthquake;

import com.backend.common.model.ClimateEvent;
import com.backend.common.model.EventType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.*;

public class EarthquakeMapper {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<ClimateEvent> map(String geoJson) throws Exception {
        List<ClimateEvent> events = new ArrayList<>();

        JsonNode root = mapper.readTree(geoJson);
        JsonNode features = root.path("features");

        if (!features.isArray()) {
            return events;
        }

        for (JsonNode f : features) {
            JsonNode props = f.path("properties");
            JsonNode geom = f.path("geometry");
            JsonNode coords = geom.path("coordinates");

            // ---- magnitude (skip null or small quakes) ----
            if (props.get("mag") == null || props.get("mag").isNull()) {
                continue;
            }

            double mag = props.get("mag").asDouble();
            if (mag < 3.0) continue;

            // ---- coordinates ----
            if (!coords.isArray() || coords.size() < 2) {
                continue;
            }

            double lon = coords.get(0).asDouble();
            double lat = coords.get(1).asDouble();
            double depth = coords.size() > 2 ? coords.get(2).asDouble() : 0.0;

            // ---- timestamp (USGS provides epoch millis) ----
            long timeMillis = props.path("time").asLong(0);
            Instant eventTime = timeMillis > 0
                    ? Instant.ofEpochMilli(timeMillis)
                    : Instant.now();

            // ---- severity scaling ----
            double severity = Math.min(1.0, mag / 8.0);

            ClimateEvent event = ClimateEvent.builder()
                    .id(f.path("id").asText(UUID.randomUUID().toString()))
                    .type(EventType.EARTHQUAKE)
                    .lat(lat)
                    .lng(lon)
                    .severity(severity)
                    .timestamp(eventTime)
                    .metadata(Map.of(
                            "magnitude", mag,
                            "depth_km", depth,
                            "place", props.path("place").asText("unknown"),
                            "title", props.path("title").asText(""),
                            "source", "USGS",
                            "url", props.path("url").asText("")
                    ))
                    .build();

            events.add(event);
        }

        return events;
    }
}
