/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.monster.EntityWitch;

/** Exposes the vanilla witch's potion timer so special witches can keep the vanilla drinking logic from running. */
@Mixin(EntityWitch.class)
public interface EntityWitchAccessor {

    @Accessor("potionUseTimer")
    void specialmobs$setPotionUseTimer(int ticks);

}
