package com.backend.ingestion.airquality;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class OpenAqClient {
    private final WebClient webClient;
    private String apiKey;

    public OpenAqClient(
            @Value("${openaq.base.url}") String baseUrl,
            @Value("${openaq.api.key}") String apiKey) {

        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("OpenAQ base URL is missing");
        }
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OpenAQ API key is missing");
        }
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-API-Key", apiKey)
                .defaultHeader("Accept", "application/json")
                .build();
    }

    public String fetchLatest(double lat, double lng) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/measurements")
                        .queryParam("coordinates", lat + "," + lng)
                        .queryParam("radius", 10000)
                        .queryParam("parameter", "pm25")
                        .queryParam("limit", 1)
                        .queryParam("sort", "desc")
                        .queryParam("order_by", "datetime")
                        .build())
                .retrieve()
                .onStatus(
                        status -> status.value() == 404,
                        response -> Mono.empty()   // ✅ no data is OK
                )
                .bodyToMono(String.class)
                .block();
    }

}
