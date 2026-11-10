/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.spider;

import fathertoast.specialmobs.bestiary.BestiaryInfo;
import fathertoast.specialmobs.bestiary.SpawnThemes;
import fathertoast.specialmobs.bestiary.SpeciesList;

/** Declares the species of the Spider family: its vanilla replacement, then its special variants. */
public final class SpiderSpecies {

    private SpiderSpecies() { }

    public static void declare(SpeciesList species) {
        species.base(Entity_SpecialSpider.class, Entity_SpecialSpider::new, 0xA80E0E);
        species.variant("Baby", EntityBabySpider.class, EntityBabySpider::new, new BestiaryInfo(0xFFC0CB).withWeight(0),
            EntityBabySpider::buildLootTable
        );
        species.variant("Desert", EntityDesertSpider.class, EntityDesertSpider::new,
            new BestiaryInfo(0xE6DDAC).withWeightExceptions(SpawnThemes.biomePrefix("desert",
                    BestiaryInfo.BASE_WEIGHT_COMMON
                ),
                SpawnThemes.biomePrefix("savanna", BestiaryInfo.BASE_WEIGHT_COMMON),
                SpawnThemes.biomePrefix("mesa", BestiaryInfo.BASE_WEIGHT_COMMON)
            ),
            EntityDesertSpider::buildLootTable
        );
        species.variant("Flying", EntityFlyingSpider.class, EntityFlyingSpider::new,
            new BestiaryInfo(0x6388B2).withWeightExceptions(SpawnThemes.MOUNTAIN), EntityFlyingSpider::buildLootTable
        );
        species.variant("Giant", EntityGiantSpider.class, EntityGiantSpider::new, new BestiaryInfo(0xA80E0E),
            EntityGiantSpider::buildLootTable
        );
        species.variant("Hungry", EntityHungrySpider.class, EntityHungrySpider::new, new BestiaryInfo(0x799C65),
            EntityHungrySpider::buildLootTable
        );
        species.variant("Mother", EntityMotherSpider.class, EntityMotherSpider::new, new BestiaryInfo(0xB300B3),
            EntityMotherSpider::buildLootTable
        );
        species.variant("Pale", EntityPaleSpider.class, EntityPaleSpider::new,
            new BestiaryInfo(0xDED4C6).withWeightExceptions(SpawnThemes.ICE), EntityPaleSpider::buildLootTable
        );
        species.variant("Poison", EntityPoisonSpider.class, EntityPoisonSpider::new,
            new BestiaryInfo(0x0C424E).withWeightExceptions(SpawnThemes.FOREST), EntityPoisonSpider::buildLootTable
        );
        species.variant("Web", EntityWebSpider.class, EntityWebSpider::new,
            new BestiaryInfo(0xE7E7E7)
                .withWeight(BestiaryInfo.BASE_WEIGHT_UNCOMMON)
                .withWeightExceptions(SpawnThemes.FOREST),
            EntityWebSpider::buildLootTable
        );
        species.variant("Witch", EntityWitchSpider.class, EntityWitchSpider::new,
            new BestiaryInfo(0xDD0E0E).withWeight(BestiaryInfo.BASE_WEIGHT_UNCOMMON), EntityWitchSpider::buildLootTable
        );
    }

}
