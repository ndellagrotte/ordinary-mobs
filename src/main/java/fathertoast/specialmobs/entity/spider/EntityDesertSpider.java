/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.spider;

import fathertoast.specialmobs.config.Config;
import fathertoast.specialmobs.entity.MobHelper;
import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityDesertSpider extends Entity_SpecialSpider {

    private static final ResourceLocation[] TEXTURES = {
        new ResourceLocation(getTexturePath("desert")),
        new ResourceLocation(getTexturePath("desert_eyes"))
    };

    public EntityDesertSpider(World world) {
        super(world);
        setSize(1.0F, 0.8F);
        getSpecialData().setBaseScale(0.8F);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addCommonDrop("common", "Leather", Items.LEATHER);
    }

    @Override
    public ResourceLocation[] getDefaultTextures() {
        return TEXTURES;
    }

    /** Called to modify the mob's attributes based on the variant. */
    @Override
    protected void applyTypeAttributes() {
        experienceValue += 2;

        getSpecialData().addAttribute(SharedMonsterAttributes.MAX_HEALTH, 4.0);
    }

    // Overridden to modify attack effects.
    @Override
    public void onTypeAttack(Entity target) {
        if (target instanceof EntityLivingBase) {
            int duration = MobHelper.getDebuffDuration(world.getDifficulty());

            if (!Config.get().GENERAL.DISABLE_NAUSEA) {
                ((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.NAUSEA, duration, 0));
            }
            ((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, duration, 0));

            ((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, duration, 2));
            ((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, duration, -2));
        }
    }

}
