# NEXA repository audit and baseline

Audit date: 2026-11-08. Branch: `arena/eca3771e-nexalauncher`, based on MojoLauncher. This is an incremental redesign, not a rewrite.

## Architecture and compatibility

- `app_mojo` is an Android Java app (minimum API 21, compile/target API 36); `forge_installer` and four native/binding submodules support it. Gradle wrapper 8.14.5, Android Gradle Plugin 8.11.1, CI uses JDK 17, NDK 29.0.14206865. Gradle repositories and build asset downloads also require Google, Maven Central, JitPack and GitHub.
- `MojoApplication` initializes storage, preferences, executor and runtime assets. `TestStorageActivity` is the launcher entry; `LauncherActivity` hosts `MainMenuFragment`, account spinner, preferences and progress. `MainMenuFragment` triggers the existing game launch through `ExtraCore`; launch, runtime/download and native execution must remain intact.
- `LauncherPreferences` uses existing shared preferences. `Instances`, `Instance`, `InstanceEditorFragment` and version spinner manage existing game data; `authenticator/accounts` and login fragments manage accounts. Game runs in a separate `:game` process, with `GameActivity`, `ControlLayout`, drawers, joystick and input handlers. Any future overlay visibility changes must preserve hardware input and provide a recovery path.
- Existing Android XML layouts/styles and custom `com.kdt.mcgui` views are reusable. Landscape has a separate `fragment_launcher` layout. Existing search/import functionality is upstream; no new marketplace or installer work is in scope.
- Application ID is `git.artdeell.mjlaunch` (debug suffix `.debug`); keep it unchanged to avoid data, upgrade, provider authority and authentication migration issues. Release signing requires existing authorized keys and passwords. LGPLv3 and upstream attribution in LICENSE/README must remain.
- Relevant debt/risks: custom views and landscape variants require regression checks; old storage permissions and multi-process preferences need Android-version review; native rendering/input and downloaded assets make actual device tests essential; current crash logs may contain sensitive data and must not be exported as a new diagnostic report without redaction. Existing signing material must not be repurposed or exposed.

## Baseline and tools

- Initialized `NoBlocklist`, `glfw`, `mojoexec`, `sdl` at their pinned commits using `git submodule update --init --recursive`.
- Attempted `./gradlew :app_mojo:assembleDebug` and `./gradlew :app_mojo:test`: both stopped immediately because Java/JAVA_HOME is absent. Android SDK, adb and emulator are also absent; no APK, lint, unit or instrumented test result is available. Java 17, SDK platform 36, build tools, NDK and CMake must be provisioned in a suitable Android build environment. This sandbox's network allowlist excludes Google, Gradle distribution and JitPack hosts, so a complete standard Android setup/build cannot be verified here.
- No conventional `src/test` or `src/androidTest` suite was found in the app checkout. `CriticalNativeTest` and `TestStorageActivity` are application code, not a substitute for automated tests.
- Previously requested skills installed into this checkout: `find-skills` (vercel-labs/skills) and `liquid-glass` (haider-nawaz/liquid-glass-skill), via `npx skills`; the latter targets Apple's SwiftUI and is not an Android dependency. ImageMagick was already available for resizing the supplied logo. No Android dependency or MCP server was added.

## Recommended sequence

1. Establish an Android-capable baseline and device test matrix; verify existing functionality before risky changes.
2. Incremental identity and lightweight translucent surfaces in both orientations, keeping all view IDs and actions.
3. Centralize theme configuration and add lifecycle-safe wallpaper/SAF access; test memory use and contrast.
4. Improve instances, accounts and settings while preserving storage and authentication.
5. Implement recording controls only after input/overlay testing; then diagnostics with secret redaction.
6. Run builds, lint, tests and real-device performance/input checks; document limitations and release only with authorization.
