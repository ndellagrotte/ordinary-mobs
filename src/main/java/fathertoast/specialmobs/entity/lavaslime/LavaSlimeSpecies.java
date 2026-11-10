/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.lavaslime;

import fathertoast.specialmobs.bestiary.BestiaryInfo;
import fathertoast.specialmobs.bestiary.SpawnThemes;
import fathertoast.specialmobs.bestiary.SpeciesList;

/** Declares the species of the LavaSlime family: its vanilla replacement, then its special variants. */
public final class LavaSlimeSpecies {

    private LavaSlimeSpecies() { }

    public static void declare(SpeciesList species) {
        species.base(Entity_SpecialLavaSlime.class, Entity_SpecialLavaSlime::new, 0xFCFC00);
        species.variant("Flying", EntityFlyingLavaSlime.class, EntityFlyingLavaSlime::new,
            new BestiaryInfo(0xB333B3).withWeightExceptions(SpawnThemes.MOUNTAIN), EntityFlyingLavaSlime::buildLootTable
        );
        species.variant("Hardened", EntityHardenedLavaSlime.class, EntityHardenedLavaSlime::new,
            new BestiaryInfo(0xDF7679).withWeight(BestiaryInfo.BASE_WEIGHT_UNCOMMON),
            EntityHardenedLavaSlime::buildLootTable
        );
        species.variant("Sticky", EntityStickyLavaSlime.class, EntityStickyLavaSlime::new, new BestiaryInfo(0x9D733F),
            EntityStickyLavaSlime::buildLootTable
        );
        species.variant("Volatile", EntityVolatileLavaSlime.class, EntityVolatileLavaSlime::new,
            new BestiaryInfo(0x331133), EntityVolatileLavaSlime::buildLootTable
        );
    }

}
