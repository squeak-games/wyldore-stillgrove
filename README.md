# Wyldore: Stillgrove

**A Spatial Creature-Tending Experience for XREAL Project Aura**

---

| Field | Detail |
|:---|:---|
| **Project** | Wyldore: Stillgrove |
| **Studio** | Squeak Games Inc. |
| **Status** | Under active development (Catalyst Program Submission Draft) |
| **License** | Apache 2.0 |
| **Target Platform** | Android XR — XREAL Project Aura |
| **Target Hardware** | XREAL Project Aura (wired XR glasses, Snapdragon compute puck, electrochromic dimming, hand tracking) |
| **Primary Vertical** | Gaming × Health & Wellness |
| **Projected Launch Window** | 9 to 12 months from program acceptance |
| **Repository** | squeak-games/wyldore-stillgrove |

---

## What Stillgrove Is

Wyldore: Stillgrove is a spatial creature-tending experience built natively for XREAL Project Aura. It transforms the user's physical space — their living room, their desk, their kitchen table — into a fragile mythic ecosystem populated by small, autonomous creatures from the Wyldore universe. The creatures arrive as displaced survivors from a world defined by climate erosion. They anchor themselves to the user's shelves, cluster beneath the warmth of a desk lamp, nest in the quiet corner behind the couch.

The biome is always in entropy. Colors desaturate. Structures slowly dissolve. Creatures curl inward. The player's role isn't to fight, solve, or optimize — it's to *tend*. Using hand tracking, they offer warmth with cupped palms, build tiny shelters by pinching and placing materials, and guide lost creatures toward one another. Crucially, the creatures also tend *each other*. A bonded pair will share warmth. A nesting group will reinforce their own shelter. The player's deepest contribution isn't direct care — it's creating the conditions for mutual aid between creatures.

Stillgrove is not a Tamagotchi. There is no death, no punishment, no guilt. Entropy is natural. When you're away, the biome doesn't "fail" — it weathers. When you return, the creatures stir. The emotional note is always *"Welcome back"*, never *"Where were you?"*

## The Hardware Makes This Possible

XREAL Project Aura's electrochromic dimming becomes a narrative instrument: as the player invests care, the lenses gradually darken, the physical room recedes, and the Wyldore biome blooms into vivid presence. Immersion depth is directly proportional to emotional investment. Hand tracking makes care feel *tactile* — reaching out to a shivering creature, cupping it in your palms, feeling the spatial audio purr settle into the right position relative to your hands.

Stillgrove is a contemplative, tactile, low-intensity game that fits into the quiet moments of a day, an experience that only works on spatial computing hardware with hand tracking and electrochromic dimming.

## Built on Aether

