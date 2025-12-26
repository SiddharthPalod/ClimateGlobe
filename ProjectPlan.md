# 🌍 Climate & Environment Real-Time Globe
## Scope
1. 🔥 Wildfires (NASA FIRMS)
2. 🌎 Earthquakes (USGS)
3. 🌫 Air Quality (OpenAQ)

## Tech stack 

**Backend**
* Java 17
* Spring Boot 3.x
* WebSockets
* Scheduled jobs
* WebClient

**Frontend**
* React + Vite + TypeScript
* three / three-globe
* @react-three/fiber (alpha)
* @react-three/drei
* tailwindcss
* motion, clsx, tailwind-merge


## ⚙️ PHASE 1 — Backend Skeleton (Day 1–2)

**Goal:** Backend runs, WebSocket works, no real data yet.

### Step 1.1 — Spring Boot setup

* Create Spring Boot project
* Add dependencies:

  * Spring Web
  * WebSocket
  * Validation
  * Lombok

### Step 1.2 — Folder structure

src/main/java/com/globe/
 ├── config/
 ├── controller/
 ├── ingestion/
 ├── model/
 ├── service/
 └── ClimateGlobeApplication.java

### Step 1.3 — Unified Event Model

Create **ClimateEvent**:
* id
* type
* lat, lng
* severity (0–1)
* timestamp
* metadata

### Step 1.4 — WebSocket configuration

* Configure `/ws/events`
* Configure `/topic/events`
* Test using dummy events
Backend should push mock events every 5 seconds

## 🔥 PHASE 2 — First Real Data Layer (Wildfires) (Day 3–4)

**Goal:** First real live data flowing to frontend.

### Step 2.1 — NASA FIRMS ingestion
* Use WebClient
* Poll FIRMS every 10–15 minutes
* Parse fire points
* Convert to ClimateEvent

### Step 2.2 — Severity scoring
* Confidence + brightness → severity
* Normalize to 0–1

### Step 2.3 — Stream to WebSocket
* Publish events as soon as fetched
* Buffer last N events (100–300)
You see wildfire JSON events over WebSocket


## 🌎 PHASE 3 — Earthquakes (Day 4–5)

**Goal:** Second data source, reusable ingestion pattern.

### Step 3.1 — USGS API integration

* Poll every 1–5 minutes
* Filter magnitude ≥ 3.0

### Step 3.2 — Earthquake severity

* magnitude / 8.0
* clamp to 1.0

### Step 3.3 — Metadata

* depth
* place
* magnitude

## 🌫 PHASE 4 — Air Quality (Day 6–7)

**Goal:** Introduce continuous sensor data.


### Step 4.1 — OpenAQ ingestion
* Poll major cities only (performance)
* Fetch PM2.5


### Step 4.2 — AQI normalization

* PM2.5 → AQI bucket
* Map AQI → severity


### Step 4.3 — Aggregation

* One event per city per interval
* Avoid spamming WebSocket
Backend is now multi-source and stable.


## 🌀 PHASE 5 — Risk Index (Day 8)

**Goal:** A real-time spatial aggregation of environmental threats
showing where the world is most at risk right now.

Divide the globe into grid cells
Aggregate events in each cell
Compute a risk score (0–1)
Stream RISK_ZONE events
Frontend renders glowing regions / heat patches


## 🎨 PHASE 6 — Frontend

**Goal:** Globe renders smoothly.

### Step 6.1 — Vite + Tailwind
* Light theme
* Fullscreen canvas

### Step 6.2 — R3F canvas

* Earth sphere
* Atmosphere
* Orbit controls
* Stars background


### Step 6.3 — RISK_ZONE events:

Radius proportional to severity
* Color gradient (green -> yellow → red)
* Slow pulse animation
* Fade after next update
* This will look insane on a 3D globe.

## 🌍 PHASE 7 — WebSocket Integration (Day 3)
**Goal:** Live data reaches globe.

### Step 7.1 — WebSocket hook
* Connect to backend
* Maintain event buffer

### Step 7.2 — Event dispatcher
* Route events by type
* Store in separate arrays


## 🔥 PHASE 8 — Layer Rendering (Day 4–6)

**Goal:** Each layer feels distinct.

### Step 8.1 — Wildfires
* Pulsing points
* Glow intensity = severity
* Auto fade after X minutes


### Step 8.2 — Earthquakes
* Expanding rings
* Duration ∝ magnitude

### Step 8.3 — Air Quality
* Heatmap / fog overlay
* Smooth interpolation

## 🎛 PHASE 9 — UI & Controls (Day 7)

**Goal:** Usable, not just pretty.

### Step 9.1 — Layer toggles
* Enable / disable layers
* Animate transitions (motion)

### Step 9.2 — Stats panel
* Active fires
* Quakes today
* Worst AQI city

### Step 9.3 — Legends
* Color scales
* Severity explanation


## ⚡ PHASE 10 — Performance & Polish (Day 8–9)
**Goal:** Smooth on normal laptops.

### Step 10.1 — Optimization
* Cap events (~500–1000)
* Use memoization
* Disable shadows
* Throttle WebSocket updates

### Step 10.2 — UX polish
* Smooth camera motion
* Tooltip on hover
* Subtle sound cues (optional)

## README

Include:
* Architecture diagram
* Data sources
* Screenshots
* Live demo link

### Resume bullets

> Built a real-time climate intelligence platform using Spring Boot, WebSockets, and React Three Fiber, visualizing live wildfire, earthquake, air quality on a 3D globe.
