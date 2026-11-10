/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.blaze;

import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityCinderBlaze extends Entity_SpecialBlaze {

    public EntityCinderBlaze(World world) {
        super(world);
        setSize(0.5F, 0.9F);
        getSpecialData().setBaseScale(0.5F);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        loot.addCommonDrop("common", "Blaze powder", Items.BLAZE_POWDER, 1);
    }

    @Override
    protected void applyTypeAttributes() {
        experienceValue = 1;

        getSpecialData().addAttribute(SharedMonsterAttributes.ATTACK_DAMAGE, -2.0);
        getSpecialData().multAttribute(SharedMonsterAttributes.MOVEMENT_SPEED, 1.3);
    }

    @Override
    protected void initTypeAI() {
        getSpecialData().rangedAttackDamage -= 1.0F;
        disableRangedAI();
    }

    // Overridden to modify attack effects.
    @Override
    protected void onTypeAttack(Entity target) {
        target.setFire(4);
    }

}
