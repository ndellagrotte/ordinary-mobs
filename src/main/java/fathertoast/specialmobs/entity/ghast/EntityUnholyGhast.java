/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.ghast;

import fathertoast.specialmobs.entity.MobHelper;
import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityUnholyGhast extends Entity_SpecialGhast {

    private static final ResourceLocation[] TEXTURES = {
        new ResourceLocation(getTexturePath("unholy")),
        null,
        new ResourceLocation(getTexturePath("unholy_shooting"))
    };

    public EntityUnholyGhast(World world) {
        super(world);
        setSize(2.0F, 2.0F);
        getSpecialData().setBaseScale(0.5F);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addCommonDrop("common", "Bones", Items.BONE);
        loot.addSemicommonDrop("semicommon", "Quartz", Items.QUARTZ);
    }

    @Override
    public ResourceLocation[] getDefaultTextures() {
        return TEXTURES;
    }

    /** Called to modify the mob's attributes based on the variant. */
    @Override
    protected void applyTypeAttributes() {
        experienceValue += 4;

        getSpecialData().addAttribute(SharedMonsterAttributes.MAX_HEALTH, 10.0);
        getSpecialData().addAttribute(SharedMonsterAttributes.ATTACK_DAMAGE, 2.0);
    }

    @Override
    protected void initTypeAI() {
        disableRangedAI();
    }

    // Overridden to modify attack effects.
    @Override
    protected void onTypeAttack(Entity target) {
        if (target instanceof EntityLivingBase) {
            MobHelper.dealDirectDamage(this, (EntityLivingBase) target, 1.0F);
            heal(1.0F);
        }
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
    }

    // Called when the entity is attacked.
    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damage) {
        if (MobHelper.isDamageSourceIneffectiveAgainstVampires(damageSource)) {
            damage = Math.min(1.0F, damage);
        }
        return super.attackEntityFrom(damageSource, damage);
    }

}
