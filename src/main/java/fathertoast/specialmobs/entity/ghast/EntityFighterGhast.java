/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.ghast;

import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityFighterGhast extends Entity_SpecialGhast {

    private static final ResourceLocation[] TEXTURES = {
        new ResourceLocation(getTexturePath("fighter")),
        null,
        new ResourceLocation(getTexturePath("fighter_shooting"))
    };

    public EntityFighterGhast(World world) {
        super(world);
        setSize(2.0F, 2.0F);
        getSpecialData().setBaseScale(0.5F);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addUncommonDrop("uncommon", "Iron ingot", Items.IRON_INGOT);
    }

    @Override
    public ResourceLocation[] getDefaultTextures() {
        return TEXTURES;
    }

    /** Called to modify the mob's attributes based on the variant. */
    @Override
    protected void applyTypeAttributes() {
        experienceValue += 1;

        getSpecialData().addAttribute(SharedMonsterAttributes.MAX_HEALTH, 20.0);
        getSpecialData().addAttribute(SharedMonsterAttributes.ARMOR, 10.0);
        getSpecialData().addAttribute(SharedMonsterAttributes.ATTACK_DAMAGE, 2.0);
        getSpecialData().multAttribute(SharedMonsterAttributes.MOVEMENT_SPEED, 1.3);
    }

    @Override
    protected void initTypeAI() {
        disableRangedAI();
    }

    // Overridden to modify attack effects.
    @Override
    protected void onTypeAttack(Entity target) {
        if (target instanceof EntityLivingBase) {
            ((EntityLivingBase) target).setHealth(((EntityLivingBase) target).getHealth() - 2.0F);
        }
    }

}
