package com.backend.ingestion.airquality;

import com.backend.common.eventbus.EventPublisher;
import com.backend.common.model.ClimateEvent;
import com.backend.common.model.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AirQualityIngestor {

    private final OpenAqClient client;
    private final EventPublisher publisher;

    @Value("${openaq.poll.interval.ms}")
    private long pollDelay;

    // Curated global city sample so air-quality feels "worldwide" rather than local.
    // These are chosen to spread across continents and hemispheres while keeping
    // the number of API calls reasonable.
    private static final List<String> CITIES = List.of(
            // South Asia
            "Delhi",
            "Mumbai",
            "Dhaka",
            "Karachi",
            // East Asia
            "Beijing",
            "Shanghai",
            "Tokyo",
            "Seoul",
            // Southeast Asia / Oceania
            "Bangkok",
            "Jakarta",
            "Singapore",
            "Sydney",
            // Europe
            "London",
            "Paris",
            "Berlin",
            "Madrid",
            "Rome",
            "Warsaw",
            // Americas
            "New York",
            "Los Angeles",
            "Mexico City",
            "São Paulo",
            "Buenos Aires",
            "Toronto",
            "Vancouver",
            // Africa / Middle East
            "Cairo",
            "Lagos",
            "Johannesburg",
            "Nairobi",
            "Riyadh",
            "Tehran"
    );

    private static final Map<String, double[]> CITY_COORDS = Map.ofEntries(
            // South Asia
            Map.entry("Delhi", new double[]{28.6139, 77.2090}),
            Map.entry("Mumbai", new double[]{19.0760, 72.8777}),
            Map.entry("Dhaka", new double[]{23.8103, 90.4125}),
            Map.entry("Karachi", new double[]{24.8607, 67.0011}),
            // East Asia
            Map.entry("Beijing", new double[]{39.9042, 116.4074}),
            Map.entry("Shanghai", new double[]{31.2304, 121.4737}),
            Map.entry("Tokyo", new double[]{35.6762, 139.6503}),
            Map.entry("Seoul", new double[]{37.5665, 126.9780}),
            // Southeast Asia / Oceania
            Map.entry("Bangkok", new double[]{13.7563, 100.5018}),
            Map.entry("Jakarta", new double[]{-6.2088, 106.8456}),
            Map.entry("Singapore", new double[]{1.3521, 103.8198}),
            Map.entry("Sydney", new double[]{-33.8688, 151.2093}),
            // Europe
            Map.entry("London", new double[]{51.5074, -0.1278}),
            Map.entry("Paris", new double[]{48.8566, 2.3522}),
            Map.entry("Berlin", new double[]{52.5200, 13.4050}),
            Map.entry("Madrid", new double[]{40.4168, -3.7038}),
            Map.entry("Rome", new double[]{41.9028, 12.4964}),
            Map.entry("Warsaw", new double[]{52.2297, 21.0122}),
            // Americas
            Map.entry("New York", new double[]{40.7128, -74.0060}),
            Map.entry("Los Angeles", new double[]{34.0522, -118.2437}),
            Map.entry("Mexico City", new double[]{19.4326, -99.1332}),
            Map.entry("São Paulo", new double[]{-23.5505, -46.6333}),
            Map.entry("Buenos Aires", new double[]{-34.6037, -58.3816}),
            Map.entry("Toronto", new double[]{43.6532, -79.3832}),
            Map.entry("Vancouver", new double[]{49.2827, -123.1207}),
            // Africa / Middle East
            Map.entry("Cairo", new double[]{30.0444, 31.2357}),
            Map.entry("Lagos", new double[]{6.5244, 3.3792}),
            Map.entry("Johannesburg", new double[]{-26.2041, 28.0473}),
            Map.entry("Nairobi", new double[]{-1.2921, 36.8219}),
            Map.entry("Riyadh", new double[]{24.7136, 46.6753}),
            Map.entry("Tehran", new double[]{35.6892, 51.3890})
    );


    @Scheduled(fixedDelayString = "${openaq.poll.interval.ms}")
    public void ingest() {
        boolean anyReal = false;
        for (String city : CITIES) {
            try {
                double[] coords = CITY_COORDS.get(city);
                if (coords == null) {
                    log.warn("AirQualityIngestor: no coordinates for city '{}'", city);
                    continue;
                }
                String json = client.fetchLatest(coords[0], coords[1]);
                var maybeEvent = AirQualityMapper.map(json, city);
                if (maybeEvent.isPresent()) {
                    publisher.publish(maybeEvent.get());
                    anyReal = true;
                }
            } catch (Exception e) {
                log.error("AirQualityIngestor: failed fetching or mapping city '{}'", city, e);
            }
        }
        if (!anyReal) {
            log.warn("AirQualityIngestor: no real OpenAQ data produced events, emitting synthetic demo AQ points");
            emitSyntheticAirQuality();
        }
    }

    private void emitSyntheticAirQuality() {
        ClimateEvent[] demo = new ClimateEvent[]{
                aq(28.6139, 77.2090, 150, "Delhi demo"),
                aq(31.2304, 121.4737, 120, "Shanghai demo"),
                aq(40.7128, -74.0060, 60, "New York demo"),
                aq(-23.5505, -46.6333, 90, "São Paulo demo"),
                aq(51.5074, -0.1278, 40, "London demo"),
                aq(-33.9249, 18.4241, 80, "Cape Town demo")
        };
        for (ClimateEvent e : demo) {
            publisher.publish(e);
        }
    }

    private ClimateEvent aq(double lat, double lng, double pm25, String label) {
        double severity = AirQualitySeverity.severityFromPm25(pm25);
        return ClimateEvent.builder()
                .id(UUID.randomUUID().toString())
                .type(EventType.AIR_QUALITY)
                .lat(lat)
                .lng(lng)
                .severity(severity)
                .timestamp(Instant.now())
                .metadata(Map.of(
                        "city", label,
                        "pm25", pm25,
                        "category", AirQualitySeverity.category(pm25),
                        "source", "synthetic"
                ))
                .build();
    }
}
