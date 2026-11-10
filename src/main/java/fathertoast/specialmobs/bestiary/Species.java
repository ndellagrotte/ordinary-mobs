/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.bestiary;

import fathertoast.specialmobs.SpecialMobsMod;
import fathertoast.specialmobs.loot.LootTableBuilder;
import org.jspecify.annotations.Nullable;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * One kind of mob added by this mod: either a family's vanilla replacement or one of its special variants.
 *
 * @param family      The family this species belongs to.
 * @param name        The variant name such as "Fire", or the family name for the vanilla replacement.
 * @param entityName  The entity name such as "FireZombie". Lower-cased, it is the path of the registry name.
 * @param index       The position within the family: 0 for the vanilla replacement, 1 and up for variants.
 * @param entityClass The entity class.
 * @param factory     Creates a new entity of this species.
 * @param info        Spawn egg colour and default spawn weights.
 * @param lootBuilder Builds the species' own loot table for the loot table generator. Null if it has none.
 * @param lootTable   The id of the species' own loot table. Null if it uses its vanilla counterpart's.
 */
public record Species(MobFamily family,
    String name,
    String entityName,
    int index,
    Class<? extends EntityLiving> entityClass,
    Function<World, ? extends EntityLiving> factory,
    BestiaryInfo info,
    @Nullable Consumer<LootTableBuilder> lootBuilder,
    @Nullable ResourceLocation lootTable
) {

    /** @return True for a special variant, false for a family's vanilla replacement. */
    public boolean isSpecial() {
        return index > 0;
    }

    /** @return The network id, unchanged from the original mod. */
    public int networkId() {
        return family.firstNetworkId + index;
    }

    /** @return The registry name, unchanged from the original mod. */
    public ResourceLocation registryName() {
        return new ResourceLocation(SpecialMobsMod.MOD_ID, entityName);
    }

    /** @return The lower-case variant name used in config keys and loot table paths. */
    public String key() {
        return name.toLowerCase(Locale.ROOT);
    }

    /** @return A new entity of this species. It is not yet in the world. */
    public EntityLiving create(World world) {
        return factory.apply(world);
    }

}
