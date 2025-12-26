package com.backend.ingestion.earthquake;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class UsgsClient {

    private final WebClient webClient = WebClient.create();

    public String fetchGeoJson() {
        return webClient.get()
                .uri("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}