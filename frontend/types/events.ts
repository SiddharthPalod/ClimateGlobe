export type EventType = "WILDFIRE" | "EARTHQUAKE" | "AIR_QUALITY" | "RISK_ZONE";

export interface ClimateEvent {
  id: string;
  type: EventType;
  lat: number;
  lng: number;
  severity: number;
  timestamp: string;
  metadata?: Record<string, unknown>;
}


