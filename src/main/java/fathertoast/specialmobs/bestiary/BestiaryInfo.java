/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.bestiary;

/**
 * The default spawn settings and spawn egg colour of one species. The weight values are only defaults, the ones in
 * effect come from the config.
 *
 * @param eggSpotsColor    The colour of the spots on the spawn egg.
 * @param weight           The default weight to be picked when a mob spawns as a special variant.
 * @param weightExceptions The default location-specific weights, as config lines (see {@link SpawnThemes}).
 */
public record BestiaryInfo(int eggSpotsColor, int weight, String[] weightExceptions) {

    public static final int BASE_WEIGHT = 100;
    public static final int BASE_WEIGHT_COMMON = BASE_WEIGHT * 2;
    public static final int BASE_WEIGHT_UNCOMMON = BASE_WEIGHT / 2;
    public static final int BASE_WEIGHT_RARE = BASE_WEIGHT / 5;

    public BestiaryInfo(int eggSpotsColor) {
        this(eggSpotsColor, BASE_WEIGHT, SpawnThemes.NONE);
    }

    /** @return A copy with a different default weight. */
    public BestiaryInfo withWeight(int newWeight) {
        return new BestiaryInfo(eggSpotsColor, newWeight, weightExceptions);
    }

    /** @return A copy with different default location-specific weights. */
    public BestiaryInfo withWeightExceptions(String... newWeightExceptions) {
        return new BestiaryInfo(eggSpotsColor, weight, newWeightExceptions);
    }

}
