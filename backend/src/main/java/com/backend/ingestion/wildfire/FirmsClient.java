package com.backend.ingestion.wildfire;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class FirmsClient {

    private final WebClient webClient;
    private final String apiKey;

    public FirmsClient(@Value("${firms.api.key}") String apiKey) {
        this.apiKey = apiKey;

        this.webClient = WebClient.builder()
                .baseUrl("https://firms.modaps.eosdis.nasa.gov")
                .codecs(configurer ->
                        configurer.defaultCodecs()
                                .maxInMemorySize(10 * 1024 * 1024) // 10 MB buffer
                )
                .build();
    }

    public String fetchFires() {
        return webClient.get()
                .uri("/api/area/csv/{key}/VIIRS_SNPP_NRT/world/1", apiKey)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
