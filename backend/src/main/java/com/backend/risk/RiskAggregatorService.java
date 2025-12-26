package com.backend.risk;
import com.backend.common.model.ClimateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class RiskAggregatorService {

    private final Map<String, RiskCell> grid = new ConcurrentHashMap<>();

    private static final double CELL_SIZE = 2.0;

    public void ingest(ClimateEvent event) {
        String cellId = cellId(event.getLat(), event.getLng());
        grid.computeIfAbsent(cellId, id -> new RiskCell())
                .add(event.getSeverity());
    }

    public Map<String, RiskCell> snapshot() {
        return Map.copyOf(grid);
    }

    private String cellId(double lat, double lng) {
        int latIdx = (int) Math.floor((lat + 90) / CELL_SIZE);
        int lngIdx = (int) Math.floor((lng + 180) / CELL_SIZE);
        return latIdx + ":" + lngIdx;
    }
}
