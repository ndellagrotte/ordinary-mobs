/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.enderman;

import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityLightningEnderman extends Entity_SpecialEnderman {

    private static final ResourceLocation[] TEXTURES = {
        new ResourceLocation(getTexturePath("lightning")),
        new ResourceLocation(getTexturePath("lightning_eyes"))
    };

    public EntityLightningEnderman(World world) {
        super(world);
        getSpecialData().setImmuneToFire(true);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addCommonDrop("common", "Redstone dust", Items.REDSTONE);
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

    // Overridden to modify attack effects.
    @Override
    public void onTypeAttack(Entity target) {
        world.addWeatherEffect(new EntityLightningBolt(world, target.posX, target.posY, target.posZ, false));
        for (int i = 64; i-- > 0;) {
            if (teleportRandomly()) {
                break;
            }
        }
    }

}
