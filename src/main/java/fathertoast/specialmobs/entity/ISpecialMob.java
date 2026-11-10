/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity;

import fathertoast.specialmobs.bestiary.Species;

import net.minecraft.util.ResourceLocation;

/** Implemented by every mob from this mod. */
public interface ISpecialMob {

    /** @return This mob's special data. */
    SpecialMobData<?> getSpecialData();

    /** @return The species of this mob. */
    default Species getSpecies() {
        return getSpecialData().species();
    }

    /** @return The experience that should be dropped by the entity. */
    int getExperience();

    /** Sets the experience that should be dropped by the entity. */
    void setExperience(int xp);

    /** Sets the entity's immunity to fire status. */
    void setImmuneToFire(boolean immune);

    /** @return All the default textures for the entity: base, then optionally eyes, then optionally overlay. */
    ResourceLocation[] getDefaultTextures();

    /**
     * Applies this mob's variant-specific attribute changes again, on top of whatever its attributes are now.
     * <p>
     * Used when this mob takes the place of a vanilla mob and has just been given that mob's attributes, which
     * would otherwise leave it with none of its own changes.
     */
    void reapplyTypeAttributes();

}
