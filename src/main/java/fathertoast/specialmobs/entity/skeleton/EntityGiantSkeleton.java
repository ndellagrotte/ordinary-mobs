/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.skeleton;

import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityGiantSkeleton extends Entity_SpecialSkeleton {

    public EntityGiantSkeleton(World world) {
        super(world);
        stepHeight = 1.0F;
        getSpecialData().setBaseScale(1.5F);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addGuaranteedDrop("base", "Bones", Items.BONE, 2);
    }

    @Override
    protected void setCollisionSize(float multiplier) {
        setSize(0.9F * multiplier, 2.7F * multiplier);
    }

    /** Called to modify the mob's attributes based on the variant. */
    @Override
    protected void applyTypeAttributes() {
        experienceValue += 1;

        getSpecialData().addAttribute(SharedMonsterAttributes.MAX_HEALTH, 20.0);
        getSpecialData().addAttribute(SharedMonsterAttributes.ATTACK_DAMAGE, 2.0);
    }

    @Override
    protected void initTypeAI() {
        getSpecialData().rangedAttackDamage += 2.0F;
    }

    @Override
    public boolean isChild() {
        return false;
    }

    @Override
    public void setChild(boolean child) { }

}
