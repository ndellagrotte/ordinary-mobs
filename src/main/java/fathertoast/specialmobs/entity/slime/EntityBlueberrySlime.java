/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.slime;

import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.block.material.Material;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityBlueberrySlime extends Entity_SpecialSlime {

    private static final ResourceLocation[] TEXTURES = { new ResourceLocation(getTexturePath("blueberry")) };

    public EntityBlueberrySlime(World world) {
        super(world);
        getSpecialData().setImmuneToBurning(true);
        getSpecialData().setCanBreatheInWater(true);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addRareDrop("rare", "Undersea loot", Items.GOLD_NUGGET, Items.PRISMARINE_SHARD, Items.PRISMARINE_CRYSTALS);

        loot.addUncommonDrop("uncommon", "Slime color", new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()));
    }

    @Override
    protected EntitySlime getSplitSlime() {
        return new EntityBlueberrySlime(world);
    }

    @Override
    public ResourceLocation[] getDefaultTextures() {
        return TEXTURES;
    }

    /** Called to modify the mob's attributes based on the variant. */
    @Override
    protected void applyTypeAttributes() {
        slimeExperienceValue += 1;
    }

    /** Called to modify the mob's attributes based on the variant. Health, damage, and speed must be modified here for slimes. */
    @Override
    protected void adjustTypeAttributesForSize(int size) {
        getSpecialData().addAttribute(SharedMonsterAttributes.ATTACK_DAMAGE, 1.0 * size);
    }

    @Override
    public boolean handleWaterMovement() {
        if (world.isMaterialInBB(getEntityBoundingBox().grow(0.0, -0.4, 0.0).shrink(0.001), Material.WATER)) {
            fallDistance = 0.0F;
            extinguish();
        }
        return inWater = false;
    }

}
