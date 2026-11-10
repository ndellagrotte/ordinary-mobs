/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.silverfish;

import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityToughSilverfish extends Entity_SpecialSilverfish {

    private static final ResourceLocation[] TEXTURES = { new ResourceLocation(getTexturePath("tough")) };

    public EntityToughSilverfish(World world) {
        super(world);
        setSize(0.6F, 0.9F);
        getSpecialData().setBaseScale(1.5F);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addCommonDrop("common", "Flint", Items.FLINT, 1);
        loot.addRareDrop("rare", "Iron ingot", Items.IRON_INGOT);
    }

    @Override
    public ResourceLocation[] getDefaultTextures() {
        return TEXTURES;
    }

    /** Called to modify the mob's attributes based on the variant. */
    @Override
    protected void applyTypeAttributes() {
        experienceValue += 2;

        getSpecialData().addAttribute(SharedMonsterAttributes.MAX_HEALTH, 16.0);
        getSpecialData().addAttribute(SharedMonsterAttributes.ARMOR, 15.0);
        getSpecialData().addAttribute(SharedMonsterAttributes.ATTACK_DAMAGE, 2.0);
        getSpecialData().multAttribute(SharedMonsterAttributes.MOVEMENT_SPEED, 0.7);
    }

}
