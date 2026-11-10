# Special Mobs

Adds variety to vanilla monsters to make enemies more interesting, without breaking the 'Minecraft' feel: 97 special variants across 14 families of vanilla mobs, from fire zombies and ninja skeletons to mother spiders and king ghasts.

This is a port of [FatherToast's Special Mobs](https://github.com/FatherToast/SpecialMobs) 1.0.2 for Minecraft 1.12.2 to the [Cleanroom](https://github.com/CleanroomMC/Cleanroom) loader. It requires Cleanroom 0.7.0 or above and does not run on Forge.

## Compatibility with the original

The port is a drop-in replacement for the original jar. It keeps the mod id `specialmobs`, every entity id, the config file `config/Special_Mobs.cfg` with all of its options, and the loot table and texture paths. Remove the original jar before adding this one, since both use the same mod id.

Entity ids and saved mob data are unchanged, so worlds played with the original should load as they are. This has not been tested against a world saved by the original jar.

## What changed from the original

**Spawn weights by location now work.** The original could not read two kinds of config lines, so they never had any effect:

- Biome group lines such as `biome/minecraft:desert*=200.0`. Every themed default uses these, for example fire mobs being more common in deserts.
- Lines for any biome that has a mutated form, such as `biome/minecraft:plains=...`.

Both now apply, including in mutated biomes, which count as the biome they are a mutation of. With an unchanged config file you will see a different mix of variants than before: this is the mix the config always described.

**Replacing vanilla mobs**

- A special variant that replaces a vanilla mob keeps its own health, damage and speed changes. They were overwritten by the vanilla mob's values before.
- Naturally spawned and spawner mobs are set up once instead of twice, so equipment and baby chances are no longer rolled a second time and no stray chicken mounts appear.
- Jockeys are replaced cleanly, without the original rider lingering as an invisible passenger.
- Vanilla mobs that are loaded from an existing world become the vanilla-like version of themselves and are otherwise left exactly as they were. They are not turned into special variants or given new equipment.
- A mob that died, or whose chunk unloaded, before it could be replaced is skipped.

**Fixes**

- Dedicated servers can send mob textures that were changed through nbt, and players who come into range later are told about them as well.
- Glowing eyes and overlay textures set through nbt are applied to the right layer.
- The extra armor-piercing damage of brutes and a few other mobs kills properly, with drops and a death message, instead of leaving its target at zero health.
- Players grabbed in mid-air by caramel slimes or sticky magma cubes are no longer kicked for flying.
- Silverfish that spawn aggressive look for players all around them.
- Blinding endermen no longer apply their effects to fake players from machines, which could crash.
- The block list for what endermen can carry is read after every mod has registered its blocks, so modded blocks and `mod_id:*` work.

**New**

- Options can be changed in game from the mod list. Changes apply to the next spawns without a restart, except for the few options marked as needing one.
- `spawning` can be switched on and off while the game is running.

## Configuration

Everything is in `config/Special_Mobs.cfg`, with a comment on each option. Each family has its own category with:

- `_replace_vanilla`: whether vanilla mobs that do not become special are swapped for a vanilla-like version that the mod's other options apply to.
- `_special_chance` and `_special_chance_exceptions`: how often mobs of the family spawn as a special variant, overall and in particular places.
- `<variant>_weight` and `<variant>_weight_exceptions`: how likely each variant is to be the one picked.

The exception lists take one entry per line: `biome/mod_id:biome_id=value`, `biome/mod_id:prefix*=value` or `dimension/dimension_name=value`. The most specific entry that applies wins: a single biome, then a biome group, then a dimension.

## Building

```
./gradlew build
```

The jar is written to `build/libs`. The version is computed from git tags; `./gradlew -q printVersion` prints it.

## Development

`./gradlew runClient` starts the game with the mod. `./gradlew runServer` starts a dedicated server, and asks you to accept the Minecraft EULA the first time.

The code is formatted with `./gradlew formatJavaApply`, and `./gradlew clearSkiesApply` expands star imports.

Two development tools are built into the mod. Both do nothing unless the game is started with their system property:

- **Self-test.** `./gradlew runClient -Pextra_jvm_args=-Dspecialmobs.selfTest=true` opens a fresh flat world, spawns every species, checks saving, loot tables and the replacement of vanilla mobs, writes the results to the log and closes the game. The last line it logs starts with `SELFTEST RESULT`.
- **Loot table generator.** The loot tables in `src/main/resources/assets/specialmobs/loot_tables/entities` are generated from the `buildLootTable` method of each variant. Start the game with `-Dspecialmobs.generateLoot=<directory>` to write them there.

The dev client occasionally crashes on startup with `Splash thread Exception` and a `ConcurrentModificationException` from the loader. This comes from the loader and not from this mod; starting it again works.

### Layout

| Package | Contents |
| --- | --- |
| `bestiary` | The mob families and their species, with the entity names and network ids kept from the original |
| `config` | The config file and the parsing of its location-specific lists |
| `spawn` | Replacing vanilla mobs as they enter the world |
| `entity` | The mobs, one package per family. `<Family>Species` declares the family's species |
| `ai`, `loot`, `network` | AI tasks, loot table building and the texture packet |
| `mixin` | Accessors for vanilla fields and the hooks into vanilla spawning |
| `client` | Renderers, the config screen and other client-only code |
| `dev` | The self-test |

## License and credits

Original mod by FatherToast, with thanks to Mother Toast. Cleanroom port by Nicholas DellaGrotte.

Licensed under the GNU General Public License v3.0, see [LICENSE](LICENSE).
