/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.witherskeleton;

import fathertoast.specialmobs.bestiary.BestiaryInfo;
import fathertoast.specialmobs.bestiary.SpeciesList;

/** Declares the species of the WitherSkeleton family: its vanilla replacement, then its special variants. */
public final class WitherSkeletonSpecies {

    private WitherSkeletonSpecies() { }

    public static void declare(SpeciesList species) {
        species.base(Entity_SpecialWitherSkeleton.class, Entity_SpecialWitherSkeleton::new, 0x474D4D);
        species.variant("Brute", EntityBruteWitherSkeleton.class, EntityBruteWitherSkeleton::new,
            new BestiaryInfo(0xFFF87E), EntityBruteWitherSkeleton::buildLootTable
        );
        species.variant("Gatling", EntityGatlingWitherSkeleton.class, EntityGatlingWitherSkeleton::new,
            new BestiaryInfo(0xFFFF0B), EntityGatlingWitherSkeleton::buildLootTable
        );
        species.variant("Giant", EntityGiantWitherSkeleton.class, EntityGiantWitherSkeleton::new,
            new BestiaryInfo(0x494949), EntityGiantWitherSkeleton::buildLootTable
        );
        species.variant("Knight", EntityKnightWitherSkeleton.class, EntityKnightWitherSkeleton::new,
            new BestiaryInfo(0xDDDDDD), EntityKnightWitherSkeleton::buildLootTable
        );
        species.variant("Ninja", EntityNinjaWitherSkeleton.class, EntityNinjaWitherSkeleton::new,
            new BestiaryInfo(0x333366), EntityNinjaWitherSkeleton::buildLootTable
        );
        species.variant("Sniper", EntitySniperWitherSkeleton.class, EntitySniperWitherSkeleton::new,
            new BestiaryInfo(0x486720), EntitySniperWitherSkeleton::buildLootTable
        );
        species.variant("Spitfire", EntitySpitfireWitherSkeleton.class, EntitySpitfireWitherSkeleton::new,
            new BestiaryInfo(0xDC1A00).withWeight(BestiaryInfo.BASE_WEIGHT_UNCOMMON),
            EntitySpitfireWitherSkeleton::buildLootTable
        );
    }

}
