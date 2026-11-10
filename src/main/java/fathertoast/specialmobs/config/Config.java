/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.config;

import fathertoast.specialmobs.bestiary.MobFamily;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * The user-defined options of this mod, as an immutable snapshot of the config file. Reloading the config makes a
 * new snapshot, so always go through {@link #get()} and do not hold on to the result.
 */
public final class Config {

    private static final List<Runnable> RELOAD_LISTENERS = new ArrayList<>();

    static Logger log;

    private static Configuration file;
    private static volatile Config instance;

    public final General GENERAL;

    public final Creepers CREEPERS;
    public final Zombies ZOMBIES;
    public final ZombiePigmen ZOMBIE_PIGMEN;
    public final Skeletons SKELETONS;
    public final WitherSkeletons WITHER_SKELETONS;
    public final Slimes SLIMES;
    public final FamilyConfig MAGMA_CUBES;
    public final FamilyConfig SPIDERS;
    public final FamilyConfig CAVE_SPIDERS;
    public final Silverfish SILVERFISH;
    public final Endermen ENDERMEN;
    public final FamilyConfig WITCHES;
    public final FamilyConfig GHASTS;
    public final FamilyConfig BLAZES;

    private final Map<MobFamily, FamilyConfig> families = new EnumMap<>(MobFamily.class);
    private final List<String> categoryNames = new ArrayList<>();

    private Config(Configuration file) {
        GENERAL = new General(file);
        categoryNames.add(GENERAL.name);

        CREEPERS = add(new Creepers(file));
        ZOMBIES = add(new Zombies(file));
        ZOMBIE_PIGMEN = add(new ZombiePigmen(file));
        SKELETONS = add(new Skeletons(file));
        WITHER_SKELETONS = add(new WitherSkeletons(file));
        SLIMES = add(new Slimes(file));
        MAGMA_CUBES = add(new FamilyConfig(file, MobFamily.MAGMA_CUBE));
        SPIDERS = add(new FamilyConfig(file, MobFamily.SPIDER));
        CAVE_SPIDERS = add(new FamilyConfig(file, MobFamily.CAVE_SPIDER));
        SILVERFISH = add(new Silverfish(file));
        ENDERMEN = add(new Endermen(file));
        WITCHES = add(new FamilyConfig(file, MobFamily.WITCH));
        GHASTS = add(new FamilyConfig(file, MobFamily.GHAST));
        BLAZES = add(new FamilyConfig(file, MobFamily.BLAZE));
    }

    /** @return The current options. */
    public static Config get() {
        return instance;
    }

    /** @return The underlying config file. Used by the in-game config screen. */
    public static Configuration file() {
        return file;
    }

    /** Loads the config file, creating it or adding missing options to it as needed. */
    public static void load(Logger logger, File configFile) {
        log = logger;
        log.info("Loading configs...");
        long startTime = System.nanoTime();

        file = new Configuration(configFile);
        file.load();
        instance = new Config(file);
        file.save();

        log.info("Loaded configs in {} ms", (System.nanoTime() - startTime) / 1.0E6);
    }

    /** Applies the option values currently held by {@link #file()}, and writes them to disk if they changed. */
    public static void reload() {
        instance = new Config(file);
        if (file.hasChanged()) {
            file.save();
        }
        for (Runnable listener : RELOAD_LISTENERS) {
            listener.run();
        }
    }

    /** Registers something to run each time the options change. */
    public static void onReload(Runnable listener) {
        RELOAD_LISTENERS.add(listener);
    }

    private <T extends FamilyConfig> T add(T familyConfig) {
        families.put(familyConfig.family, familyConfig);
        categoryNames.add(familyConfig.name);
        return familyConfig;
    }

    /** @return The options of a mob family. */
    public FamilyConfig family(MobFamily family) {
        return families.get(family);
    }

    /** @return The names of the categories in the config file, general options first. */
    public List<String> categoryNames() {
        return List.copyOf(categoryNames);
    }

    public static final class General extends ConfigCategory {

        public final boolean DISABLE_NAUSEA;
        public final boolean FANCY_FISHING_MOBS;
        public final float RANDOM_SCALING;
        public final boolean SPAWN_EGGS;
        public final boolean SPAWNING;

        General(Configuration file) {
            super(file, "_general", "General and/or miscellaneous options.");

            DISABLE_NAUSEA = prop("disable_nausea_effects", false,
                "If true, prevents any of this mod's mobs from applying nausea when they normally would.\n" +
                    "Use this if the screen warping from nausea hurts your face or makes you sick."
            );

            Property fancyFishingMobs = cprop("fancy_fishing_mobs", true,
                "Overrides the default fishing rod item animation so that it is compatible with fishing mobs from this mod.\n" +
                    "Disable this if it causes problems with another mod for some reason. Fishing mobs will instead render a stick while casting."
            );
            fancyFishingMobs.setRequiresMcRestart(true);
            FANCY_FISHING_MOBS = fancyFishingMobs.getBoolean();

            RANDOM_SCALING = prop("random_scaling", 0.15F,
                "When greater than 0, mobs will have a random render scale applied. This is a visual effect only.\n" +
                    "With a value of 0.2, mob scale can vary by 20% (+/- 10% from normal size).\n" +
                    "This only affects special variants and vanilla-replacement mobs."
            );

            Property spawnEggs = cprop("spawn_eggs", true,
                "If true, the mod will attempt to generate a spawn egg for each variant mob.\n" +
                    "Disable this if your 'misc' creative tab is too full, or if you need to for whatever reason."
            );
            spawnEggs.setRequiresMcRestart(true);
            SPAWN_EGGS = spawnEggs.getBoolean();

            SPAWNING = prop("spawning", true,
                "Set this to false to disable mob spawning from this mod entirely. Do this if you want\n" +
                    "to handle the spawning through another mod."
            );
        }

    }

    public static final class Creepers extends FamilyConfig {

        public final float CHARGED_CHANCE;

        Creepers(Configuration file) {
            super(file, MobFamily.CREEPER);
            CHARGED_CHANCE = prop("_charged_chance", 0.01F, "Chance for creepers to spawn charged during thunderstorms."
            );
        }

    }

    public static final class Zombies extends FamilyConfig {

        public final float BOW_CHANCE;
        public final float CHICKEN_JOCKEY_CHANCE;
        public final float CHICKEN_JOCKEY_FORCED_CHANCE;

        Zombies(Configuration file) {
            super(file, MobFamily.ZOMBIE);
            BOW_CHANCE = prop("_bow_chance", 0.05F, "Chance for valid zombies to spawn with bows.");
            CHICKEN_JOCKEY_CHANCE = prop("_chicken_jockey_chance", 1.0F,
                "Chance for baby zombies to spawn riding on a pre-existing chicken near their spawn point.\n" +
                    "The vanilla chance for this is 5%, like the forced chicken jockey version.\n" +
                    "See the Forge config for zombie baby chance (forge.cfg)."
            );
            CHICKEN_JOCKEY_FORCED_CHANCE = prop("_chicken_jockey_forced_chance", 0.05F,
                "Chance for baby zombies to spawn with a chicken mount.\n" +
                    "See the Forge config for zombie baby chance (forge.cfg)."
            );
        }

    }

    public static final class ZombiePigmen extends FamilyConfig {

        public final float BOW_CHANCE;
        public final float CHICKEN_JOCKEY_CHANCE;
        public final float CHICKEN_JOCKEY_FORCED_CHANCE;

        ZombiePigmen(Configuration file) {
            super(file, MobFamily.ZOMBIE_PIGMAN);
            BOW_CHANCE = prop("_bow_chance", 0.2F, "Chance for valid zombie pigmen to spawn with bows.");
            CHICKEN_JOCKEY_CHANCE = prop("_chicken_jockey_chance", 1.0F,
                "Chance for baby zombie pigmen to spawn riding on a pre-existing chicken near their spawn point.\n" +
                    "The vanilla chance for this is 5%, like the forced chicken jockey version.\n" +
                    "See the Forge config for zombie baby chance (forge.cfg)."
            );
            CHICKEN_JOCKEY_FORCED_CHANCE = prop("_chicken_jockey_forced_chance", 0.05F,
                "Chance for baby zombie pigmen to spawn with a chicken mount.\n" +
                    "See the Forge config for zombie baby chance (forge.cfg)."
            );
        }

    }

    public static final class Skeletons extends FamilyConfig {

        public final float BABY_CHANCE;
        public final float BOW_CHANCE;

        Skeletons(Configuration file) {
            super(file, MobFamily.SKELETON);
            BABY_CHANCE = prop("_baby_chance", 0.05F, "Chance for skeletons to spawn as babies.");
            BOW_CHANCE = prop("_bow_chance", 0.95F, "Chance for valid skeletons to spawn with bows.");
        }

    }

    public static final class WitherSkeletons extends FamilyConfig {

        public final float BABY_CHANCE;
        public final float BOW_CHANCE;

        WitherSkeletons(Configuration file) {
            super(file, MobFamily.WITHER_SKELETON);
            BABY_CHANCE = prop("_baby_chance", 0.05F, "Chance for wither skeletons to spawn as babies.");
            BOW_CHANCE = prop("_bow_chance", 0.05F, "Chance for valid wither skeletons to spawn with bows.");
        }

    }

    public static final class Slimes extends FamilyConfig {

        public final boolean TINY_SLIME_DAMAGE;

        Slimes(Configuration file) {
            super(file, MobFamily.SLIME);
            TINY_SLIME_DAMAGE = prop("_tiny_slime_damage", true,
                "Setting this to false makes tiny slimes incapable of dealing damage, like in vanilla.\n" +
                    "I don't recommend disabling this, but the option is here if you want to revert the change."
            );
        }

    }

    public static final class Silverfish extends FamilyConfig {

        public final float AGGRESSIVE_CHANCE;

        Silverfish(Configuration file) {
            super(file, MobFamily.SILVERFISH);
            AGGRESSIVE_CHANCE = prop("_aggressive_chance", 0.2F,
                "Chance for silverfish to spawn already calling for reinforcements."
            );
        }

    }

    public static final class Endermen extends FamilyConfig {

        /** The config lines naming the blocks endermen can carry. Resolve them with {@link BlockTarget}. */
        public final String[] CARRIABLE_BLOCKS;

        Endermen(Configuration file) {
            super(file, MobFamily.ENDERMAN);
            Property carriableBlocks = cprop("_carriable_blocks",
                new String[] {
                    "minecraft:grass",
                    "minecraft:mycelium",
                    "minecraft:dirt",
                    "minecraft:clay",
                    "minecraft:sand",
                    "minecraft:gravel",
                    "minecraft:yellow_flower",
                    "minecraft:red_flower",
                    "minecraft:brown_mushroom",
                    "minecraft:red_mushroom",
                    "minecraft:pumpkin",
                    "minecraft:melon_block",
                    "minecraft:cactus",
                    "minecraft:tnt",
                    "minecraft:netherrack",
                },
                "Set of blocks that can be moved around by endermen. Removing all entries here disables enderman griefing.\n" +
                    "Other mods can still add/remove blocks from this list, so be aware of which mods do.\n" +
                    "Unlike most config options here, this affects vanilla endermen in addition to the endermen from this mod (and probably other mods).",
                "Block_Array", "mod_id:block_id, mod_id:block_id meta, mod_id:*"
            );
            carriableBlocks.setRequiresMcRestart(true);
            CARRIABLE_BLOCKS = carriableBlocks.getStringList();
        }

    }

}
