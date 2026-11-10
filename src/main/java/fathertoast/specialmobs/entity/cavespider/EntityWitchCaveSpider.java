/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.cavespider;

import fathertoast.specialmobs.entity.MobHelper;
import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityWitchCaveSpider extends Entity_SpecialCaveSpider {

    private static final ResourceLocation[] TEXTURES = {
        new ResourceLocation(getTexturePath("witch")),
        new ResourceLocation(getTexturePath("witch_eyes"))
    };

    public EntityWitchCaveSpider(World world) {
        super(world);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addLootTable("common", "Witch loot", LootTableList.ENTITIES_WITCH);
    }

    @Override
    public ResourceLocation[] getDefaultTextures() {
        return TEXTURES;
    }

    /** Called to modify the mob's attributes based on the variant. */
    @Override
    protected void applyTypeAttributes() {
        experienceValue += 2;
    }

    // Returns true if the potion can be applied.
    @Override
    public boolean isPotionApplicable(PotionEffect effect) {
        // Witch spider is immune to negative potion effects
        return effect != null && !effect.getPotion().isBadEffect() && super.isPotionApplicable(effect);
    }

    // Overridden to modify attack effects.
    @Override
    public void onTypeAttack(Entity target) {
        if (target instanceof EntityLivingBase) {
            ((EntityLivingBase) target).addPotionEffect(MobHelper.nextWitchSpiderEffect(rand, world, false));
            MobHelper.stealPotionEffect(this, (EntityLivingBase) target);
        }
    }

}
