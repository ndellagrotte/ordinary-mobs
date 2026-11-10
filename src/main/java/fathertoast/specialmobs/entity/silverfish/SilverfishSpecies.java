/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.silverfish;

import fathertoast.specialmobs.bestiary.BestiaryInfo;
import fathertoast.specialmobs.bestiary.SpawnThemes;
import fathertoast.specialmobs.bestiary.SpeciesList;

/** Declares the species of the Silverfish family: its vanilla replacement, then its special variants. */
public final class SilverfishSpecies {

    private SilverfishSpecies() { }

    public static void declare(SpeciesList species) {
        species.base(Entity_SpecialSilverfish.class, Entity_SpecialSilverfish::new, 0x303030);
        species.variant("Blinding", EntityBlindingSilverfish.class, EntityBlindingSilverfish::new,
            new BestiaryInfo(0x000000).withWeightExceptions(SpawnThemes.FOREST),
            EntityBlindingSilverfish::buildLootTable
        );
        species.variant("Fishing", EntityFishingSilverfish.class, EntityFishingSilverfish::new,
            new BestiaryInfo(0x2D41F4).withWeightExceptions(SpawnThemes.FISHING),
            EntityFishingSilverfish::buildLootTable
        );
        species.variant("Flying", EntityFlyingSilverfish.class, EntityFlyingSilverfish::new,
            new BestiaryInfo(0x6388B2).withWeightExceptions(SpawnThemes.MOUNTAIN),
            EntityFlyingSilverfish::buildLootTable
        );
        species.variant("Poison", EntityPoisonSilverfish.class, EntityPoisonSilverfish::new,
            new BestiaryInfo(0x779C68).withWeightExceptions(SpawnThemes.FOREST), EntityPoisonSilverfish::buildLootTable
        );
        species.variant("Tough", EntityToughSilverfish.class, EntityToughSilverfish::new,
            new BestiaryInfo(0xDD0E0E).withWeight(BestiaryInfo.BASE_WEIGHT_UNCOMMON),
            EntityToughSilverfish::buildLootTable
        );
    }

}
