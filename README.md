# ClientPulse

ClientPulse is a multi-version client project for Minecraft Java Edition. It keeps shared feature metadata separate from version-specific game integrations and provides independently built client variants. It uses the normal Minecraft launcher and account authentication; it is not a standalone launcher.

## Current feature status

| Feature | Status |
|---|---|
| Shared Java 8-compatible feature model and registry | Implemented |
| Minecraft 26.3 Fabric client, searchable settings screen, F8 keybind, HUD modules | Build and unit tests pass; in-game launch not yet verified |
| Minecraft 1.8.9 Forge client, HUD modules and settings | Build verified on JDK 8; OptiFine runtime not yet verified |
| Minecraft 1.7.10 Forge client and matching HUD/settings controls | Build verified on JDK 8; in-game launch not yet verified |
| Windows development-client launch and in-game feature verification | Not yet verified |

All HUD modules are off by default. The modern settings screen opens with F8 or from Options > Controls > Key Binds; the 1.7.10 build uses its Forge Controls keybind.

See [docs/compatibility.md](docs/compatibility.md) for the support matrix and exact verification status.

## Modern target: Minecraft 26.3

- Minecraft Java Edition 26.3
- Java 25
- Fabric Loader 0.19.5
- Fabric API 0.164.0+26.3
- Fabric Loom 1.19.3

These versions were checked against Fabric documentation, metadata, and Maven artifacts when selected. Re-check the [Fabric version selector](https://fabricmc.net/develop/) before upgrading.

## Build and run

Install a JDK 25 distribution and ensure `java -version` reports version 25. From PowerShell in the project directory:

```powershell
.\gradlew.bat build
.\gradlew.bat runClient
```

The first build downloads Gradle, Minecraft, and dependencies. `runClient` launches a development instance; sign in through the official Minecraft launcher for normal gameplay. In IntelliJ IDEA, open this directory as a Gradle project, set the Gradle JVM to JDK 25, sync, then run the `runClient` task.

## Legacy target: Minecraft 1.7.10

The legacy Forge implementation has its own Gradle 2.14.1 wrapper and requires JDK 8. It reuses `clientpulse-core` but does not share modern Minecraft classes or rendering APIs. Its `build` task has passed locally; the legacy launch has not yet been verified.

```powershell
Set-Location .\versions\1.7.10-forge
.\gradlew.bat build
.\gradlew.bat runClient
```

The legacy wrapper and Forge dependencies build successfully; runtime launch and in-game behavior remain unverified.

## Minecraft 1.8.9 Forge test build

The 1.8.9 build is a separate Forge mod and is not compatible with the 1.7.10 or 26.3 artifacts. It targets Forge 11.15.1.2318 and Java 8; its local build succeeds. To build it locally:

```powershell
Set-Location .\versions\1.8.9-forge
.\gradlew.bat build
```

To try it with OptiFine, use a Minecraft 1.8.9 Forge profile with Forge 11.15.1.2318 and a matching OptiFine 1.8.9 installation. Put the `clientpulse-1.8.9-0.1.0.jar` artifact in that profile's `mods` folder. OptiFine compatibility has not been verified in-game; report crashes with the full game log and the exact Forge/OptiFine versions.

## Configuration

The modern Fabric target stores settings in `config/clientpulse.json`. Forge targets store separate properties files under `config/`. All HUD modules are off by default.

## Project layout

```text
clientpulse-core/              # Version-independent feature model; Java 8 bytecode
src/
├── client/java/com/ruhioki/clientpulse/
│   ├── config/             # Persisted feature settings
│   ├── hud/                # Optional client HUD diagnostics
│   ├── screen/             # Searchable client settings screen
│   └── ClientPulseClient.java
├── client/resources/       # Client translations
└── main/resources/         # Fabric mod metadata
versions/
├── 1.7.10-forge/              # ForgeGradle 1.2 build and legacy adapter
└── 1.8.9-forge/               # ForgeGradle 2.1 build and 1.8.9 adapter
```

## Development

Run `.\gradlew.bat test` for unit tests and `.\gradlew.bat build` for the modern target. Use the version-specific wrappers for Forge targets. The GitHub Actions workflow builds each configured target independently. The modern and Forge JARs are written to their respective `build/libs/` directories. Graphical launch and in-game behavior still need verification on a machine with Minecraft assets and a usable display.

ClientPulse is licensed under the MIT License. Minecraft and Fabric are third-party projects; their respective terms apply.
