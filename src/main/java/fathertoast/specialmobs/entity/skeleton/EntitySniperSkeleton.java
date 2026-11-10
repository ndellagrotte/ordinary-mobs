/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.skeleton;

import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntitySniperSkeleton extends Entity_SpecialSkeleton {

    private static final ResourceLocation[] TEXTURES = {
        new ResourceLocation(getTexturePath("sniper")),
        null,
        new ResourceLocation(getTexturePath("sniper_overlay"))
    };

    public EntitySniperSkeleton(World world) {
        super(world);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addGuaranteedDrop("base", "Arrows", Items.ARROW, 1);
        loot.addClusterDrop("uncommon", "Spectral arrows", Items.SPECTRAL_ARROW, 4);
    }

    @Override
    public ResourceLocation[] getDefaultTextures() {
        return TEXTURES;
    }

    /** Called to modify the mob's attributes based on the variant. */
    @Override
    protected void applyTypeAttributes() {
        experienceValue += 1;

        getSpecialData().addAttribute(SharedMonsterAttributes.FOLLOW_RANGE, 16.0F);
        getSpecialData().addAttribute(SharedMonsterAttributes.ATTACK_DAMAGE, 2.0F);
    }

    @Override
    protected void initTypeAI() {
        getSpecialData().rangedAttackDamage += 2.0F;
        getSpecialData().rangedAttackSpread *= 0.05F;
        setRangedAI(0.3, 30, 25.0F);
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        super.setEquipmentBasedOnDifficulty(difficulty);

        if (!(getHeldItemMainhand().getItem() instanceof ItemBow)) {
            setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        }
    }

}
