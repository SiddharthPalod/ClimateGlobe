package com.backend.common.model;

import lombok.*;
import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClimateEvent {

    private String id;
    private EventType type;
    private double lat;
    private double lng;
    private double severity;
    private Instant timestamp;
    private Map<String, Object> metadata;
}
