/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.witch;

import fathertoast.specialmobs.bestiary.BestiaryInfo;
import fathertoast.specialmobs.bestiary.SpawnThemes;
import fathertoast.specialmobs.bestiary.SpeciesList;

/** Declares the species of the Witch family: its vanilla replacement, then its special variants. */
public final class WitchSpecies {

    private WitchSpecies() { }

    public static void declare(SpeciesList species) {
        species.base(Entity_SpecialWitch.class, Entity_SpecialWitch::new, 0x51A03E);
        species.variant("Domination", EntityDominationWitch.class, EntityDominationWitch::new,
            new BestiaryInfo(0xFFF87E).withWeight(BestiaryInfo.BASE_WEIGHT_UNCOMMON),
            EntityDominationWitch::buildLootTable
        );
        species.variant("Shadows", EntityShadowsWitch.class, EntityShadowsWitch::new,
            new BestiaryInfo(0x000000).withWeightExceptions(SpawnThemes.FOREST), EntityShadowsWitch::buildLootTable
        );
        species.variant("Undead", EntityUndeadWitch.class, EntityUndeadWitch::new, new BestiaryInfo(0x799C65),
            EntityUndeadWitch::buildLootTable
        );
        species.variant("Wilds", EntityWildsWitch.class, EntityWildsWitch::new,
            new BestiaryInfo(0xA80E0E).withWeightExceptions(SpawnThemes.FOREST), EntityWildsWitch::buildLootTable
        );
        species.variant("Wind", EntityWindWitch.class, EntityWindWitch::new,
            new BestiaryInfo(0x6388B2).withWeightExceptions(SpawnThemes.MOUNTAIN), EntityWindWitch::buildLootTable
        );
    }

}
