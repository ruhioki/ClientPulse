# ClientPulse cosmetics implementation

## Architecture

- `clientpulse-core/.../cosmetic/` holds Minecraft-independent cosmetic categories, original catalog definitions, and a validated persistent profile.
- `Cosmetics189` owns local profile initialization, properties serialization, atomic replacement, and Forge client setup.
- `CosmeticRenderer189` renders original geometry from the 1.8.9 `RenderPlayerEvent.Post` hook. It applies only to the local player and restores OpenGL state after drawing.
- `CosmeticsScreen189` is an animated-theme-compatible, non-pausing screen with search, categories, favorites, selection/reset controls, a rotating player preview, enable/animation switches, tint presets, scale, animation-duration controls, and cape/wing height adjustment.
- Open Cosmetics Studio from the visible **COSMETICS** button in the Right Shift Click GUI header, or press **C** while that GUI is open. Press Escape in Studio to return to the Click GUI. Search fields and active setting key capture retain keyboard input.
- Settings are stored independently in `config/clientpulse-cosmetics.properties`; no Lunar Client files are read at runtime.

## Included original cosmetics

| Category | Cosmetic | Rendering |
|---|---|---|
| Cape | Pulse Cape | Segmented, subtly swaying procedural cloth |
| Wings | Aurora Wings | Two original, flapping triangular panels |
| Hat | Orbit Halo | Animated elliptical ring above the player |
| Trail | Starlight Trail | Small orbiting points, generated without texture assets |
| Badge | Pulse Badge | Pulsing diamond on the player model |

Cosmetics are independently selectable by category. They are disabled by default, render locally only, and do not transmit state to servers or other clients. The preview uses the active Minecraft player model. Positioning and rendering are adapted to the 1.8.9 Forge render hook and legacy OpenGL; this is not an implementation of Lunar formats or exact visual compatibility.

The cape and wing models attach around the player's back/shoulder region in the 1.8.9 renderer's feet-origin coordinate space. Their vertical offsets can each be adjusted in Cosmetics Studio by 0.05 model units per click. The **Reset** action clears all selections and favorites, disables cosmetics, and restores default tint, scale, animation, and placement settings.

## Persistence and robustness

The profile validates RGB values, scale (0.5–1.5), animation duration (200–3,000 ms), catalog IDs, and category membership. Invalid stored properties are ignored and reported through the mod logger. Writes use a temporary file and atomic replacement where supported. The renderer uses generated geometry rather than loading image/model files, so there is no new texture cache or per-cosmetic resource lifecycle; OpenGL enable/color/depth/line/point state is scoped and restored.

## Verification status

- Automated tests cover profile round-tripping, favorites and category selections, invalid-property recovery, category mismatch rejection, and non-finite scale rejection.
- `versions/1.8.9-forge`: the final `test build` passed on JDK 8. An earlier `clean test build` caught an incorrect `GuiInventory` import, which was corrected. The shared-core modern Fabric `test build` passed on JDK 25, and the 1.7.10 Forge `build` passed on JDK 8.
- After rebuilding, a fresh development client loaded Minecraft 1.8.9 and entered an integrated world. Third-person inspection confirmed the cape and wings render in the player's back/shoulder area rather than below the feet. Cosmetics Studio displayed the live model preview and the cape/wing placement controls.
- The first live cape preview exposed a low model-space anchor. Cape, wing, halo, trail, and badge anchors were corrected and the 1.8.9 tests/build passed again.
- Automated profile tests cover reset-to-default behavior, including selections, enabled state, color, scale, animation duration, and placement offsets. The Reset control is present in Cosmetics Studio, but its mouse interaction and persisted reset have not been confirmed in a manual gameplay test.
- GUI-scale changes, mouse-driven scrolling/selection/dragging, and the corrected animation appearance were not independently verified. OptiFine compatibility, remote-player visibility, server synchronization, custom user assets, and the Lunar formats remain unimplemented or unverified.
