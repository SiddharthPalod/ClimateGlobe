 "use client";

import { useEffect, useRef } from "react";
import { OrbitControls } from "three/examples/jsm/controls/OrbitControls.js";
import * as THREE from "three";
import Globe from "three-globe";
import type { ClimateEvent } from "../types/events";

interface GlobeSceneProps {
  wildfires: ClimateEvent[];
  earthquakes: ClimateEvent[];
  airQuality: ClimateEvent[];
  riskZones: ClimateEvent[];
  layers: {
    wildfires: boolean;
    earthquakes: boolean;
    airQuality: boolean;
    riskZones: boolean;
  };
}

export function GlobeScene(props: GlobeSceneProps) {
  const containerRef = useRef<HTMLDivElement | null>(null);
  const globeRef = useRef<Globe | null>(null);
  const sceneRef = useRef<THREE.Scene | null>(null);
  const cameraRef = useRef<THREE.PerspectiveCamera | null>(null);

  useEffect(() => {
    if (!containerRef.current) return;

    const width = containerRef.current.clientWidth || 800;
    const height = containerRef.current.clientHeight || 600;

    const scene = new THREE.Scene();
    scene.background = new THREE.Color("#f8fafc"); // light slate, matches app
    const camera = new THREE.PerspectiveCamera(45, width / height, 0.1, 1000);
    camera.position.set(0, 0, 260);
    sceneRef.current = scene;
    cameraRef.current = camera;

    const renderer = new THREE.WebGLRenderer({
      antialias: true,
      alpha: true
    });
    renderer.setPixelRatio(window.devicePixelRatio);
    renderer.setSize(width, height);

    containerRef.current.innerHTML = "";
    containerRef.current.appendChild(renderer.domElement);

    const ambientLight = new THREE.AmbientLight(0xffffff, 0.85);
    scene.add(ambientLight);

    const directionalLight = new THREE.DirectionalLight(0xffffff, 0.95);
    directionalLight.position.set(120, 80, 200);
    scene.add(directionalLight);

    const globe = new Globe()
      .globeImageUrl("//unpkg.com/three-globe/example/img/earth-blue-marble.jpg")
      .bumpImageUrl("//unpkg.com/three-globe/example/img/earth-topology.png")
      .showAtmosphere(true)
      .atmosphereColor("#0b1220")
      .atmosphereAltitude(0.18);

    const globeMaterial = globe.globeMaterial();
    globeMaterial.color = new THREE.Color("#e2e8f0"); // slate-200 style land/sea
    globeMaterial.emissive = new THREE.Color("#cbd5f5");
    globeMaterial.emissiveIntensity = 0.2;

    globeRef.current = globe;
    scene.add(globe);

    const controls = new OrbitControls(camera, renderer.domElement);
    controls.enableDamping = true;
    controls.dampingFactor = 0.08;
    controls.enablePan = false;
    controls.rotateSpeed = 0.6;
    controls.minDistance = 200;
    controls.maxDistance = 400;

    let frameId: number;
    const animate = () => {
      frameId = requestAnimationFrame(animate);
      globe.rotation.y += 0.0008;
      controls.update();
      renderer.render(scene, camera);
    };
    animate();

    const handleResize = () => {
      if (!containerRef.current) return;
      const { clientWidth, clientHeight } = containerRef.current;
      camera.aspect = clientWidth / clientHeight;
      camera.updateProjectionMatrix();
      renderer.setSize(clientWidth, clientHeight);
    };

    window.addEventListener("resize", handleResize);

    return () => {
      cancelAnimationFrame(frameId);
      window.removeEventListener("resize", handleResize);
      controls.dispose();
      renderer.dispose();
      if (containerRef.current?.contains(renderer.domElement)) {
        containerRef.current.removeChild(renderer.domElement);
      }
    };
  }, []);

  useEffect(() => {
    if (!globeRef.current) return;

    const { wildfires, earthquakes, airQuality, riskZones, layers } = props;

    const points: any[] = [];

    if (layers.wildfires) {
      for (const e of wildfires) {
        points.push({
          lat: e.lat,
          lng: e.lng,
          severity: e.severity,
          color: "#fb923c"
        });
      }
    }

    if (layers.earthquakes) {
      for (const e of earthquakes) {
        points.push({
          lat: e.lat,
          lng: e.lng,
          severity: e.severity,
          color: "#0ea5e9"
        });
      }
    }

    if (layers.airQuality) {
      for (const e of airQuality) {
        points.push({
          lat: e.lat,
          lng: e.lng,
          severity: e.severity,
          color: "#22c55e"
        });
      }
    }

    if (layers.riskZones) {
      for (const e of riskZones) {
        points.push({
          lat: e.lat,
          lng: e.lng,
          severity: e.severity,
          color: "#e11d48"
        });
      }
    }

    globeRef.current
      .pointsData(points)
      .pointColor((d: any) => d.color)
      .pointAltitude((d: any) => 0.03 + Math.min(0.15, (d.severity ?? 0) * 0.2))
      .pointRadius(0.9);
  }, [
    props.wildfires,
    props.earthquakes,
    props.airQuality,
    props.riskZones,
    props.layers
  ]);

  return (
    <div className="h-full w-full bg-slate-100">
      <div ref={containerRef} className="h-full w-full" />
    </div>
  );
}


