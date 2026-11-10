/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.pigzombie;

import fathertoast.specialmobs.bestiary.BestiaryInfo;
import fathertoast.specialmobs.bestiary.SpawnThemes;
import fathertoast.specialmobs.bestiary.SpeciesList;

/** Declares the species of the PigZombie family: its vanilla replacement, then its special variants. */
public final class PigZombieSpecies {

    private PigZombieSpecies() { }

    public static void declare(SpeciesList species) {
        species.base(Entity_SpecialPigZombie.class, Entity_SpecialPigZombie::new, 0x4C7129);
        species.variant("Brute", EntityBrutePigZombie.class, EntityBrutePigZombie::new, new BestiaryInfo(0xFFF87E),
            EntityBrutePigZombie::buildLootTable
        );
        species.variant("Fishing", EntityFishingPigZombie.class, EntityFishingPigZombie::new,
            new BestiaryInfo(0x2D41F4).withWeightExceptions(SpawnThemes.FISHING), EntityFishingPigZombie::buildLootTable
        );
        species.variant("Giant", EntityGiantPigZombie.class, EntityGiantPigZombie::new, new BestiaryInfo(0x4C7129),
            EntityGiantPigZombie::buildLootTable
        );
        species.variant("Hungry", EntityHungryPigZombie.class, EntityHungryPigZombie::new, new BestiaryInfo(0xAB1518),
            EntityHungryPigZombie::buildLootTable
        );
        species.variant("Knight", EntityKnightPigZombie.class, EntityKnightPigZombie::new, new BestiaryInfo(0xFDEF28),
            EntityKnightPigZombie::buildLootTable
        );
        species.variant("Plague", EntityPlaguePigZombie.class, EntityPlaguePigZombie::new,
            new BestiaryInfo(0x8AA838).withWeightExceptions(SpawnThemes.FOREST), EntityPlaguePigZombie::buildLootTable
        );
        species.variant("Vampire", EntityVampirePigZombie.class, EntityVampirePigZombie::new,
            new BestiaryInfo(0x000000).withWeight(BestiaryInfo.BASE_WEIGHT_UNCOMMON),
            EntityVampirePigZombie::buildLootTable
        );
    }

}
