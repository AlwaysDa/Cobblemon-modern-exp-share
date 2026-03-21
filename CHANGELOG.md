# Changelog

All notable changes to this project will be documented in this file.

## [0.1.1] - 2026-03-21

### Added
- Added a standalone MIT license file for clearer GitHub license detection.
- Added this changelog for release tracking.

### Changed
- Updated Cobblemon compatibility to 1.7.3+1.21.1.
- Updated local dependency matching so launcher-renamed Cobblemon jars are accepted.
- Raised the mod version to 0.1.1.
- Ignored local Copilot and agent helper directories from git.

### Fixed
- Fixed Fabric mod metadata that previously hard-pinned Cobblemon 1.7.1+1.21.1 and caused load failures with 1.7.3.

## [0.1.0] - 2026-03-21

### Added
- Initial release of Cobblemon Modern Exp Share.
- Inventory-based Exp. Share behavior for non-participating, non-fainted party Pokemon.
- Server-side configuration for shared EXP and shared EV multipliers.