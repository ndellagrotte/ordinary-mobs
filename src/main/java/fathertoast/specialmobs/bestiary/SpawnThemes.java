/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.bestiary;

import static fathertoast.specialmobs.bestiary.BestiaryInfo.BASE_WEIGHT_COMMON;
import static fathertoast.specialmobs.bestiary.BestiaryInfo.BASE_WEIGHT_RARE;
import static fathertoast.specialmobs.bestiary.BestiaryInfo.BASE_WEIGHT_UNCOMMON;

/**
 * Default location-specific weights shared by several species, written as the config lines they default to.
 * <p>
 * The lines have to stay exactly as the original mod wrote them, since they are compared against existing config
 * files to decide whether a value is still the default.
 */
public final class SpawnThemes {

    public static final String[] NONE = {};

    public static final String[] FIRE = {
        biomePrefix("desert", BASE_WEIGHT_COMMON),
        biomePrefix("savanna", BASE_WEIGHT_COMMON),
        biomePrefix("mesa", BASE_WEIGHT_COMMON),
        dimension("the_nether", BASE_WEIGHT_COMMON),
        biomePrefix("ice", BASE_WEIGHT_RARE),
        biomePrefix("frozen", BASE_WEIGHT_RARE),
        biomePrefix("cold", BASE_WEIGHT_RARE),
        biomePrefix("taiga_cold", BASE_WEIGHT_RARE),
    };

    public static final String[] ICE = {
        biomePrefix("ice", BASE_WEIGHT_COMMON),
        biomePrefix("frozen", BASE_WEIGHT_COMMON),
        biomePrefix("cold", BASE_WEIGHT_COMMON),
        biomePrefix("taiga_cold", BASE_WEIGHT_COMMON),
        biomePrefix("desert", BASE_WEIGHT_RARE),
        biomePrefix("savanna", BASE_WEIGHT_RARE),
        biomePrefix("mesa", BASE_WEIGHT_RARE),
        dimension("the_nether", BASE_WEIGHT_RARE),
    };

    public static final String[] FOREST = {
        biomePrefix("swamp", BASE_WEIGHT_COMMON),
        biomePrefix("forest", BASE_WEIGHT_COMMON),
        biomePrefix("birch_forest", BASE_WEIGHT_COMMON),
        biomePrefix("roofed_forest", BASE_WEIGHT_COMMON),
        biomePrefix("jungle", BASE_WEIGHT_COMMON),
        biomePrefix("taiga", BASE_WEIGHT_COMMON),
        biomePrefix("redwood_taiga", BASE_WEIGHT_COMMON),
        biomePrefix("desert", BASE_WEIGHT_RARE),
        biomePrefix("ice", BASE_WEIGHT_RARE),
        biomePrefix("stone", BASE_WEIGHT_RARE),
    };

    public static final String[] MOUNTAIN = {
        biomePrefix("extreme", BASE_WEIGHT_COMMON),
        biomePrefix("smaller_extreme", BASE_WEIGHT_COMMON),
        biomePrefix("mesa", BASE_WEIGHT_COMMON),
        biomePrefix("ice_mountains", BASE_WEIGHT_COMMON),
        biomePrefix("stone", BASE_WEIGHT_COMMON),
        biomePrefix("plains", BASE_WEIGHT_RARE),
        biomePrefix("desert", BASE_WEIGHT_RARE),
        biomePrefix("swamp", BASE_WEIGHT_RARE),
        biomePrefix("beach", BASE_WEIGHT_RARE),
        biomePrefix("cold_beach", BASE_WEIGHT_RARE),
        biomePrefix("savanna", BASE_WEIGHT_RARE),
        biomePrefix("ice_flats", BASE_WEIGHT_RARE),
    };

    public static final String[] WATER = {
        biomePrefix("swamp", BASE_WEIGHT_COMMON),
        biome("beaches", BASE_WEIGHT_COMMON),
        biome("ocean", BASE_WEIGHT_COMMON),
        biome("deep_ocean", BASE_WEIGHT_COMMON),
        biome("frozen_ocean", BASE_WEIGHT_COMMON),
        biomePrefix("desert", BASE_WEIGHT_RARE),
        biomePrefix("savanna", BASE_WEIGHT_RARE),
        dimension("the_nether", 0),
    };

    public static final String[] FISHING = {
        biomePrefix("swamp", BASE_WEIGHT_COMMON),
        biome("beaches", BASE_WEIGHT_COMMON),
        biome("ocean", BASE_WEIGHT_COMMON),
        biome("deep_ocean", BASE_WEIGHT_COMMON),
        biome("frozen_ocean", BASE_WEIGHT_COMMON),
        biomePrefix("desert", BASE_WEIGHT_UNCOMMON),
        biomePrefix("savanna", BASE_WEIGHT_UNCOMMON),
        biomePrefix("mesa", BASE_WEIGHT_UNCOMMON),
    };

    private SpawnThemes() { }

    /** @return A config line that applies to one vanilla biome. */
    public static String biome(String vanillaBiomeId, float value) {
        return "biome/minecraft:" + vanillaBiomeId + "=" + value;
    }

    /** @return A config line that applies to every vanilla biome whose id starts with the prefix. */
    public static String biomePrefix(String vanillaBiomeIdPrefix, float value) {
        return "biome/minecraft:" + vanillaBiomeIdPrefix + "*=" + value;
    }

    /** @return A config line that applies to a whole dimension. */
    public static String dimension(String dimensionName, float value) {
        return "dimension/" + dimensionName + "=" + value;
    }

}
