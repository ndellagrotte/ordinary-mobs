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
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityPlagueZombie extends Entity_SpecialZombie {

    private static final ResourceLocation[] TEXTURES = { new ResourceLocation(getTexturePath("plague")) };

    public EntityPlagueZombie(World world) {
        super(world);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addUncommonDrop("uncommon", "Gross stuff", Items.POISONOUS_POTATO, Items.SPIDER_EYE,
            Items.FERMENTED_SPIDER_EYE, Item.getItemFromBlock(Blocks.RED_MUSHROOM),
            Item.getItemFromBlock(Blocks.BROWN_MUSHROOM)
        );
    }

    @Override
    public ResourceLocation[] getDefaultTextures() {
        return TEXTURES;
    }

    /** Called to modify the mob's attributes based on the variant. */
    @Override
    protected void applyTypeAttributes() {
        experienceValue += 1;

        getSpecialData().addAttribute(SharedMonsterAttributes.MAX_HEALTH, 10.0F);
        getSpecialData().addAttribute(SharedMonsterAttributes.ARMOR, 10.0F);
    }

    // Overridden to modify attack effects.
    @Override
    protected void onTypeAttack(Entity target) {
        if (target instanceof EntityLivingBase) {
            ((EntityLivingBase) target).addPotionEffect(MobHelper.nextPlagueEffect(rand, world));
        }
    }

    @Override
    protected EntityArrow getArrow(float distanceFactor) {
        EntityArrow arrow = super.getArrow(distanceFactor);

        if (arrow instanceof EntityTippedArrow) {
            ((EntityTippedArrow) arrow).addEffect(MobHelper.nextPlagueEffect(rand, world));
        }

        return arrow;
    }

}
