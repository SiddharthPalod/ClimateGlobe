 "use client";

import { useState } from "react";
import dynamic from "next/dynamic";
import { useClimateEvents } from "../hooks/useClimateEvents";

type LayerKey = "wildfires" | "earthquakes" | "airQuality" | "riskZones";

const GlobeScene = dynamic(
  () => import("../components/GlobeScene").then((m) => m.GlobeScene),
  {
    ssr: false
  }
);

export default function HomePage() {
  const { wildfires, earthquakes, airQuality, riskZones, stats } = useClimateEvents();
  const [layers, setLayers] = useState<Record<LayerKey, boolean>>({
    wildfires: true,
    earthquakes: true,
    airQuality: true,
    riskZones: true
  });

  const toggleLayer = (key: LayerKey) => {
    setLayers((prev) => ({ ...prev, [key]: !prev[key] }));
  };

  return (
    <div className="h-screen w-screen bg-white text-slate-900 flex flex-col">
      <header className="flex items-center justify-between px-6 py-3 border-b border-slate-300 bg-white">
        <div className="flex flex-col">
          <span className="text-xs font-semibold tracking-widest text-emerald-700 uppercase">
            Climate Globe
          </span>
          <span className="text-sm text-slate-700">
            Live wildfires, earthquakes, air quality and risk zones on a 3D globe.
          </span>
        </div>
        <div className="flex gap-2 text-xs">
          <span className="px-2 py-1 rounded-full bg-emerald-100 text-emerald-900 border border-emerald-500">
            Backend: Live
          </span>
        </div>
      </header>

      <main className="flex-1 flex overflow-hidden">
        <section className="relative flex-1">
          <GlobeScene
            wildfires={wildfires}
            earthquakes={earthquakes}
            airQuality={airQuality}
            riskZones={riskZones}
            layers={layers}
          />
        </section>

        <aside className="w-80 border-l border-slate-300 bg-slate-50 p-4 flex flex-col gap-4">
          <div>
            <h2 className="text-xs font-semibold tracking-widest text-slate-400 uppercase mb-2">
              Layers
            </h2>
            <div className="space-y-2 text-sm text-slate-900">
              <button
                type="button"
                onClick={() => toggleLayer("wildfires")}
                className={`w-full flex items-center justify-between rounded-md border px-3 py-2 ${
                  layers.wildfires
                    ? "border-amber-600 bg-amber-100 text-amber-900"
                    : "border-slate-300 bg-white text-slate-700"
                }`}
              >
                <span>Wildfires</span>
                <span className="text-xs opacity-80">{stats.wildfires}</span>
              </button>

              <button
                type="button"
                onClick={() => toggleLayer("earthquakes")}
                className={`w-full flex items-center justify-between rounded-md border px-3 py-2 ${
                  layers.earthquakes
                    ? "border-sky-600 bg-sky-100 text-sky-900"
                    : "border-slate-300 bg-white text-slate-700"
                }`}
              >
                <span>Earthquakes</span>
                <span className="text-xs opacity-80">{stats.earthquakes}</span>
              </button>

              <button
                type="button"
                onClick={() => toggleLayer("airQuality")}
                className={`w-full flex items-center justify-between rounded-md border px-3 py-2 ${
                  layers.airQuality
                    ? "border-emerald-600 bg-emerald-100 text-emerald-900"
                    : "border-slate-300 bg-white text-slate-700"
                }`}
              >
                <span>Air Quality</span>
                <span className="text-xs opacity-80">{stats.airQuality}</span>
              </button>

              <button
                type="button"
                onClick={() => toggleLayer("riskZones")}
                className={`w-full flex items-center justify-between rounded-md border px-3 py-2 ${
                  layers.riskZones
                    ? "border-rose-600 bg-rose-100 text-rose-900"
                    : "border-slate-300 bg-white text-slate-700"
                }`}
              >
                <span>Risk Zones</span>
                <span className="text-xs opacity-80">{stats.riskZones}</span>
              </button>
            </div>
          </div>

          <div>
            <h2 className="text-xs font-semibold tracking-widest text-slate-400 uppercase mb-2">
              Stats
            </h2>
            <div className="grid grid-cols-2 gap-2 text-xs text-slate-900">
              <div className="rounded-md border border-slate-300 bg-white p-2">
                <div className="text-slate-700">Active fires</div>
                <div className="text-lg font-semibold text-amber-800">
                  {stats.wildfires}
                </div>
              </div>
              <div className="rounded-md border border-slate-300 bg-white p-2">
                <div className="text-slate-700">Quakes (recent)</div>
                <div className="text-lg font-semibold text-sky-800">
                  {stats.earthquakes}
                </div>
              </div>
              <div className="rounded-md border border-slate-300 bg-white p-2 col-span-2">
                <div className="text-slate-700">Risk zones</div>
                <div className="text-lg font-semibold text-rose-800">
                  {stats.riskZones}
                </div>
              </div>
            </div>
          </div>

          <div className="mt-auto text-[11px] text-slate-500 space-y-1">
            <h2 className="text-[10px] font-semibold tracking-widest text-slate-500 uppercase">
              Legends
            </h2>
            <p>
              <span className="text-amber-300">Wildfires</span> — pulsing orange points; size
              and glow ≈ severity.
            </p>
            <p>
              <span className="text-sky-300">Earthquakes</span> — expanding rings; radius ≈
              magnitude.
            </p>
            <p>
              <span className="text-emerald-300">Air Quality</span> — green→orange→red fog
              disks based on PM2.5.
            </p>
            <p>
              <span className="text-rose-300">Risk zones</span> — glowing patches where
              events cluster.
            </p>
          </div>
        </aside>
      </main>
    </div>
  );
}


