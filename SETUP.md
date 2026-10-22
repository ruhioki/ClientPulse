# ClientPulse setup notes

This is a starter scaffold targeting Minecraft 26.3 using the version values published by Fabric at the time this scaffold was prepared. Verify them at https://fabricmc.net/develop/ before updating or building.

## Requirements

- IntelliJ IDEA
- JDK 25
- Gradle compatible with the selected Fabric Loom release
- Internet access for the first dependency resolution

## Open the project

1. Extract this folder.
2. Open the `ClientPulse` folder in IntelliJ IDEA.
3. Set the Project SDK and Gradle JVM to JDK 25.
4. Allow Gradle to sync.
5. Run the Gradle `runClient` task if it is available after Loom sync.

This archive does not include the Gradle wrapper JAR/scripts. If IntelliJ does not import Gradle automatically, create or install a compatible Gradle wrapper before using `gradlew`.

## First milestone

When the development client launches, check the log for:

`ClientPulse initialized. Learn. Build. Optimize.`

The banner is a design concept. It does not imply that the dashboard or listed feature modules are implemented.
