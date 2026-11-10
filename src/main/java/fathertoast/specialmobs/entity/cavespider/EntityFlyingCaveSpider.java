/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.cavespider;

import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityFlyingCaveSpider extends Entity_SpecialCaveSpider {

    private static final ResourceLocation[] TEXTURES = {
        new ResourceLocation(getTexturePath("flying")),
        new ResourceLocation(getTexturePath("flying_eyes"))
    };

    public EntityFlyingCaveSpider(World world) {
        super(world);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addCommonDrop("common", "Feathers", Items.FEATHER);
    }

    @Override
    public ResourceLocation[] getDefaultTextures() {
        return TEXTURES;
    }

    /** Called to modify the mob's attributes based on the variant. */
    @Override
    protected void applyTypeAttributes() {
        experienceValue += 2;

        getSpecialData().setFallDamageMultiplier(0.0F);

        getSpecialData().multAttribute(SharedMonsterAttributes.MOVEMENT_SPEED, 1.2);
    }

    // Called every tick while this entity is alive.
    @Override
    public void onLivingUpdate() {
        Entity target = getAttackTarget();
        if (target != null) {
            float distance = getDistance(target);
            if (distance > 6.0F && distance < 12.0F && onGround && rand.nextInt(10) == 0) {
                double vX = target.posX - posX;
                double vZ = target.posZ - posZ;
                double vH = Math.sqrt(vX * vX + vZ * vZ);
                motionX = vX / vH * 2.0 + motionX * 0.2;
                motionY = 0.4 * 2.0;
                motionZ = vZ / vH * 2.0 + motionZ * 0.2;
            }
        }
        super.onLivingUpdate();
    }

}
