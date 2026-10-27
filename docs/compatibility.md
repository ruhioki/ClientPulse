# ClientPulse compatibility

ClientPulse builds each Minecraft integration with its own loader, Java target, and Gradle wrapper. The shared library contains game-independent animation, module, and setting logic and targets Java 8 bytecode so version-specific adapters can reuse it.

| Minecraft | Loader and version | Java | Build | Runtime / features |
|---|---|---|---|---|
| 1.7.10 | Forge 10.13.4.1614 | 8 | Build verified with ForgeGradle 1.2.2 | Client implementation builds; launch and in-game feature tests pending |
| 1.8.9 | Forge 11.15.1.2318 | 8 | Clean test/build verified with ForgeGradle 2.1.3 | Development world and Click GUI open/render smoke-tested; full interactive GUI and OptiFine compatibility tests pending |
| 1.8.x other than 1.8.9, and 1.9–1.12.2 | Not selected | Version-specific | Planned | Unsupported |
| 1.13.x–1.16.5 | Not selected | Version-specific | Planned | Unsupported |
| 1.17.x–1.20.x | Not selected | Version-specific | Planned | Unsupported |
| 1.21.x | Not selected | Version-specific | Planned | Unsupported |
| 26.3 | Fabric Loader 0.19.5 / Fabric API 0.164.0+26.3 | 25 | Build and unit tests pass | UI, keybind, HUD, and persistence are implemented; launch test pending |

Do not infer support for a range from one version. A row moves to “build verified” only after the exact version-specific Gradle wrapper resolves dependencies and completes the build. Runtime support requires a separate launch and feature test.

## Build boundaries

- The repository root is the modern 26.3 Fabric project. It uses Gradle 9.8.2 and Java 25.
- `clientpulse-core` contains version-independent Java logic and emits Java 8 bytecode.
- `versions/1.7.10-forge` is an independent ForgeGradle 1.2 project. It uses Gradle 2.14.1 and must run with JDK 8; it compiles the shared core sources directly.
- `versions/1.8.9-forge` is an independent ForgeGradle 2.1 project. It uses Gradle 2.14.1 and must run with JDK 8; it compiles the shared core sources directly.
- Additional Minecraft versions must receive their own compatible module and build verification. Do not add old APIs to the modern source set or claim one JAR works across versions.

## Updating versions

Check each loader's official version and build documentation before changing dependencies. For Fabric, use the [Fabric version selector](https://fabricmc.net/develop/). For Forge 1.7.10, the final listed Forge build is [10.13.4.1614](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.7.10.html).
