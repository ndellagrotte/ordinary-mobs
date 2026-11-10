/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.zombie;

import fathertoast.specialmobs.bestiary.BestiaryInfo;
import fathertoast.specialmobs.bestiary.SpawnThemes;
import fathertoast.specialmobs.bestiary.SpeciesList;

/** Declares the species of the Zombie family: its vanilla replacement, then its special variants. */
public final class ZombieSpecies {

    private ZombieSpecies() { }

    public static void declare(SpeciesList species) {
        species.base(Entity_SpecialZombie.class, Entity_SpecialZombie::new, 0x799C65);
        species.variant("Brute", EntityBruteZombie.class, EntityBruteZombie::new, new BestiaryInfo(0xFFF87E),
            EntityBruteZombie::buildLootTable
        );
        species.variant("Fire", EntityFireZombie.class, EntityFireZombie::new,
            new BestiaryInfo(0xDC1A00).withWeightExceptions(SpawnThemes.FIRE), EntityFireZombie::buildLootTable
        );
        species.variant("Fishing", EntityFishingZombie.class, EntityFishingZombie::new,
            new BestiaryInfo(0x2D41F4).withWeightExceptions(SpawnThemes.FISHING), EntityFishingZombie::buildLootTable
        );
        species.variant("Giant", EntityGiantZombie.class, EntityGiantZombie::new, new BestiaryInfo(0x799C65),
            EntityGiantZombie::buildLootTable
        );
        species.variant("Hungry", EntityHungryZombie.class, EntityHungryZombie::new, new BestiaryInfo(0xAB1518),
            EntityHungryZombie::buildLootTable
        );
        species.variant("Husk", EntityHuskZombie.class, EntityHuskZombie::new,
            new BestiaryInfo(0xE6CC94)
                .withWeight(BestiaryInfo.BASE_WEIGHT_UNCOMMON)
                .withWeightExceptions(SpawnThemes.FIRE),
            EntityHuskZombie::buildLootTable
        );
        species.variant("Plague", EntityPlagueZombie.class, EntityPlagueZombie::new,
            new BestiaryInfo(0x8AA838).withWeightExceptions(SpawnThemes.FOREST), EntityPlagueZombie::buildLootTable
        );
    }

}
