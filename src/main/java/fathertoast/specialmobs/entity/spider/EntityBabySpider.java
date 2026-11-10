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

public class EntityBabySpider extends Entity_SpecialSpider {

    public EntityBabySpider(World world) {
        super(world);
        setSize(0.6F, 0.4F);
        getSpecialData().setBaseScale(0.4F);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        loot.addCommonDrop("common", "String", Items.STRING, 1);
    }

    /** Called to modify the mob's attributes based on the variant. */
    @Override
    protected void applyTypeAttributes() {
        experienceValue = 1;

        getSpecialData().multAttribute(SharedMonsterAttributes.MAX_HEALTH, 1.0 / 4.0);
        getSpecialData().addAttribute(SharedMonsterAttributes.ATTACK_DAMAGE, -1.0);
        getSpecialData().multAttribute(SharedMonsterAttributes.MOVEMENT_SPEED, 1.3);
    }

    @Override
    protected void initTypeAI() {
        getSpecialData().rangedAttackDamage -= 1.0F;
        getSpecialData().rangedAttackMaxRange = 0.0F;
    }

}
