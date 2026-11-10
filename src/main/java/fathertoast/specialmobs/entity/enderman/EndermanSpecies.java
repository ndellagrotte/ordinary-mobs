/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.enderman;

import fathertoast.specialmobs.bestiary.BestiaryInfo;
import fathertoast.specialmobs.bestiary.SpawnThemes;
import fathertoast.specialmobs.bestiary.SpeciesList;

/** Declares the species of the Enderman family: its vanilla replacement, then its special variants. */
public final class EndermanSpecies {

    private EndermanSpecies() { }

    public static void declare(SpeciesList species) {
        species.base(Entity_SpecialEnderman.class, Entity_SpecialEnderman::new, 0x000000);
        species.variant("Blinding", EntityBlindingEnderman.class, EntityBlindingEnderman::new,
            new BestiaryInfo(0xFFFFFF).withWeightExceptions(SpawnThemes.FOREST), EntityBlindingEnderman::buildLootTable
        );
        species.variant("Icy", EntityIcyEnderman.class, EntityIcyEnderman::new,
            new BestiaryInfo(0x72959C).withWeightExceptions(SpawnThemes.ICE), EntityIcyEnderman::buildLootTable
        );
        species.variant("Lightning", EntityLightningEnderman.class, EntityLightningEnderman::new,
            new BestiaryInfo(0x4BB4B5), EntityLightningEnderman::buildLootTable
        );
        species.variant("Mini", EntityMiniEnderman.class, EntityMiniEnderman::new,
            new BestiaryInfo(0xFFC0CB).withWeight(BestiaryInfo.BASE_WEIGHT_UNCOMMON), EntityMiniEnderman::buildLootTable
        );
        species.variant("Mirage", EntityMirageEnderman.class, EntityMirageEnderman::new,
            new BestiaryInfo(0xC2BC84)
                .withWeight(BestiaryInfo.BASE_WEIGHT_UNCOMMON)
                .withWeightExceptions(SpawnThemes.biomePrefix("desert", BestiaryInfo.BASE_WEIGHT_COMMON),
                    SpawnThemes.biomePrefix("savanna", BestiaryInfo.BASE_WEIGHT_COMMON),
                    SpawnThemes.biomePrefix("mesa", BestiaryInfo.BASE_WEIGHT_COMMON),
                    SpawnThemes.biome("beaches", BestiaryInfo.BASE_WEIGHT_COMMON)
                ),
            EntityMirageEnderman::buildLootTable
        );
        species.variant("Thief", EntityThiefEnderman.class, EntityThiefEnderman::new,
            new BestiaryInfo(0x04FA00).withWeight(BestiaryInfo.BASE_WEIGHT_UNCOMMON),
            EntityThiefEnderman::buildLootTable
        );
    }

}
