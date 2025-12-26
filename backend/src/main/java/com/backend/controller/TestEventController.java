package com.backend.controller;
import com.backend.common.eventbus.EventPublisher;
import com.backend.common.model.ClimateEvent;
import com.backend.common.model.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TestEventController {
    private final EventPublisher eventPublisher;

    @PostMapping("/test/publish")
    public void publishTestEvent() {
        ClimateEvent event = ClimateEvent.builder()
                .id(UUID.randomUUID().toString())
                .type(EventType.WILDFIRE)
                .lat(19.07)
                .lng(72.87)
                .severity(0.8)
                .timestamp(Instant.now())
                .build();

        eventPublisher.publish(event);
    }
}
