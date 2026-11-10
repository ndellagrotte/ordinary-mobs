/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.blaze;

import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityInfernoBlaze extends Entity_SpecialBlaze {

    private static final ResourceLocation[] TEXTURES = { new ResourceLocation(getTexturePath("inferno")) };

    public EntityInfernoBlaze(World world) {
        super(world);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addCommonDrop("common", "Blaze powder", Items.BLAZE_POWDER);
        loot.addSemicommonDrop("semicommon", "Fire charge", Items.FIRE_CHARGE);
    }

    @Override
    public ResourceLocation[] getDefaultTextures() {
        return TEXTURES;
    }

    /** Called to modify the mob's attributes based on the variant. */
    @Override
    protected void applyTypeAttributes() {
        experienceValue += 2;

        getSpecialData().addAttribute(SharedMonsterAttributes.MAX_HEALTH, 10.0);
    }

    @Override
    protected void initTypeAI() {
        getSpecialData().rangedAttackSpread *= 3.0F;
        setRangedAI(12, 2, 80, 100, 20.0F);
    }

    // Overridden to modify attack effects.
    @Override
    protected void onTypeAttack(Entity target) {
        if (target instanceof EntityLivingBase) {
            ((EntityLivingBase) target).setHealth(((EntityLivingBase) target).getHealth() - 2.0F);
        }
    }

}
