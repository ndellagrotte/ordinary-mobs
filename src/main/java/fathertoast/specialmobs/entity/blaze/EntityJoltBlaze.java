/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.blaze;

import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class EntityJoltBlaze extends Entity_SpecialBlaze {

    private static final ResourceLocation[] TEXTURES = { new ResourceLocation(getTexturePath("jolt")) };

    public EntityJoltBlaze(World world) {
        super(world);
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

        getSpecialData().addAttribute(SharedMonsterAttributes.MAX_HEALTH, 10.0);
        getSpecialData().addAttribute(SharedMonsterAttributes.ARMOR, 10.0);
        getSpecialData().multAttribute(SharedMonsterAttributes.MOVEMENT_SPEED, 1.3);
    }

    @Override
    protected void initTypeAI() {
        disableRangedAI();
    }

    // Called every tick while this entity is alive.
    @Override
    public void onLivingUpdate() {
        if (!world.isRemote &&
            isEntityAlive() &&
            getAttackTarget() != null &&
            rand.nextInt(20) == 0 &&
            getAttackTarget().getDistanceSq(this) > 256.0) {
            for (int i = 0; i < 16; i++) {
                if (teleportToEntity(getAttackTarget())) {
                    world.addWeatherEffect(new EntityLightningBolt(world, posX, posY, posZ, false));
                    break;
                }
            }
        }
        super.onLivingUpdate();
    }

    // Damages this entity from the damageSource by the given amount. Returns true if this entity is damaged.
    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damage) {
        if (!world.isRemote && !DamageSource.DROWN.equals(damageSource)) {
            double xI = posX;
            double yI = posY;
            double zI = posZ;

            for (int i = 0; i < 64; i++) {
                if (teleportRandomly()) {
                    if (damageSource instanceof EntityDamageSourceIndirect) {
                        return true;
                    }
                    boolean hit = super.attackEntityFrom(damageSource, damage);

                    if (getHealth() > 0.0F) {
                        world.addWeatherEffect(new EntityLightningBolt(world, xI, yI, zI, false));
                    } else {
                        setPosition(xI, yI, zI);
                    }
                    return hit;
                }
            }
        }
        return super.attackEntityFrom(damageSource, damage);
    }

    @Override
    public void onStruckByLightning(EntityLightningBolt lightningBolt) { }

    // Used by the enderman-like AI.
    private boolean teleportRandomly() {
        double targetX = posX + (rand.nextDouble() - 0.5) * 16.0;
        double targetY = posY + (double) (rand.nextInt(12) - 4);
        double targetZ = posZ + (rand.nextDouble() - 0.5) * 16.0;
        return teleportTo(targetX, targetY, targetZ);
    }

    // Used by the enderman-like AI.
    private boolean teleportToEntity(Entity entity) {
        Vec3d vec3d = new Vec3d(posX - entity.posX,
            getEntityBoundingBox().minY + (double) (height / 2.0F) - entity.posY + (double) entity.getEyeHeight(),
            posZ - entity.posZ
        );
        vec3d = vec3d.normalize();
        double targetX = posX + (rand.nextDouble() - 0.5) * 8.0 - vec3d.x * 16.0;
        double targetY = posY + (double) (rand.nextInt(8) - 2) - vec3d.y * 16.0;
        double targetZ = posZ + (rand.nextDouble() - 0.5) * 8.0 - vec3d.z * 16.0;
        return teleportTo(targetX, targetY, targetZ);
    }

    // Used by the enderman-like AI.
    private boolean teleportTo(double x, double y, double z) {
        EnderTeleportEvent event = new EnderTeleportEvent(this, x, y, z, 0);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            return false;
        }
        return attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ());
    }

}
