# ClientPulse development backlog

## Research snapshot

Reviewed Lunar Client's [official changelog](https://www.lunarclient.com/changelog) on 2026-10-22, including launcher and game entries published through October 18, 2026. These entries are inspiration and quality lessons, not requirements to reproduce another client's implementation.

ClientPulse's active target for this cycle is Minecraft 1.8.9 on Forge 11.15.1.2318 and Java 8. Its rendering integration uses the 1.8.9 Forge overlay events, `ScaledResolution`, Minecraft's GUI classes, and legacy OpenGL state APIs. Newer-version-only rendering, shader, Vulkan, inventory, or server integrations must not be assumed to work on this target.

## Changelog findings and ClientPulse direction

| Area | Changelog lesson | Why it helps | Independent ClientPulse direction |
|---|---|---|---|
| GUI and interface design | Profile search, sortable content lists, and fixes for clipped selection rings, overlapping modals, and clicks reaching dimmed content | Search and predictable hit regions remain usable as interfaces grow | Keep module search and explicit selection, bound hit tests to visible panels, and add profile search only when profiles exist |
| Animations and transitions | Smooth FOV transitions and legacy server-list dragging were fixed when motion appeared stepped or choppy | Frame-rate-dependent movement feels inconsistent and undermines precise input | Use elapsed-time animations for GUI transitions and scroll; keep drag calculations tied to scaled mouse coordinates |
| HUD and overlays | Armor Status gained automatic damage placement; HUDs gained Minecraft GUI-scale and vanilla-position options; opacity and persisted position bugs were fixed | HUDs must remain legible, movable, and stable across GUI scales and restarts | Store screen-relative positions, clamp text to the viewport, and persist them with each HUD module |
| Cosmetics and personalization | Profile icon editing adds symbols, gradients, and randomization; cloak switching exposed state-reset bugs | Lightweight personalization improves ownership, but state must survive selection changes | Add original ClientPulse theme presets and previews after core settings are reliable; avoid copied assets |
| Visual effects and rendering | Custom skies, foliage hiding, crosshair indicators, shader/reload crash fixes, and OptiFine-specific fixes span multiple render paths | Effects can conflict with resource packs, shaders, or legacy render hooks | Prefer isolated, opt-in 1.8.9 effects; preserve GL state and gate OptiFine claims on real compatibility tests |
| Performance optimization | Changelog entries attribute FPS gains to graphics processing and texture/render caching | Avoid repeating expensive work every frame or rebuilding unchanged state | Keep HUD work small, reuse immutable registry data, and profile before adding render-heavy effects |
| Configuration and profiles | Profile switching, bulk actions, export, settings persistence, and invalid/blank input fixes recur | Users need reliable, understandable configuration changes | Keep validated settings serialization; introduce named profiles with explicit import/export and migration tests |
| Inventory and quality of life | Inventory search adds `|`, `&`, and `!`; container animations improve feedback | Search operators help advanced users; animations should not obscure item movement | Consider a small, independently written inventory filter only after 1.8.9 container compatibility tests; keep it optional |
| Compatibility and reliability | The changelog calls out Forge 1.8.9 menu overlap, OptiFine fog, invalid server icons, and older-version crashes | Version-specific APIs and malformed inputs can break startup or UI behavior | Keep the 1.8.9 adapter isolated, validate values before rendering, and test clean launch plus OptiFine separately |
| Testing and bug prevention | Fixes cover resize/fullscreen input loss, wrong mouse alignment, stale profile state, and blank/invalid values | Regression tests should target state transitions and edge conditions, not only happy paths | Add tests for persisted settings, viewport edges, key capture, resize-scaled positions, and animation retargeting |

## Prioritized work

1. **Completed — viewport-aware HUD positioning.** Existing HUD modules have persistent horizontal/vertical percentage settings and use the current scaled viewport with clamping and boundary/persistence tests.
2. **Implemented; interaction verification pending — original cosmetics.** Added a local 1.8.9 procedural cosmetic catalog/renderer and Cosmetics Studio, including reset and cape/wing placement controls. Builds/tests pass, and the rebuilt client visibly renders cosmetics at the player’s back/shoulder area; manually verify reset persistence, mouse interactions, and GUI-scale behavior.
3. **In progress — useful 1.8.9 HUD set.** The configurable CPS HUD now tracks left/right clicks in a rolling one-second window and persists its settings. Its isolated Forge 1.8.9 `clean test build` passed with 15 tests; the commit is integrated here, but this branch still needs a serialized combined build. Keystrokes, ping, and armor status remain planned.
4. **Planned — crosshair customization.** Add a small set of original color, size, gap, and opacity controls using 1.8.9-compatible rendering; test with GUI scale changes and OptiFine before claiming compatibility.
5. **Planned — named configuration profiles.** Add create/switch/rename/export/import for validated settings, with safe filenames, clear errors, and migration/round-trip tests.
6. **Planned — inventory and interface quality of life.** Prototype a local inventory search/filter with a deliberately small grammar; do not alter container behavior until slot/input regression tests exist.
7. **Blocked — OptiFine compatibility claim.** The development Forge client has launched, but OptiFine has not been installed or tested; keep this explicitly unverified until a matching 1.8.9 setup is exercised.

## Status

### Completed

- Java 8-compatible animation, module/category, and setting foundations with serialization and validation.
- Minecraft 1.8.9 searchable Click GUI with category filtering, module toggles, per-module settings, key capture, theme/color controls, hover/open/close transitions, and animated scrolling.
- Existing FPS, coordinates, session, and memory HUD modules with persisted enable/settings state.
- Automated tests for animation retargeting, colors, validation, and module persistence.
- ClientPulse-owned cosmetics catalog/profile and 1.8.9 Cosmetics Studio integration; placement visually checked in a rebuilt client, with GUI interaction verification still pending.

### In progress

- Manual Cosmetics Studio mouse-interaction, reset-persistence, and GUI-scale verification.
- Combined Forge verification of the integrated CPS HUD and animated purple-to-white Click GUI accent. The CPS slice passed its isolated Java 8 build; the combined build is pending because the host currently has active Java 21 workloads, and this worktree has no Java 8 JDK installed.

### Blocked

- OptiFine-specific runtime verification pending a matching OptiFine/Forge 1.8.9 test environment.
