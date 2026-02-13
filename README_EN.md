# Cobblemon Modern Exp Share

A **Cobblemon (Fabric)** Exp. Share behavior “modernization” mod: when a player has `cobblemon:exp_share` in their inventory/hotbar, the victory payout phase will grant extra Experience (EXP) and Effort Values (EV) to party members of the same player that **did not participate** and **are not fainted**.

## Compatibility

- Minecraft: **1.21.1**
- Fabric Loader: **>= 0.17.2**
- Fabric API: **>= 0.116.6+1.21.1**
- Cobblemon (Fabric): **1.7.1+1.21.1**
- Java: **21**

Mod metadata:
- Name: Modern Exp Share
- Mod ID: `modern_exp_share`

## Behavior

This mod applies when:
- The player has `cobblemon:exp_share` in their inventory/hotbar.
- During battle victory payout, for each Pokémon in that player’s party that is:
  - **Not participated**, and
  - **Not fainted**

Extra rewards are granted:
- **EXP**: `participation EXP × sharedExpMultiplier` (default: 0.5)
- **EV**: `participation EV × sharedEvMultiplier` (default: 1.0)

> Note: Multipliers are server-authoritative (also applies in single-player integrated server).

## Configuration

Config file location:
- `config/cobblemon_modern_exp_share.json`

Default config:

```json
{
  "sharedExpMultiplier": 0.5,
  "sharedEvMultiplier": 1.0
}
```

Fields:
- `sharedExpMultiplier`: extra shared EXP multiplier (>= 0)
- `sharedEvMultiplier`: extra shared EV multiplier (>= 0)

The config is loaded on server startup; if the file does not exist, it is created with defaults.

## Building

This project includes a Gradle Wrapper (pinned to Gradle 9.2.0). Use the wrapper to build.

### 1) Provide the Cobblemon jar locally

This project references Cobblemon as a local jar dependency. Make sure the file exists:
- `libs/cobblemon-fabric-1.7.1+1.21.1.jar`

This jar is intentionally **gitignored** (do not commit/distribute it).

### 2) Ensure Java 21

- The build uses a Java toolchain set to 21 (see `build.gradle`).
- Make sure Java 21 is installed and available (e.g. via `JAVA_HOME`).

### 3) Build

From the project root:

```powershell
.\gradlew.bat build
```

Output:
- `build/libs/cobblemon-modern-exp-share-<version>.jar`

## Install / Usage

- Put the built jar into the client/server `mods/` folder.
- Install compatible Cobblemon and Fabric API.
- Keep `cobblemon:exp_share` in your inventory/hotbar to enable the sharing behavior.

## License

MIT (see the project’s license declaration/files for the authoritative license).
