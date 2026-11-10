/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.enderman;

import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class EntityThiefEnderman extends Entity_SpecialEnderman {

    private static final ResourceLocation[] TEXTURES = {
        new ResourceLocation(getTexturePath("thief")),
        new ResourceLocation(getTexturePath("thief_eyes"))
    };

    private int teleportTargetDelay;

    public EntityThiefEnderman(World world) {
        super(world);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addGuaranteedDrop("base", "Ender pearl", Items.ENDER_PEARL, 1);
    }

    @Override
    public ResourceLocation[] getDefaultTextures() {
        return TEXTURES;
    }

    /** Called to modify the mob's attributes based on the variant. */
    @Override
    protected void applyTypeAttributes() {
        experienceValue += 2;

        getSpecialData().multAttribute(SharedMonsterAttributes.MOVEMENT_SPEED, 1.2);
    }

    @Override
    public void onLivingUpdate() {
        if (!world.isRemote) {
            teleportTargetDelay--;
        }
        super.onLivingUpdate();
    }

    // Overridden to modify attack effects.
    @Override
    public void onTypeAttack(Entity target) {
        if (!world.isRemote && target instanceof EntityLivingBase && teleportTargetDelay <= 0) {
            for (int i = 64; i-- > 0;) {
                if (teleportTargetRandomly((EntityLivingBase) target)) {
                    teleportTargetDelay = 120;

                    for (int j = 16; j-- > 0;) {
                        if (teleportToEntity(target)) {
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    private boolean teleportTargetRandomly(EntityLivingBase target) {
        double x = target.posX + (rand.nextDouble() - 0.5) * 32.0;
        double y = target.posY + (double) (rand.nextInt(32) - 16);
        double z = target.posZ + (rand.nextDouble() - 0.5) * 32.0;

        EnderTeleportEvent event = new EnderTeleportEvent(target, x, y, z, 0);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            return false;
        }
        boolean success = target.attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ());

        if (success) {
            world.playSound(null, target.prevPosX, target.prevPosY, target.prevPosZ,
                SoundEvents.ENTITY_ENDERMEN_TELEPORT, getSoundCategory(), 1.0F, 1.0F
            );
            target.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
        }

        return success;
    }

}
