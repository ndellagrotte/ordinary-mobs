/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.config;

import fathertoast.specialmobs.bestiary.MobFamily;
import fathertoast.specialmobs.bestiary.SpawnThemes;
import fathertoast.specialmobs.bestiary.Species;
import fathertoast.specialmobs.config.environment.EnvironmentList;
import fathertoast.specialmobs.config.environment.SpawnLocation;

import net.minecraftforge.common.config.Configuration;

import java.util.List;

/** The options every mob family has: whether and how often its mobs are replaced, and each variant's weight. */
public class FamilyConfig extends ConfigCategory {

    private static final String ENVIRONMENT_TYPE = "Environment_Array";
    private static final String ENVIRONMENT_DESCRIPTION = "biome/mod_id:biome_id=value, biome/mod_id:prefix*=value, dimension/dimension_id=value";

    public final MobFamily family;

    /** True if vanilla mobs that do not become a special variant are swapped for the vanilla-like version. */
    public final boolean REPLACE_VANILLA;

    private final float specialChance;
    private final EnvironmentList specialChanceExceptions;

    private final int[] variantWeights;
    private final EnvironmentList[] variantWeightExceptions;

    FamilyConfig(Configuration file, MobFamily family) {
        super(file, family.key.replace(' ', '_'), "Options related to special " + family.key + ".");
        this.family = family;

        REPLACE_VANILLA = prop("_replace_vanilla", true,
            "When true, all vanilla " + family.key + " are replaced by 'vanilla-like' versions.\n" +
                "This enables all of the mod's options that apply to special variants to also apply to vanilla mobs,\n" +
                "in addition to all the nbt editing capabilities that special mobs have."
        );

        specialChance = prop("_special_chance", 0.6F, "The chance for " + family.key + " to spawn as special variants."
        );
        specialChanceExceptions = environmentList("_special_chance_exceptions",
            new String[] {
                SpawnThemes.dimension("overworld", 0.25F),
                SpawnThemes.dimension("the_nether", 0.33F),
                SpawnThemes.dimension("the_end", 0.33F),
            },
            "The chance for " + family.key + " to spawn as special variants when spawning in particular locations.\n" +
                "More specific locations take priority over others (biome < biome* < dimension < global setting)."
        );

        List<Species> variants = family.variants();
        variantWeights = new int[variants.size()];
        variantWeightExceptions = new EnvironmentList[variants.size()];
        for (int i = 0; i < variantWeights.length; i++) {
            Species variant = variants.get(i);
            String key = variant.key();
            variantWeights[i] = prop(key + "_weight", variant.info().weight(),
                "The weight for the '" + key + "' variant to be picked when " + family.key +
                    " spawn as special variants."
            );
            variantWeightExceptions[i] = environmentList(key + "_weight_exceptions", variant.info().weightExceptions(),
                "The weight for the '" + key + "' variant to be picked in particular locations."
            );
        }
    }

    @Override
    float[] floatRange() {
        return R_FLT_ONE;
    }

    private EnvironmentList environmentList(String key, String[] defaultValues, String comment) {
        String[] lines = cprop(key, defaultValues, comment, ENVIRONMENT_TYPE, ENVIRONMENT_DESCRIPTION).getStringList();
        return new EnvironmentList(lines, Config.log::error);
    }

    /** @return The chance for this family's mobs to spawn as a special variant at a location. */
    public float specialChance(SpawnLocation location) {
        return specialChanceExceptions.valueFor(location, specialChance);
    }

    /** @return The weight of the variant at a position in {@link MobFamily#variants()} at a location. */
    public int variantWeight(int variantIndex, SpawnLocation location) {
        return (int) variantWeightExceptions[variantIndex].valueFor(location, variantWeights[variantIndex]);
    }

}
