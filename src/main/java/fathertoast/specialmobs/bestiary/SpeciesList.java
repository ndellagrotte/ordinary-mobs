/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.bestiary;

import fathertoast.specialmobs.SpecialMobsMod;
import fathertoast.specialmobs.entity.ISpecialMob;
import fathertoast.specialmobs.loot.LootTableBuilder;
import org.jspecify.annotations.Nullable;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Collects the species a family declares. Each family package has one class that fills this in, which is the only
 * place that has to know the family's entity classes.
 */
public final class SpeciesList {

    private final MobFamily family;
    private final String[] variantNames;
    private final @Nullable Species[] variants;

    private @Nullable Species base;

    SpeciesList(MobFamily family, String[] variantNames) {
        this.family = family;
        this.variantNames = variantNames;
        variants = new Species[variantNames.length];
    }

    /** Declares the family's vanilla replacement. */
    public <T extends EntityLiving & ISpecialMob> void base(Class<T> entityClass, Function<World, T> factory,
        int eggSpotsColor
    ) {
        if (base != null) {
            throw new IllegalStateException("Mob family '" + family.name + "' already has a vanilla replacement");
        }
        base = new Species(family, family.name, LegacyIds.baseEntityName(family.name), 0, entityClass, factory,
            new BestiaryInfo(eggSpotsColor), null, null
        );
    }

    /**
     * Declares one of the family's special variants.
     *
     * @param name The variant name. It must be one of the names the original mod used for the family.
     */
    public <T extends EntityLiving & ISpecialMob> void variant(String name, Class<T> entityClass,
        Function<World, T> factory, BestiaryInfo info, Consumer<LootTableBuilder> lootBuilder
    ) {
        int i = indexOf(name);
        if (variants[i] != null) {
            throw new IllegalStateException("Variant '" + name + "' declared twice for mob family '" + family.name + "'"
            );
        }
        ResourceLocation lootTable = new ResourceLocation(SpecialMobsMod.MOD_ID,
            "entities/" + family.name.toLowerCase(Locale.ROOT) + "/" + name.toLowerCase(Locale.ROOT)
        );
        variants[i] = new Species(family, name, LegacyIds.variantEntityName(family.name, name), i + 1, entityClass,
            factory, info, lootBuilder, lootTable
        );
    }

    @Nullable
    Species base() {
        return base;
    }

    /** @return The declared variants in network id order, or null if the family is not completely declared. */
    Species @Nullable [] variants() {
        if (base == null) {
            return null;
        }
        Species[] complete = new Species[variants.length];
        for (int i = 0; i < variants.length; i++) {
            Species variant = variants[i];
            if (variant == null) {
                throw new IllegalStateException("Mob family '" + family.name + "' does not declare its '" +
                        variantNames[i] + "' variant"
                );
            }
            complete[i] = variant;
        }
        return complete;
    }

    private int indexOf(String name) {
        for (int i = 0; i < variantNames.length; i++) {
            if (variantNames[i].equals(name)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Mob family '" + family.name + "' has no variant named '" + name + "'");
    }

}
