# ClientPulse development setup

## Modern 26.3 target requirements

- IntelliJ IDEA with Gradle project support
- JDK 25 (set as both the Project SDK and Gradle JVM)
- Internet access for the first dependency resolution

Gradle is supplied by the wrapper. No system Gradle installation is required.

## Open and run

1. Open the repository root in IntelliJ IDEA and import it as a Gradle project.
2. Set the Gradle JVM to JDK 25 and allow the initial sync to finish.
3. Run `.\gradlew.bat build` to compile, test, and package the mod.
4. Run `.\gradlew.bat runClient` to start Minecraft with ClientPulse in a development instance.
5. Press Right Shift in the game to open the ClientPulse Click GUI.

The first Gradle run downloads the configured Gradle distribution and game dependencies. The mod uses Minecraft's normal authentication and launch process; a development launch requires the game assets and network access to be available.

## Logs and configuration

ClientPulse logs its startup message through the mod logger. The game configuration is written to `config/clientpulse.json` under the development run directory. Enabled modules and their settings, including each HUD module's horizontal and vertical position percentages, are persisted; all HUD modules begin disabled.

Run `.\gradlew.bat test` for the standalone feature-filtering tests. Run `.\gradlew.bat build` before sharing a mod JAR.

## Forge 1.8.9 client

Install JDK 8 and run the version-specific wrapper from PowerShell:

```powershell
Set-Location .\versions\1.8.9-forge
.\gradlew.bat clean test build
.\gradlew.bat runClient
```

Press Right Shift to open the Click GUI. Choose a category or search for a module, click a module card to edit its settings, and use the card's switch to toggle it. The Open Key setting captures a new keyboard binding. Configuration is saved to `run\config\clientpulse-1.8.9.properties`.

The generated mod is in `versions\1.8.9-forge\build\libs`. Use only with Minecraft 1.8.9 and Forge 11.15.1.2318. OptiFine 1.8.9 has not been tested for runtime compatibility.
