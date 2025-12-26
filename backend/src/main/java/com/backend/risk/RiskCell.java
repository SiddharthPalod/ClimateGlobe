package com.backend.risk;
import java.time.Instant;
public class RiskCell {

    public double cumulativeRisk;
    public int eventCount;
    public Instant lastUpdated;

    public void add(double severity) {
        cumulativeRisk += severity;
        eventCount++;
        lastUpdated = Instant.now();
    }

    public double score() {
        return Math.min(1.0, cumulativeRisk / eventCount);
    }
}
