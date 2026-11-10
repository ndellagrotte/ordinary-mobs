/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.slime;

import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityStrawberrySlime extends Entity_SpecialSlime {

    private static final ResourceLocation[] TEXTURES = { new ResourceLocation(getTexturePath("strawberry")) };

    public EntityStrawberrySlime(World world) {
        super(world);
        getSpecialData().setImmuneToFire(true);
        getSpecialData().setDamagedByWater(true);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addCommonDrop("common", "Fire charges", Items.FIRE_CHARGE, 1);

        loot.addUncommonDrop("uncommon", "Slime color", new ItemStack(Items.DYE, 1, EnumDyeColor.RED.getDyeDamage()));
    }

    @Override
    protected EntitySlime getSplitSlime() {
        return new EntityStrawberrySlime(world);
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

    // Overridden to modify attack effects.
    @Override
    protected void onTypeAttack(Entity target) {
        target.setFire(getSlimeSize() * 3);
    }

}
