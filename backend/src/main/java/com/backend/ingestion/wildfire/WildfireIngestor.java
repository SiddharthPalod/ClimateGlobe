package com.backend.ingestion.wildfire;
import com.backend.common.eventbus.EventPublisher;
import com.backend.common.model.ClimateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WildfireIngestor {
    private final FirmsClient firmsClient;
    private final EventPublisher eventPublisher;

    @Scheduled(fixedDelayString = "${firms.poll.interval.ms}")
    public void ingest() {

        String csv = firmsClient.fetchFires();
        List<ClimateEvent> events = WildfireMapper.mapCsv(csv);

        events.stream()
                .limit(300) // safety limit
                .forEach(eventPublisher::publish);
    }
}
