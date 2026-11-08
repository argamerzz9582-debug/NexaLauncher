# NEXA redesign progress

## Command Center first pass

The home dashboard now uses the supplied transparent logo, a midnight glass-inspired card, quick links to the existing instance selector, account selector and settings, and a selected-instance launch area. RAM allocation comes from the existing launcher preference. Existing launcher tools and the game launch handler remain available. Both portrait and landscape use the same scrollable dashboard content with orientation-specific launch controls. The logo also replaces the drawable used by the app icon.

The visual treatment uses static translucent surfaces and gradients, not real background blur. No new marketplace or installer was added. Existing JAR installer functionality is preserved.

## Validation and limitations

All resource XML was parsed and dashboard IDs were checked in both orientations. `./gradlew :app_mojo:assembleFullDebug` cannot start here because Java is not installed; no Android SDK or emulator is available either. Android compilation, layout/accessibility checks and game, authentication and input regression testing remain required on an Android-capable environment. The updated logo icon should be checked under different launcher icon masks.

This is not the complete redesign: dedicated instances/accounts pages and navigation, theme studio, wallpaper selection, recording mode, diagnostics, comprehensive settings redesign, localization for new strings and device/performance testing remain future work.

## Appearance first pass

Settings now offers five launcher background presets and a custom wallpaper selected using the Android document picker. Read access is persisted; old grants are released when replaced or reset. Images are sampled off the UI thread with a lower size limit on low-memory devices. A fixed dark overlay improves text contrast and the preset remains the fallback if an image cannot be read. Changes apply when returning to the dashboard. This is not yet the full Theme Studio: color pickers, adjustable blur, dimming and glass controls, image positioning, and live preview remain outstanding. Verify URI persistence, memory use and contrast on real devices.
