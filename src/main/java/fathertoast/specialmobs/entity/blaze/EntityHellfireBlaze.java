/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.blaze;

import fathertoast.specialmobs.entity.SpecialMobData;
import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityHellfireBlaze extends Entity_SpecialBlaze {

    private static final String TAG_EXPLOSION_POWER = "ExplosionPower";

    private static final ResourceLocation[] TEXTURES = { new ResourceLocation(getTexturePath("hellfire")) };

    // The base explosion strength of this blaze's fireballs.
    private int explosionPower = 2;

    public EntityHellfireBlaze(World world) {
        super(world);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addCommonDrop("common", "Gunpowder", Items.GUNPOWDER);
    }

    @Override
    public ResourceLocation[] getDefaultTextures() {
        return TEXTURES;
    }

    /** Called to modify the mob's attributes based on the variant. */
    @Override
    protected void applyTypeAttributes() {
        experienceValue += 2;

        getSpecialData().addAttribute(SharedMonsterAttributes.MAX_HEALTH, 10.0);
    }

    @Override
    protected void initTypeAI() {
        getSpecialData().rangedAttackSpread = 0.0F;
        setRangedAI(1, 0, 60, 100, 40.0F);
    }

    // Called to attack the target entity with a fireball.
    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        world.playEvent(null, 1018, new BlockPos(this), 0);

        double dX = target.posX - posX;
        double dY = target.getEntityBoundingBox().minY + target.height / 2.0F - (posY + height / 2.0F);
        double dZ = target.posZ - posZ;

        float accelVariance = MathHelper.sqrt(MathHelper.sqrt(getDistanceSq(target))) *
            getSpecialData().rangedAttackSpread / 28.0F;

        EntityLargeFireball fireball = new EntityLargeFireball(world, this, dX + rand.nextGaussian() * accelVariance,
            dY, dZ + rand.nextGaussian() * accelVariance
        );
        fireball.explosionPower = explosionPower;
        fireball.posY = posY + height / 2.0F + 0.5;
        world.spawnEntity(fireball);
    }

    // Saves this entity to NBT.
    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        NBTTagCompound saveTag = SpecialMobData.getSaveLocation(tag);

        saveTag.setInteger(TAG_EXPLOSION_POWER, explosionPower);
    }

    // Reads this entity from NBT.
    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        NBTTagCompound saveTag = SpecialMobData.getSaveLocation(tag);

        if (saveTag.hasKey(TAG_EXPLOSION_POWER, SpecialMobData.NBT_TYPE_PRIMITIVE)) {
            explosionPower = saveTag.getInteger(TAG_EXPLOSION_POWER);
        }
    }

}
