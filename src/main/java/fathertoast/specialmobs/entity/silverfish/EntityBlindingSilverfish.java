/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.silverfish;

import fathertoast.specialmobs.entity.MobHelper;
import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityBlindingSilverfish extends Entity_SpecialSilverfish {

    private static final ResourceLocation[] TEXTURES = { new ResourceLocation(getTexturePath("blinding")) };

    public EntityBlindingSilverfish(World world) {
        super(world);
        getSpecialData().addPotionImmunity(MobEffects.BLINDNESS);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addCommonDrop("common", "Ink sac", new ItemStack(Items.DYE, 1, EnumDyeColor.BLACK.getDyeDamage()), 1);
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

    // Overridden to modify attack effects.
    @Override
    public void onTypeAttack(Entity target) {
        if (target instanceof EntityLivingBase) {
            int duration = MobHelper.getDebuffDuration(world.getDifficulty());

            ((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, duration, 0));
            ((EntityLivingBase) target).removePotionEffect(MobEffects.NIGHT_VISION);
        }
    }

}
