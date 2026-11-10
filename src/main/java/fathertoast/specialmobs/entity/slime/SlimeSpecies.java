/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.slime;

import fathertoast.specialmobs.bestiary.BestiaryInfo;
import fathertoast.specialmobs.bestiary.SpawnThemes;
import fathertoast.specialmobs.bestiary.SpeciesList;

/** Declares the species of the Slime family: its vanilla replacement, then its special variants. */
public final class SlimeSpecies {

    private SlimeSpecies() { }

    public static void declare(SpeciesList species) {
        species.base(Entity_SpecialSlime.class, Entity_SpecialSlime::new, 0x7EBF6E);
        species.variant("Blackberry", EntityBlackberrySlime.class, EntityBlackberrySlime::new,
            new BestiaryInfo(0x331133), EntityBlackberrySlime::buildLootTable
        );
        species.variant("Blueberry", EntityBlueberrySlime.class, EntityBlueberrySlime::new,
            new BestiaryInfo(0x766BBC).withWeightExceptions(SpawnThemes.WATER), EntityBlueberrySlime::buildLootTable
        );
        species.variant("Caramel", EntityCaramelSlime.class, EntityCaramelSlime::new, new BestiaryInfo(0x9D733F),
            EntityCaramelSlime::buildLootTable
        );
        species.variant("Grape", EntityGrapeSlime.class, EntityGrapeSlime::new,
            new BestiaryInfo(0xB333B3).withWeightExceptions(SpawnThemes.MOUNTAIN), EntityGrapeSlime::buildLootTable
        );
        species.variant("Lemon", EntityLemonSlime.class, EntityLemonSlime::new, new BestiaryInfo(0xE6E861),
            EntityLemonSlime::buildLootTable
        );
        species.variant("Strawberry", EntityStrawberrySlime.class, EntityStrawberrySlime::new,
            new BestiaryInfo(0xBE696B).withWeightExceptions(SpawnThemes.FIRE), EntityStrawberrySlime::buildLootTable
        );
        species.variant("Watermelon", EntityWatermelonSlime.class, EntityWatermelonSlime::new,
            new BestiaryInfo(0xDF7679).withWeight(BestiaryInfo.BASE_WEIGHT_UNCOMMON),
            EntityWatermelonSlime::buildLootTable
        );
    }

}
