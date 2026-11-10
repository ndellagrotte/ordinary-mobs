/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.network.datasync.DataParameter;

/** Exposes the vanilla creeper's "powered" data parameter so special creepers can read and set it. */
@Mixin(EntityCreeper.class)
public interface EntityCreeperAccessor {

    @Accessor("POWERED")
    static DataParameter<Boolean> specialmobs$getPowered() {
        throw new AssertionError();
    }

}
