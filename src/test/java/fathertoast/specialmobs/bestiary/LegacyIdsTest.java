/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.bestiary;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class LegacyIdsTest {

    @Test
    void thereAre112EntitiesEndingWithTheFishHook() {
        List<String> names = LegacyIds.allEntityNames();

        assertThat(names).hasSize(112);
        assertThat(names.get(LegacyIds.FISH_HOOK_ID)).isEqualTo("SMFishHook");
    }

    @Test
    void registryNamesAreUnique() {
        List<String> registryNames = LegacyIds.allEntityNames()
            .stream()
            .map(name -> name.toLowerCase(Locale.ROOT))
            .toList();

        assertThat(registryNames).doesNotHaveDuplicates();
    }

    @Test
    void networkIdsMatchTheOriginalRegistrationOrder() {
        List<String> names = LegacyIds.allEntityNames();

        assertThat(names.get(0)).isEqualTo("SpecialCreeper");
        assertThat(names.get(1)).isEqualTo("DarkCreeper");
        assertThat(names.get(12)).isEqualTo("SplittingCreeper");
        assertThat(names.get(13)).isEqualTo("SpecialZombie");
        assertThat(names.get(21)).isEqualTo("SpecialPigZombie");
        assertThat(names.get(56)).isEqualTo("SpecialLavaSlime");
        assertThat(names.get(96)).isEqualTo("WindWitch");
        assertThat(names.get(110)).isEqualTo("WildfireBlaze");
    }

    @Test
    void familiesStartWhereThePreviousOneEnds() {
        String[] families = {
            "Creeper",
            "Zombie",
            "PigZombie",
            "Skeleton",
            "WitherSkeleton",
            "Slime",
            "LavaSlime",
            "Spider",
            "CaveSpider",
            "Silverfish",
            "Enderman",
            "Witch",
            "Ghast",
            "Blaze",
        };
        int[] firstIds = { 0, 13, 21, 29, 40, 48, 56, 61, 72, 78, 84, 91, 97, 103 };

        for (int i = 0; i < families.length; i++) {
            assertThat(LegacyIds.firstNetworkId(families[i])).as(families[i]).isEqualTo(firstIds[i]);
            int next = i + 1 < families.length ? firstIds[i + 1] : LegacyIds.FISH_HOOK_ID;
            assertThat(firstIds[i] + 1 + LegacyIds.variantNames(families[i]).length).as(families[i]).isEqualTo(next);
        }
    }

}
