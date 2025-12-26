"use client";

import { useEffect, useMemo, useState } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import type { ClimateEvent, EventType } from "../types/events";

export interface UseClimateEventsResult {
  wildfires: ClimateEvent[];
  earthquakes: ClimateEvent[];
  airQuality: ClimateEvent[];
  riskZones: ClimateEvent[];
  stats: {
    wildfires: number;
    earthquakes: number;
    airQuality: number;
    riskZones: number;
  };
}

const MAX_EVENTS = 500;

export function useClimateEvents(): UseClimateEventsResult {
  const [events, setEvents] = useState<ClimateEvent[]>([]);

  useEffect(() => {
    const socket = new SockJS("http://localhost:8081/ws/events");
    const client = new Client({
      webSocketFactory: () => socket as unknown as WebSocket,
      reconnectDelay: 5000,
      debug: () => {}
    });

    client.onConnect = () => {
      client.subscribe("/topic/events", (msg) => {
        try {
          const payload = JSON.parse(msg.body) as ClimateEvent;
          setEvents((prev) => {
            const next = [...prev, payload];
            if (next.length > MAX_EVENTS) {
              next.splice(0, next.length - MAX_EVENTS);
            }
            return next;
          });
        } catch {
          // ignore bad payloads
        }
      });
    };

    client.activate();

    return () => {
      client.deactivate();
    };
  }, []);

  const partitioned = useMemo(() => {
    const wildfires: ClimateEvent[] = [];
    const earthquakes: ClimateEvent[] = [];
    const airQuality: ClimateEvent[] = [];
    const riskZones: ClimateEvent[] = [];

    for (const e of events) {
      switch (e.type as EventType) {
        case "WILDFIRE":
          wildfires.push(e);
          break;
        case "EARTHQUAKE":
          earthquakes.push(e);
          break;
        case "AIR_QUALITY":
          airQuality.push(e);
          break;
        case "RISK_ZONE":
          riskZones.push(e);
          break;
      }
    }

    return { wildfires, earthquakes, airQuality, riskZones };
  }, [events]);

  const stats = useMemo(
    () => ({
      wildfires: partitioned.wildfires.length,
      earthquakes: partitioned.earthquakes.length,
      airQuality: partitioned.airQuality.length,
      riskZones: partitioned.riskZones.length
    }),
    [partitioned]
  );

  return { ...partitioned, stats };
}


