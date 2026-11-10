/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.creeper;

import fathertoast.specialmobs.bestiary.BestiaryInfo;
import fathertoast.specialmobs.bestiary.SpawnThemes;
import fathertoast.specialmobs.bestiary.SpeciesList;

/** Declares the species of the Creeper family: its vanilla replacement, then its special variants. */
public final class CreeperSpecies {

    private CreeperSpecies() { }

    public static void declare(SpeciesList species) {
        species.base(Entity_SpecialCreeper.class, Entity_SpecialCreeper::new, 0x000000);
        species.variant("Dark", EntityDarkCreeper.class, EntityDarkCreeper::new, new BestiaryInfo(0xF9FF3A),
            EntityDarkCreeper::buildLootTable
        );
        species.variant("Death", EntityDeathCreeper.class, EntityDeathCreeper::new, new BestiaryInfo(0xCD0000),
            EntityDeathCreeper::buildLootTable
        );
        species.variant("Dirt", EntityDirtCreeper.class, EntityDirtCreeper::new, new BestiaryInfo(0x78553B),
            EntityDirtCreeper::buildLootTable
        );
        species.variant("Doom", EntityDoomCreeper.class, EntityDoomCreeper::new,
            new BestiaryInfo(0x494949).withWeightExceptions(SpawnThemes.FOREST), EntityDoomCreeper::buildLootTable
        );
        species.variant("Drowning", EntityDrowningCreeper.class, EntityDrowningCreeper::new,
            new BestiaryInfo(0x2D41F4)
                .withWeight(BestiaryInfo.BASE_WEIGHT_UNCOMMON)
                .withWeightExceptions(SpawnThemes.WATER),
            EntityDrowningCreeper::buildLootTable
        );
        species.variant("Ender", EntityEnderCreeper.class, EntityEnderCreeper::new,
            new BestiaryInfo(0xCC00FA)
                .withWeight(BestiaryInfo.BASE_WEIGHT_UNCOMMON)
                .withWeightExceptions(SpawnThemes.dimension("the_end", BestiaryInfo.BASE_WEIGHT * 4)),
            EntityEnderCreeper::buildLootTable
        );
        species.variant("Fire", EntityFireCreeper.class, EntityFireCreeper::new,
            new BestiaryInfo(0xE13916).withWeightExceptions(SpawnThemes.FIRE), EntityFireCreeper::buildLootTable
        );
        species.variant("Gravel", EntityGravelCreeper.class, EntityGravelCreeper::new, new BestiaryInfo(0x908884),
            EntityGravelCreeper::buildLootTable
        );
        species.variant("Jumping", EntityJumpingCreeper.class, EntityJumpingCreeper::new,
            new BestiaryInfo(0x7D6097).withWeightExceptions(SpawnThemes.MOUNTAIN), EntityJumpingCreeper::buildLootTable
        );
        species.variant("Lightning", EntityLightningCreeper.class, EntityLightningCreeper::new,
            new BestiaryInfo(0x499CAE), EntityLightningCreeper::buildLootTable
        );
        species.variant("Mini", EntityMiniCreeper.class, EntityMiniCreeper::new,
            new BestiaryInfo(0xFFC0CB).withWeight(BestiaryInfo.BASE_WEIGHT_UNCOMMON), EntityMiniCreeper::buildLootTable
        );
        species.variant("Splitting", EntitySplittingCreeper.class, EntitySplittingCreeper::new,
            new BestiaryInfo(0x5F9D22).withWeight(BestiaryInfo.BASE_WEIGHT_UNCOMMON),
            EntitySplittingCreeper::buildLootTable
        );
    }

}
