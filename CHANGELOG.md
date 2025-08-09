<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# dart-barrel-file Changelog

## [1.0.1] - 2025-08-09
### Added
- Keyboard shortcut for Add To Barrel File: Ctrl+Alt+Shift+B.

### Fixed
- Restore "Add To Barrel File" context menu visibility in modern Android Studio/IntelliJ by registering the action in `EditorPopupMenu` and `ProjectViewPopupMenu`.
- Ensure action updates during indexing by converting to `DumbAwareAction` and moving update to `BGT` thread.
- Improve file content replacement safety when updating barrel files.

### Changed
- Upgrade toolchain: Gradle 8.7, Kotlin 1.9.24, IntelliJ Gradle Plugin 1.17.3, Platform 2023.1 (since-build `231`).
- Set Java compatibility to 17.
- Fix Gradle publish channels configuration for newer Gradle API.

## [Unreleased]
