/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.blaze;

import fathertoast.specialmobs.bestiary.BestiaryInfo;
import fathertoast.specialmobs.bestiary.SpeciesList;

/** Declares the species of the Blaze family: its vanilla replacement, then its special variants. */
public final class BlazeSpecies {

    private BlazeSpecies() { }

    public static void declare(SpeciesList species) {
        species.base(Entity_SpecialBlaze.class, Entity_SpecialBlaze::new, 0xFFF87E);
        species.variant("Cinder", EntityCinderBlaze.class, EntityCinderBlaze::new,
            new BestiaryInfo(0xFFC0CB).withWeight(0), EntityCinderBlaze::buildLootTable
        );
        species.variant("Conflagration", EntityConflagrationBlaze.class, EntityConflagrationBlaze::new,
            new BestiaryInfo(0xFFF87E), EntityConflagrationBlaze::buildLootTable
        );
        species.variant("Ember", EntityEmberBlaze.class, EntityEmberBlaze::new, new BestiaryInfo(0x000000),
            EntityEmberBlaze::buildLootTable
        );
        species.variant("Hellfire", EntityHellfireBlaze.class, EntityHellfireBlaze::new, new BestiaryInfo(0xDDDDDD),
            EntityHellfireBlaze::buildLootTable
        );
        species.variant("Inferno", EntityInfernoBlaze.class, EntityInfernoBlaze::new, new BestiaryInfo(0xF14F00),
            EntityInfernoBlaze::buildLootTable
        );
        species.variant("Jolt", EntityJoltBlaze.class, EntityJoltBlaze::new, new BestiaryInfo(0x499CAE),
            EntityJoltBlaze::buildLootTable
        );
        species.variant("Wildfire", EntityWildfireBlaze.class, EntityWildfireBlaze::new, new BestiaryInfo(0xF4EE32),
            EntityWildfireBlaze::buildLootTable
        );
    }

}
