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

The FPS HUD is enabled by default and appears in the top-left corner during gameplay. Press **F8** to hide or show it; its binding can be changed under **Options → Controls → Key Binds → ClientPulse**.

The HUD reports rendered frames per second, sampled over one-second intervals. Allow a moment after launch or enabling it for the first reading to appear. The banner is a design concept and does not imply that its dashboard or other planned feature modules are implemented.

## Manual feature check

1. Launch the development client and enter a world.
2. Confirm that `ClientPulse | <number> FPS` appears in the top-left corner and updates about once per second.
3. Press **F8** and confirm that the counter disappears; press **F8** again and confirm that it returns.
4. Open **Options → Controls → Key Binds → ClientPulse**, change the binding, and confirm that the new key toggles the counter.
