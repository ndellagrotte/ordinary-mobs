/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.zombie;

import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityGiantZombie extends Entity_SpecialZombie {

    public EntityGiantZombie(World world) {
        super(world);
        stepHeight = 1.0F;
        setSize(0.9F, 2.7F);
        multiplySize(1.0F); // Zombie thing to actually update the size
        getSpecialData().setBaseScale(1.5F);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addGuaranteedDrop("base", "Rotten flesh", Items.ROTTEN_FLESH, 2);
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
