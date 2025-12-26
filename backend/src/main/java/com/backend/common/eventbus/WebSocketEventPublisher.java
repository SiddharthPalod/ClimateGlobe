package com.backend.common.eventbus;
import com.backend.risk.RiskAggregatorService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.backend.common.model.ClimateEvent;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketEventPublisher implements EventPublisher {

    private final SimpMessagingTemplate messagingTemplate;
    private final RiskAggregatorService riskAggregator;

    @Override
    public void publish(ClimateEvent event) {
        messagingTemplate.convertAndSend("/topic/events", event);
        riskAggregator.ingest(event);
    }
}
