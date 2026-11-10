/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.zombie;

import fathertoast.specialmobs.entity.MobHelper;
import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityBruteZombie extends Entity_SpecialZombie {

    private static final ResourceLocation[] TEXTURES = {
        new ResourceLocation(getTexturePath("brute")),
        null,
        new ResourceLocation(getTexturePath("brute_overlay"))
    };

    public EntityBruteZombie(World world) {
        super(world);
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

        getSpecialData().addAttribute(SharedMonsterAttributes.MAX_HEALTH, 10.0F);
        getSpecialData().addAttribute(SharedMonsterAttributes.ARMOR, 10.0F);
    }

    // Overridden to modify attack effects.
    @Override
    protected void onTypeAttack(Entity target) {
        if (target instanceof EntityLivingBase) {
            MobHelper.dealDirectDamage(this, (EntityLivingBase) target, 2.0F);
        }
    }

    @Override
    protected EntityArrow getArrow(float distanceFactor) {
        EntityArrow arrow = super.getArrow(distanceFactor);

        if (arrow instanceof EntityTippedArrow) {
            ((EntityTippedArrow) arrow).addEffect(new PotionEffect(MobEffects.INSTANT_DAMAGE, 1, 0));
        }

        return arrow;
    }

}
