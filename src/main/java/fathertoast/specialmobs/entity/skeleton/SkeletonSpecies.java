/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.skeleton;

import fathertoast.specialmobs.bestiary.BestiaryInfo;
import fathertoast.specialmobs.bestiary.SpawnThemes;
import fathertoast.specialmobs.bestiary.SpeciesList;

/** Declares the species of the Skeleton family: its vanilla replacement, then its special variants. */
public final class SkeletonSpecies {

    private SkeletonSpecies() { }

    public static void declare(SpeciesList species) {
        species.base(Entity_SpecialSkeleton.class, Entity_SpecialSkeleton::new, 0x494949);
        species.variant("Brute", EntityBruteSkeleton.class, EntityBruteSkeleton::new, new BestiaryInfo(0xFFF87E),
            EntityBruteSkeleton::buildLootTable
        );
        species.variant("Fire", EntityFireSkeleton.class, EntityFireSkeleton::new,
            new BestiaryInfo(0xDC1A00).withWeightExceptions(SpawnThemes.FIRE), EntityFireSkeleton::buildLootTable
        );
        species.variant("Gatling", EntityGatlingSkeleton.class, EntityGatlingSkeleton::new, new BestiaryInfo(0xFFFF0B),
            EntityGatlingSkeleton::buildLootTable
        );
        species.variant("Giant", EntityGiantSkeleton.class, EntityGiantSkeleton::new, new BestiaryInfo(0x494949),
            EntityGiantSkeleton::buildLootTable
        );
        species.variant("Knight", EntityKnightSkeleton.class, EntityKnightSkeleton::new, new BestiaryInfo(0xDDDDDD),
            EntityKnightSkeleton::buildLootTable
        );
        species.variant("Ninja", EntityNinjaSkeleton.class, EntityNinjaSkeleton::new, new BestiaryInfo(0x333366),
            EntityNinjaSkeleton::buildLootTable
        );
        species.variant("Poison", EntityPoisonSkeleton.class, EntityPoisonSkeleton::new,
            new BestiaryInfo(0x779C68).withWeightExceptions(SpawnThemes.FOREST), EntityPoisonSkeleton::buildLootTable
        );
        species.variant("Sniper", EntitySniperSkeleton.class, EntitySniperSkeleton::new, new BestiaryInfo(0x486720),
            EntitySniperSkeleton::buildLootTable
        );
        species.variant("Spitfire", EntitySpitfireSkeleton.class, EntitySpitfireSkeleton::new,
            new BestiaryInfo(0xDC1A00)
                .withWeight(BestiaryInfo.BASE_WEIGHT_UNCOMMON)
                .withWeightExceptions(SpawnThemes.FIRE),
            EntitySpitfireSkeleton::buildLootTable
        );
        species.variant("Stray", EntityStraySkeleton.class, EntityStraySkeleton::new,
            new BestiaryInfo(0xDDEAEA)
                .withWeight(BestiaryInfo.BASE_WEIGHT_UNCOMMON)
                .withWeightExceptions(SpawnThemes.ICE),
            EntityStraySkeleton::buildLootTable
        );
    }

}
