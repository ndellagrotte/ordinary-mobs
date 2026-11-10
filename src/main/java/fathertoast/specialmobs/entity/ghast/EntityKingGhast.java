/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.ghast;

import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityKingGhast extends Entity_SpecialGhast {

    private static final ResourceLocation[] TEXTURES = {
        new ResourceLocation(getTexturePath("king")),
        null,
        new ResourceLocation(getTexturePath("king_shooting"))
    };

    public EntityKingGhast(World world) {
        super(world);
        setSize(6.0F, 6.0F);
        getSpecialData().setBaseScale(1.5F);

        getSpecialData().setRegenerationTime(30);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addSemicommonDrop("semicommon", "Gold ingot", Items.GOLD_INGOT);
        loot.addUncommonDrop("uncommon", "Emerald", Items.EMERALD);
    }

    @Override
    public ResourceLocation[] getDefaultTextures() {
        return TEXTURES;
    }

    /** Called to modify the mob's attributes based on the variant. */
    @Override
    protected void applyTypeAttributes() {
        experienceValue += 4;

        getSpecialData().addAttribute(SharedMonsterAttributes.MAX_HEALTH, 20.0);
        getSpecialData().addAttribute(SharedMonsterAttributes.ARMOR, 10.0);
        getSpecialData().addAttribute(SharedMonsterAttributes.ATTACK_DAMAGE, 4.0);
        getSpecialData().multAttribute(SharedMonsterAttributes.MOVEMENT_SPEED, 0.6);
    }

    @Override
    public int getFireballStrength() {
        return Math.round(super.getFireballStrength() * 2.5F);
    }

}
