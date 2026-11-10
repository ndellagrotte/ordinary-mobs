/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.spawn;

import fathertoast.specialmobs.bestiary.MobFamily;
import fathertoast.specialmobs.bestiary.Species;
import fathertoast.specialmobs.config.Config;
import fathertoast.specialmobs.config.FamilyConfig;
import fathertoast.specialmobs.config.environment.ResourceName;
import fathertoast.specialmobs.config.environment.SpawnLocation;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Random;

/**
 * The spawn settings of every mob family at every location, worked out once per dimension and biome.
 * <p>
 * The config describes these with lists that have to be searched, which is far too slow to do for each variant on
 * each spawn. The settings only depend on the dimension and the biome though, so the result is remembered.
 * <p>
 * Only the server thread reads this. Reloading the config swaps in a fresh, empty table.
 */
public final class WeightTable {

    private static volatile WeightTable current = new WeightTable();

    static {
        Config.onReload(() -> current = new WeightTable());
    }

    /** Per family, by ordinal: dimension id to biome to settings. Biomes are compared by identity. */
    private final Map<Integer, Map<Biome, Entry>>[] entries;

    @SuppressWarnings("unchecked")
    private WeightTable() {
        entries = new Map[MobFamily.values().length];
        for (int i = 0; i < entries.length; i++) {
            entries[i] = new HashMap<>();
        }
    }

    /** @return The settings of a family at a position in a world. */
    public static Entry get(MobFamily family, World world, BlockPos pos) {
        return current.lookUp(family, world, world.getBiome(pos));
    }

    /** Describes a location for the config. A mutated biome counts as the biome it is a mutation of. */
    private static SpawnLocation locate(World world, Biome biome) {
        int baseBiomeId = Biome.MUTATION_TO_BASE_ID_MAP.get(biome);
        if (baseBiomeId >= 0) {
            Biome baseBiome = Biome.getBiome(baseBiomeId);
            if (baseBiome != null) {
                biome = baseBiome;
            }
        }
        ResourceLocation biomeName = biome.getRegistryName();
        return new SpawnLocation(world.provider.getDimensionType().getName(), world.provider.getDimension(),
            biomeName == null
                ? new ResourceName(ResourceName.DEFAULT_NAMESPACE, "")
                : new ResourceName(biomeName.getNamespace(), biomeName.getPath()),
            Biome.getIdForBiome(biome)
        );
    }

    private Entry lookUp(MobFamily family, World world, Biome biome) {
        Map<Biome, Entry> byBiome = entries[family.ordinal()].computeIfAbsent(world.provider.getDimension(),
            dimension -> new IdentityHashMap<>()
        );
        Entry entry = byBiome.get(biome);
        if (entry == null) {
            entry = new Entry(family, Config.get().family(family), locate(world, biome));
            byBiome.put(biome, entry);
        }
        return entry;
    }

    /** The settings of one family at one location. */
    public static final class Entry {

        /** The chance for the family's mobs to become a special variant here. */
        public final float specialChance;

        private final MobFamily family;
        private final int[] variantWeights;
        private final int totalWeight;

        private Entry(MobFamily family, FamilyConfig config, SpawnLocation location) {
            this.family = family;
            specialChance = config.specialChance(location);
            variantWeights = new int[family.variantCount()];
            for (int i = 0; i < variantWeights.length; i++) {
                variantWeights[i] = config.variantWeight(i, location);
            }
            totalWeight = WeightedChoice.totalWeight(variantWeights);
        }

        /** @return A random special variant, or the vanilla replacement if every variant is disabled here. */
        public Species pickVariant(Random random) {
            int picked = WeightedChoice.pick(variantWeights, totalWeight, random);
            return picked < 0 ? family.base() : family.variant(picked);
        }

    }

}
