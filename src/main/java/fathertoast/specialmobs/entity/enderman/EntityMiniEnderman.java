/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.enderman;

import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityMiniEnderman extends Entity_SpecialEnderman {

    public EntityMiniEnderman(World world) {
        super(world);
        stepHeight = 0.5F;
        setSize(0.5F, 0.9F);
        getSpecialData().setBaseScale(0.35F);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
    }

    /** Called to modify the mob's attributes based on the variant. */
    @Override
    protected void applyTypeAttributes() {
        experienceValue += 1;

        getSpecialData().addAttribute(SharedMonsterAttributes.ATTACK_DAMAGE, -2.0);
        getSpecialData().multAttribute(SharedMonsterAttributes.MOVEMENT_SPEED, 1.3);
    }

}
