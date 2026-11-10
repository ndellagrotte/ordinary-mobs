/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.creeper;

import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityFireCreeper extends Entity_SpecialCreeper {

    private static final ResourceLocation[] TEXTURES = { new ResourceLocation(getTexturePath("fire")) };

    public EntityFireCreeper(World world) {
        super(world);
        getSpecialData().setDamagedByWater(true);
        getSpecialData().setImmuneToFire(true);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addCommonDrop("common", "Fire charge", Items.FIRE_CHARGE);
        loot.addUncommonDrop("uncommon", "Coal", Items.COAL);
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

    @Override
    protected void initTypeAI() {
        setCanNotExplodeWhenWet(true);
    }

    // The explosion caused by this creeper.
    @Override
    public void explodeByType(boolean powered, boolean griefing) {
        float power = (float) explosionRadius * (powered ? 2.0F : 1.0F);
        world.newExplosion(this, posX, posY, posZ, power, true, griefing);
    }

}
