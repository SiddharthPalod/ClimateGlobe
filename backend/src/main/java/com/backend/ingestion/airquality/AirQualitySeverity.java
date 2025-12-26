package com.backend.ingestion.airquality;

public class AirQualitySeverity {

    public static double severityFromPm25(double pm) {
        if (pm <= 12) return 0.1;
        if (pm <= 35) return 0.3;
        if (pm <= 55) return 0.6;
        return 1.0;
    }

    public static String category(double pm) {
        if (pm <= 12) return "GOOD";
        if (pm <= 35) return "MODERATE";
        if (pm <= 55) return "POOR";
        return "SEVERE";
    }
}

