/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.cavespider;

import fathertoast.specialmobs.bestiary.BestiaryInfo;
import fathertoast.specialmobs.bestiary.SpawnThemes;
import fathertoast.specialmobs.bestiary.SpeciesList;

/** Declares the species of the CaveSpider family: its vanilla replacement, then its special variants. */
public final class CaveSpiderSpecies {

    private CaveSpiderSpecies() { }

    public static void declare(SpeciesList species) {
        species.base(Entity_SpecialCaveSpider.class, Entity_SpecialCaveSpider::new, 0xA80E0E);
        species.variant("Baby", EntityBabyCaveSpider.class, EntityBabyCaveSpider::new,
            new BestiaryInfo(0xFFC0CB).withWeight(0), EntityBabyCaveSpider::buildLootTable
        );
        species.variant("Flying", EntityFlyingCaveSpider.class, EntityFlyingCaveSpider::new,
            new BestiaryInfo(0x6388B2).withWeightExceptions(SpawnThemes.MOUNTAIN),
            EntityFlyingCaveSpider::buildLootTable
        );
        species.variant("Mother", EntityMotherCaveSpider.class, EntityMotherCaveSpider::new, new BestiaryInfo(0xB300B3),
            EntityMotherCaveSpider::buildLootTable
        );
        species.variant("Web", EntityWebCaveSpider.class, EntityWebCaveSpider::new,
            new BestiaryInfo(0xE7E7E7)
                .withWeight(BestiaryInfo.BASE_WEIGHT_UNCOMMON)
                .withWeightExceptions(SpawnThemes.FOREST),
            EntityWebCaveSpider::buildLootTable
        );
        species.variant("Witch", EntityWitchCaveSpider.class, EntityWitchCaveSpider::new,
            new BestiaryInfo(0xDD0E0E).withWeight(BestiaryInfo.BASE_WEIGHT_UNCOMMON),
            EntityWitchCaveSpider::buildLootTable
        );
    }

}
