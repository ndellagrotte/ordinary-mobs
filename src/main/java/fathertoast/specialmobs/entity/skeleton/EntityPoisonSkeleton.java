/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.skeleton;

import fathertoast.specialmobs.entity.MobHelper;
import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityPoisonSkeleton extends Entity_SpecialSkeleton {

    private static final ResourceLocation[] TEXTURES = {
        new ResourceLocation(getTexturePath("poison")),
        null,
        new ResourceLocation(getTexturePath("poison_overlay"))
    };

    public EntityPoisonSkeleton(World world) {
        super(world);
        getSpecialData().addPotionImmunity(MobEffects.POISON);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        ItemStack arrow = new ItemStack(Items.TIPPED_ARROW);
        PotionUtils.addPotionToItemStack(arrow, PotionTypes.POISON);
        loot.addUncommonDrop("uncommon", "Poison arrow", arrow);
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
    protected void onTypeAttack(Entity target) {
        if (target instanceof EntityLivingBase) {
            int duration = MobHelper.getDebuffDuration(world.getDifficulty());

            ((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.POISON, duration));
        }
    }

    @Override
    protected EntityArrow getArrow(float distanceFactor) {
        EntityArrow arrow = super.getArrow(distanceFactor);

        if (arrow instanceof EntityTippedArrow) {
            int duration = MobHelper.getDebuffDuration(world.getDifficulty());

            ((EntityTippedArrow) arrow).addEffect(new PotionEffect(MobEffects.POISON, duration));
        }

        return arrow;
    }

}
