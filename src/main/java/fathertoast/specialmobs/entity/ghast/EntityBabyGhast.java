/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.ghast;

import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityBabyGhast extends Entity_SpecialGhast {

    public EntityBabyGhast(World world) {
        super(world);
        setSize(1.0F, 1.0F);
        getSpecialData().setBaseScale(0.25F);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        loot.addCommonDrop("common", "Gunpowder", Items.GUNPOWDER, 1);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    /** Called to modify the mob's attributes based on the variant. */
    @Override
    protected void applyTypeAttributes() {
        experienceValue = 1;

        getSpecialData().addAttribute(SharedMonsterAttributes.ATTACK_DAMAGE, -1.0);
        getSpecialData().multAttribute(SharedMonsterAttributes.MOVEMENT_SPEED, 1.3);
    }

    @Override
    protected void initTypeAI() {
        disableRangedAI();
    }

}
