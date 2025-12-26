package com.backend.common.eventbus;

import com.backend.common.model.ClimateEvent;

public interface EventPublisher {
    void publish(ClimateEvent event);
}