Stillgrove consumes [Aether](https://github.com/squeak-games/aether), Squeak Games' shared Android XR platform. Aether provides the procedural audio engine, Room database schemas, shared Wyldore data models, and XR capability detection helpers that Stillgrove builds on top of.

| Aether Module | Used By Stillgrove For |
|:---|:---|
| `:core-audio` | Creature vocalizations, ambient biome soundscape, spatial audio positioning, care response feedback |
| `:core-data` | Persistent creature state, bond graph, session history, spatial anchors |
| `:core-model` | Creature state enums, bond level definitions, creature species, care action types |
| `:core-xr` | Runtime XR capability detection, projected activity lifecycle, touchpad input abstractions |

Stillgrove-specific engineering lives in this repo. Shared infrastructure lives in Aether.

## Stillgrove-Specific Systems

The product-specific code that does not belong in Aether lives here:

| System | Purpose |
|:---|:---|
| `BiomeStateEngine` | Entropy calculations, care accumulator, biome health state, surface affinity |
| `CreatureAIManager` | Per-species finite state machines, bond graph, colony detection, movement planning |
| `SceneCoreRenderLayer` | glTF 2.0 creature models, procedural biome geometry (moss, root tendrils, particles), emissive materials |
| `DimmingController` | Electrochromic dimming narrative: maps care accumulator to `setPassthroughOpacityPreference()` with safety cap via person detection |
| `HandGestureRecognizer` | 26-joint hand model with 5 gesture types: offering, sheltering, building, guiding, calling |
| `AnchorRepository` | Spatial anchor persistence via `AnchorPersistenceMode.LOCAL` |
| `GlimmerOverlays` | Session summary cards, settings, accessibility overlays |
| `OnboardingFlow` | First-time experience with one-time narration |

## Module Layout

```
wyldore-stillgrove/
├── app/                    # Projected activity, ARCore integration, SceneCore scene graph
├── engine/                 # Creature AI, bond graph, dimming driver, entropy system
├── render/                 # SceneCore render layer, glTF loading, procedural biome art
├── interaction/            # Hand gesture recognition, touchpad input routing
└── docs/
    ├── PRD.md              # Full Product Requirements Document
    ├── CATALYST.md         # Traceability: pitch claim to file mapping
    └── PRIVACY.md          # Privacy architecture
```

## Design Principles

| Principle | Description |
|:---|:---|
| **Your room is the world.** | The biome is *your* space. Creatures anchor to *your* furniture. The game doesn't create a virtual room — it inhabits your real one. |
| **Hands, not controllers.** | Every interaction is a care gesture performed with your actual hands. No menus, no HUD, no button prompts during active tending. |
| **Entropy is not failure.** | The biome degrades when unattended. This is natural, not punitive. There is no "game over." Creatures never die — they become dormant, curled, waiting. Return is always welcome. |
| **Creatures help each other.** | The deepest satisfaction comes not from direct care, but from watching two creatures you've brought together begin tending each other. Mutual aid is the emergent behavior, not the player's burden alone. |
| **Dimming is depth.** | The electrochromic lens state reflects emotional investment. More care means deeper dimming means more vivid biome. The player earns immersion through tenderness. |
| **Sessions, not marathons.** | 10 to 30 minute sessions. The game respects the user's time and the hardware's battery. Short, meaningful encounters over long, exhausting ones. |

## Technology Stack

| Layer | Technology |
|:---|:---|
| **Language** | Kotlin (app logic, AI, state), C++ (audio engine via Aether's JNI bridge) |
| **Build** | Gradle with Kotlin DSL, version catalogs (`gradle/libs.versions.toml`) |
| **XR Framework** | Jetpack XR SDK (SceneCore, ARCore for Jetpack XR, Compose Glimmer) |
| **3D Assets** | glTF 2.0, loaded via `GltfModelEntity` (<500 triangles per creature) |
| **Audio** | Aether `:core-audio` (Android Oboe via JNI) |
| **Persistence** | Aether `:core-data` (Room Database, SQLite) |
| **Creature AI** | Custom Kotlin FSM + behavior trees (no ML) |
| **Spatial Perception** | ARCore for Jetpack XR (plane detection, anchoring, 26-joint hand tracking) |
| **Dimming Control** | `SpatialEnvironment.setPassthroughOpacityPreference()` |
| **DI** | Hilt (Dagger) |
| **Architecture** | MVVM + Clean Architecture |

## Build

```bash
./gradlew :app:assembleDebug
./gradlew :engine:test
./gradlew :interaction:test
./gradlew test
```

The app module requires an Android XR-capable environment or emulator. The engine and interaction modules run on the JVM with `ProjectedTestRule` simulation harnesses.

## Privacy

Stillgrove inherits Aether's privacy-by-construction model. The platform layer enforces the no-network rule; the product layer cannot violate it.

| Data Type | Handling | Storage | Transmission |
|:---|:---|:---|:---|
| Camera feed | Processed by ARCore for plane detection and hand tracking. Raw frames never accessed by application code. | Never stored | Never transmitted |
| Spatial anchors | Stored as opaque UUIDs with local persistence. No GPS coordinates or room geometry exported. | Local UUID only | Never transmitted |
| Hand tracking data | Consumed per-frame for gesture recognition. Joint positions are never logged or stored. | Never stored | Never transmitted |
| Biome state | Creature positions, bond graph, entropy levels, session history. | Local Room DB | Never transmitted |
| Dimming preferences | User's max dimming cap, safety preferences. | Local Room DB | Never transmitted |

Zero backend server infrastructure. No mandatory accounts. No analytics telemetry. The biome is entirely local to the device.

See `docs/PRIVACY.md` for the full model.

## Documentation

- [`docs/PRD.md`](docs/PRD.md) — Full Product Requirements Document
- [`docs/CATALYST.md`](docs/CATALYST.md) — Catalyst submission traceability (pitch claim to file mapping)
- [`docs/PRIVACY.md`](docs/PRIVACY.md) — Privacy architecture for Stillgrove

## Catalyst Submission

This repo is open-sourced in support of the Android XR Developer Catalyst Program submission. The README and code intentionally reflect the current development stage (stage 3 of 10). It is not yet the shipping creature experience, it is the evidence that we already know how to build that experience.

See `docs/CATALYST.md` for the mapping from each pitch deck claim to the file in this repo that substantiates it.

## Sister Products

Stillgrove is one of two Android XR products Squeak Games is developing:

- **Wyldore: Hushwild** — an ambient, audio-first wellness companion for Android XR audio and display glasses. On-the-go, passive, always-on. See [wyldore-hushwild](https://github.com/squeak-games/wyldore-hushwild).
- **Wyldore: Stillgrove** (this repo) — a spatial creature-tending experience for XREAL Project Aura. At-home, active, hand-tracked.

Both products share the [Aether](https://github.com/squeak-games/aether) platform. Together they create a continuous cycle of care: the creature that walks with you through morning traffic (Hushwild) inhabits the same mythic ecosystem you tend with your hands at home (Stillgrove).

## License

Apache 2.0. See [LICENSE](LICENSE).

## Status

Under active development. Committed 2026, ahead of Catalyst Program submission. Further commits track the milestone plan in the PRD (`docs/PRD.md`, section 13).

---

*Squeak Games Inc.*