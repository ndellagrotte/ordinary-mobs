/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.spider;

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

public class EntityPaleSpider extends Entity_SpecialSpider {

    private static final ResourceLocation[] TEXTURES = {
        new ResourceLocation(getTexturePath("pale")),
        new ResourceLocation(getTexturePath("pale_eyes"))
    };

    public EntityPaleSpider(World world) {
        super(world);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addSemicommonDrop("semicommon", "Fermented eye", Items.FERMENTED_SPIDER_EYE);
    }

    @Override
    public ResourceLocation[] getDefaultTextures() {
        return TEXTURES;
    }

    /** Called to modify the mob's attributes based on the variant. */
    @Override
    protected void applyTypeAttributes() {
        experienceValue += 1;

        getSpecialData().addAttribute(SharedMonsterAttributes.ARMOR, 15.0);
    }

    // Overridden to modify attack effects.
    @Override
    public void onTypeAttack(Entity target) {
        if (target instanceof EntityLivingBase) {
            int duration = MobHelper.getDebuffDuration(world.getDifficulty());

            ((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, duration, 0));
            ((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, duration, 1));
        }
    }

}
