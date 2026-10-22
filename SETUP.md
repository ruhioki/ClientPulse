# ClientPulse development setup

## Requirements

- IntelliJ IDEA with Gradle project support
- JDK 25 (set as both the Project SDK and Gradle JVM)
- Internet access for the first dependency resolution

Gradle is supplied by the wrapper. No system Gradle installation is required.

## Open and run

1. Open the repository root in IntelliJ IDEA and import it as a Gradle project.
2. Set the Gradle JVM to JDK 25 and allow the initial sync to finish.
3. Run `.\gradlew.bat build` to compile, test, and package the mod.
4. Run `.\gradlew.bat runClient` to start Minecraft with ClientPulse in a development instance.
5. Press F8 in the game to open the ClientPulse feature screen.

The first Gradle run downloads the configured Gradle distribution and game dependencies. The mod uses Minecraft's normal authentication and launch process; a development launch requires the game assets and network access to be available.

## Logs and configuration

ClientPulse logs its startup message through the mod logger. The game configuration is written to `config/clientpulse.json` under the development run directory. Enabled modules are persisted when toggled; all HUD modules begin disabled.

Run `.\gradlew.bat test` for the standalone feature-filtering tests. Run `.\gradlew.bat build` before sharing a mod JAR.

## Forge 1.8.9 test build

Install JDK 8 and run the version-specific wrapper from PowerShell:

```powershell
Set-Location .\versions\1.8.9-forge
.\gradlew.bat build
```

The generated mod is in `versions\1.8.9-forge\build\libs`. Use only with Minecraft 1.8.9 and Forge 11.15.1.2318. OptiFine 1.8.9 has not been tested for runtime compatibility.
