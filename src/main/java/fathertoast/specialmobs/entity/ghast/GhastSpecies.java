/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.ghast;

import fathertoast.specialmobs.bestiary.BestiaryInfo;
import fathertoast.specialmobs.bestiary.SpeciesList;

/** Declares the species of the Ghast family: its vanilla replacement, then its special variants. */
public final class GhastSpecies {

    private GhastSpecies() { }

    public static void declare(SpeciesList species) {
        species.base(Entity_SpecialGhast.class, Entity_SpecialGhast::new, 0xBCBCBC);
        species.variant("Baby", EntityBabyGhast.class, EntityBabyGhast::new, new BestiaryInfo(0xFFC0CB).withWeight(0),
            EntityBabyGhast::buildLootTable
        );
        species.variant("Fighter", EntityFighterGhast.class, EntityFighterGhast::new, new BestiaryInfo(0x7A1300),
            EntityFighterGhast::buildLootTable
        );
        species.variant("King", EntityKingGhast.class, EntityKingGhast::new,
            new BestiaryInfo(0xE8C51A).withWeight(BestiaryInfo.BASE_WEIGHT_UNCOMMON), EntityKingGhast::buildLootTable
        );
        species.variant("Queen", EntityQueenGhast.class, EntityQueenGhast::new, new BestiaryInfo(0xCE0AFF),
            EntityQueenGhast::buildLootTable
        );
        species.variant("Unholy", EntityUnholyGhast.class, EntityUnholyGhast::new,
            new BestiaryInfo(0x7AC754).withWeight(BestiaryInfo.BASE_WEIGHT_UNCOMMON), EntityUnholyGhast::buildLootTable
        );
    }

}
