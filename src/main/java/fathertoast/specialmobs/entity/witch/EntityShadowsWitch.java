/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.witch;

import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Collection;

public class EntityShadowsWitch extends Entity_SpecialWitch {

    private static final ResourceLocation[] TEXTURES = { new ResourceLocation(getTexturePath("shadows")) };

    private static final Collection<PotionEffect> POTION_SHADOWS = Arrays.asList(new PotionEffect(MobEffects.BLINDNESS,
            300, 0
        ),
        new PotionEffect(MobEffects.WITHER, 200, 0)
    );

    public EntityShadowsWitch(World world) {
        super(world);
        getSpecialData().addPotionImmunity(MobEffects.BLINDNESS, MobEffects.WITHER);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addCommonDrop("common", "Ink sac", new ItemStack(Items.DYE, 1, EnumDyeColor.BLACK.getDyeDamage()), 1);

        ItemStack potion = PotionUtils.appendEffects(new ItemStack(Items.POTIONITEM),
            Arrays.asList(new PotionEffect(MobEffects.BLINDNESS, 400), new PotionEffect(MobEffects.NIGHT_VISION, 300))
        );
        loot.addRareDrop("rare", "Shadows potion", potion);
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

    // Overridden to modify potion attacks. Return ItemStack.EMPTY to cancel the potion throw.
    protected ItemStack pickThrownPotionByType(ItemStack potion, EntityLivingBase target, float distanceFactor,
        float distance
    ) {
        if (target.getHealth() >= 4.0F && !target.isPotionActive(MobEffects.BLINDNESS)) {
            potion = makeSplashPotion(POTION_SHADOWS);
        }

        return potion;
    }

    // Called every tick while this entity is alive.
    @Override
    public void onLivingUpdate() {
        EntityLivingBase target = getAttackTarget();
        if (!world.isRemote &&
            isEntityAlive() &&
            target != null &&
            rand.nextInt(10) == 0 &&
            target.isPotionActive(MobEffects.BLINDNESS)) {
            target.removePotionEffect(MobEffects.NIGHT_VISION);
        }
        super.onLivingUpdate();
    }

}
