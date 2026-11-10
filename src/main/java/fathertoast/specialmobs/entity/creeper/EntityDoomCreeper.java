/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.creeper;

import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityDoomCreeper extends Entity_SpecialCreeper {

    private static final ResourceLocation[] TEXTURES = {
        new ResourceLocation(getTexturePath("doom")),
        null,
        new ResourceLocation(getTexturePath("doom_overlay"))
    };

    public EntityDoomCreeper(World world) {
        super(world);
        getSpecialData().addPotionImmunity(MobEffects.INSTANT_DAMAGE, MobEffects.WITHER);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addLootTable("common", "Witch loot", LootTableList.ENTITIES_WITCH);
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

    // The explosion caused by this creeper.
    @Override
    public void explodeByType(boolean powered, boolean griefing) {
        float power = (float) explosionRadius / 2.0F * (powered ? 2.0F : 1.0F);
        world.createExplosion(this, posX, posY, posZ, power, false);
    }

    // Return true here only if you are adding extra potion effects to potionCloudByType().
    @Override
    public boolean alwaysMakePotionCloud() {
        return true;
    }

    // The explosion caused by this creeper.
    @Override
    public void potionCloudByType(EntityAreaEffectCloud potionCloud, boolean powered) {
        potionCloud.setRadius((float) explosionRadius * (powered ? 2.0F : 1.0F));

        potionCloud.addEffect(new PotionEffect(MobEffects.INSTANT_DAMAGE, 100, 1));
        potionCloud.addEffect(new PotionEffect(MobEffects.WITHER, 200));
    }

}
