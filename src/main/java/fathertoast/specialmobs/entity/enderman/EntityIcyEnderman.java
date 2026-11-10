/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.enderman;

import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityIcyEnderman extends Entity_SpecialEnderman {

    private static final ResourceLocation[] TEXTURES = {
        new ResourceLocation(getTexturePath("icy")),
        new ResourceLocation(getTexturePath("icy_eyes"))
    };

    public EntityIcyEnderman(World world) {
        super(world);
        getSpecialData().addPotionImmunity(MobEffects.SLOWNESS);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addClusterDrop("common", "Snowballs", Items.SNOWBALL);
        loot.addUncommonDrop("uncommon", "Ice block", Blocks.ICE);
    }

    @Override
    public ResourceLocation[] getDefaultTextures() {
        return TEXTURES;
    }

    /** Called to modify the mob's attributes based on the variant. */
    @Override
    protected void applyTypeAttributes() {
        experienceValue += 1;
    }

    // Overridden to modify attack effects.
    @Override
    public void onTypeAttack(Entity target) {
        if (target instanceof EntityLivingBase) {
            int duration;
            switch (target.world.getDifficulty()) {
                case PEACEFUL, EASY -> duration = 40;
                case NORMAL -> duration = 60;
                default -> duration = 80;
            }

            // -75% movespeed
            ((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, duration, 4));
            // -30% attackspeed
            ((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, duration, 2));
        }
    }

}
