package com.backend.ingestion.wildfire;
import com.backend.common.model.EventType;
import com.backend.common.model.ClimateEvent;
import java.time.Instant;
import java.util.*;


public class WildfireMapper {

    public static List<ClimateEvent> mapCsv(String csv) {
        List<ClimateEvent> events = new ArrayList<>();

        String[] lines = csv.split("\n");
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;

            String[] cols = line.split(",");
            if (cols.length < 10) continue;
            try {
                double lat = Double.parseDouble(cols[0]);
                double lon = Double.parseDouble(cols[1]);
                double brightness = Double.parseDouble(cols[2]);
                String confidenceStr = cols[9];
                int confidence = parseConfidence(confidenceStr);
                double severity = Math.min(
                        1.0,
                        (brightness / 500.0) * (confidence / 100.0)
                );
                ClimateEvent event = ClimateEvent.builder()
                        .id(UUID.randomUUID().toString())
                        .type(EventType.WILDFIRE)
                        .lat(lat)
                        .lng(lon)
                        .severity(severity)
                        .timestamp(Instant.now())
                        .metadata(Map.of(
                                "brightness", brightness,
                                "confidence", confidence,
                                "instrument", cols[8]
                        ))
                        .build();

                events.add(event);

            } catch (Exception ignored) {

            }
        }

        return events;
    }
    private static int parseConfidence(String raw) {
        raw = raw.trim().toLowerCase();
        return switch (raw) {
            case "l" -> 30;
            case "n" -> 60;
            case "h" -> 90;
            default -> {
                try {
                    yield Integer.parseInt(raw);
                } catch (Exception e) {
                    yield 50;
                }
            }
        };
    }
}
