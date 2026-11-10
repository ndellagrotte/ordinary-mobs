/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.spider;

import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityGiantSpider extends Entity_SpecialSpider {

    public EntityGiantSpider(World world) {
        super(world);
        setSize(1.9F, 1.3F);
        getSpecialData().setBaseScale(1.5F);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addGuaranteedDrop("base", "String", Items.STRING, 2);
    }

    /** Called to modify the mob's attributes based on the variant. */
    @Override
    protected void applyTypeAttributes() {
        experienceValue += 1;

        getSpecialData().addAttribute(SharedMonsterAttributes.MAX_HEALTH, 16.0);
        getSpecialData().addAttribute(SharedMonsterAttributes.ATTACK_DAMAGE, 2.0);
    }

    @Override
    protected void initTypeAI() {
        getSpecialData().rangedAttackDamage += 1.0F;
    }

}
