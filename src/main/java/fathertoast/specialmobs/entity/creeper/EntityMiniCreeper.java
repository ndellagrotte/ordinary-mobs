/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.creeper;

import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityMiniCreeper extends Entity_SpecialCreeper {

    public EntityMiniCreeper(World world) {
        super(world);
        setSize(0.5F, 0.9F);
        getSpecialData().setBaseScale(0.5F);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
    }

    /** Called to modify the mob's attributes based on the variant. */
    @Override
    protected void applyTypeAttributes() {
        getSpecialData().multAttribute(SharedMonsterAttributes.MOVEMENT_SPEED, 1.3);
    }

    // The explosion caused by this creeper.
    @Override
    public void explodeByType(boolean powered, boolean griefing) {
        float power = (float) explosionRadius / 2.0F * (powered ? 2.0F : 1.0F);
        world.createExplosion(this, posX, posY, posZ, power, griefing);
    }

}
