/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.slime;

import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityGrapeSlime extends Entity_SpecialSlime {

    private static final ResourceLocation[] TEXTURES = { new ResourceLocation(getTexturePath("grape")) };

    public EntityGrapeSlime(World world) {
        super(world);
        getSpecialData().setFallDamageMultiplier(0.0F);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addGuaranteedDrop("base", "Slime balls", Items.SLIME_BALL, 1);

        loot.addUncommonDrop("uncommon", "Slime color", new ItemStack(Items.DYE, 1, EnumDyeColor.PURPLE.getDyeDamage())
        );
    }

    @Override
    protected EntitySlime getSplitSlime() {
        return new EntityGrapeSlime(world);
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
        getSpecialData().addAttribute(SharedMonsterAttributes.MAX_HEALTH, 4.0 * size);
        getSpecialData().multAttribute(SharedMonsterAttributes.MOVEMENT_SPEED, 1.2);
    }

    @Override
    protected void jump() {
        EntityLivingBase target = getAttackTarget();
        if (target != null) {
            double distanceSq = getDistanceSq(target);
            if (distanceSq > 36.0 && distanceSq < 144.0) {
                double vX = target.posX - posX;
                double vZ = target.posZ - posZ;
                double vH = Math.sqrt(vX * vX + vZ * vZ);
                motionX = vX / vH * 1.1 + motionX * 0.2;
                motionY = 0.42 * 2.6;
                motionZ = vZ / vH * 1.1 + motionZ * 0.2;
                isAirBorne = true;
                return;
            }
        }
        super.jump();
    }

}
