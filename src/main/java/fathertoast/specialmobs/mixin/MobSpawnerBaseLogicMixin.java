/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.mixin;

import fathertoast.specialmobs.spawn.SpawnInitInterceptor;
import org.jspecify.annotations.Nullable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.world.DifficultyInstance;

/**
 * Skips the spawn initialization of mobs from mob spawners that are about to be replaced, see
 * {@link SpawnInitInterceptor}. This is an optimization and not required: if another mod changes mob spawners so
 * that this does not apply, replaced mobs are simply initialized twice.
 */
@Mixin(MobSpawnerBaseLogic.class)
public abstract class MobSpawnerBaseLogicMixin {

    @WrapOperation(method = "updateSpawner",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/EntityLiving;onInitialSpawn(Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/entity/IEntityLivingData;)Lnet/minecraft/entity/IEntityLivingData;"
        ),
        require = 0
    )
    private @Nullable
    IEntityLivingData specialmobs$deferSpawnInit(EntityLiving entity, DifficultyInstance difficulty,
        @Nullable IEntityLivingData groupData, Operation<IEntityLivingData> original
    ) {
        if (SpawnInitInterceptor.shouldDeferInit(entity)) {
            return groupData;
        }
        return original.call(entity, difficulty, groupData);
    }

}
