# ClientPulse

ClientPulse is a multi-version client project for Minecraft Java Edition. It keeps shared feature metadata separate from version-specific game integrations and provides independently built client variants. It uses the normal Minecraft launcher and account authentication; it is not a standalone launcher.

## Current feature status

| Feature | Status |
|---|---|
| Shared Java 8-compatible feature model and registry | Implemented |
| Minecraft 26.3 Fabric client, searchable settings screen, F8 keybind, HUD modules | Build and unit tests pass; in-game launch not yet verified |
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

## Configuration

The file `config/clientpulse.json` is created in the Minecraft game directory. It stores the enabled state of registered features. Unknown feature IDs are ignored so configurations remain forward-compatible. If the file is invalid, ClientPulse logs a warning and starts with default feature states.

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
└── 1.7.10-forge/              # Standalone ForgeGradle 1.2 build and legacy adapter
```

## Development

Run `.\gradlew.bat test` for unit tests and `.\gradlew.bat build` for the modern target. Use the version-specific wrapper for legacy builds. The GitHub Actions workflow builds both configured targets independently. The modern JAR is written to `build/libs/`; the legacy JAR is written to `versions/1.7.10-forge/build/libs/`. No screenshots or performance gains are claimed; graphical launch and in-game behavior still need verification on a machine with Minecraft assets and a usable display.

ClientPulse is licensed under the MIT License. Minecraft and Fabric are third-party projects; their respective terms apply.
