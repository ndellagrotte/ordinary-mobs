/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.config.environment;

import fathertoast.specialmobs.bestiary.SpawnThemes;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EnvironmentListTest {

    private static final SpawnLocation DESERT_HILLS = overworld("minecraft:desert_hills", 17);
    private static final SpawnLocation PLAINS = overworld("minecraft:plains", 1);
    private static final SpawnLocation NETHER = new SpawnLocation("the_nether", -1,
        ResourceName.parse("minecraft:hell"), 8
    );

    private static SpawnLocation overworld(String biome, int biomeId) {
        return new SpawnLocation("overworld", 0, ResourceName.parse(biome), biomeId);
    }

    private static EnvironmentList list(String... lines) {
        List<String> problems = new ArrayList<>();
        EnvironmentList list = new EnvironmentList(lines, problems::add);
        assertThat(problems).isEmpty();
        return list;
    }

    @Test
    void prefixEntryMatchesEveryBiomeStartingWithIt() {
        EnvironmentList list = list("biome/minecraft:desert*=200.0");

        assertThat(list.valueFor(DESERT_HILLS, 100)).isEqualTo(200);
        assertThat(list.valueFor(overworld("minecraft:desert", 2), 100)).isEqualTo(200);
        assertThat(list.valueFor(PLAINS, 100)).isEqualTo(100);
    }

    @Test
    void prefixEntryStaysInsideItsNamespace() {
        EnvironmentList list = list("biome/minecraft:desert*=200.0");

        assertThat(list.valueFor(overworld("othermod:desert_oasis", 90), 100)).isEqualTo(100);
    }

    @Test
    void prefixWithoutNamespaceMeansVanilla() {
        EnvironmentList list = list("biome/mesa*=0");

        assertThat(list.valueFor(overworld("minecraft:mesa_rock", 38), 100)).isZero();
        assertThat(list.valueFor(overworld("othermod:mesa", 91), 100)).isEqualTo(100);
    }

    @Test
    void namespaceWildcardMatchesWholeMod() {
        EnvironmentList list = list("biome/biomesoplenty:*=5");

        assertThat(list.valueFor(overworld("biomesoplenty:alps", 60), 100)).isEqualTo(5);
        assertThat(list.valueFor(PLAINS, 100)).isEqualTo(100);
    }

    @Test
    void exactBiomeBeatsPrefixBeatsDimension() {
        EnvironmentList list = list("dimension/overworld=1", "biome/minecraft:desert*=2",
            "biome/minecraft:desert_hills=3"
        );

        assertThat(list.valueFor(DESERT_HILLS, 0)).isEqualTo(3);
        assertThat(list.valueFor(overworld("minecraft:desert", 2), 0)).isEqualTo(2);
        assertThat(list.valueFor(PLAINS, 0)).isEqualTo(1);
        assertThat(list.valueFor(NETHER, 0)).isZero();
    }

    @Test
    void firstListedEntryOfTheSameKindWins() {
        EnvironmentList list = list("biome/minecraft:ice*=200", "biome/minecraft:ice_flats*=20");

        assertThat(list.valueFor(overworld("minecraft:ice_flats", 12), 100)).isEqualTo(200);
    }

    @Test
    void dimensionMatchesByNameIgnoringCase() {
        EnvironmentList list = list("dimension/The_Nether=0.33");

        assertThat(list.valueFor(NETHER, 0.6F)).isEqualTo(0.33F);
        assertThat(list.valueFor(PLAINS, 0.6F)).isEqualTo(0.6F);
    }

    @Test
    void numericalIdsStillWorkButAreReported() {
        List<String> problems = new ArrayList<>();
        EnvironmentList list = new EnvironmentList(new String[] { "dimension/-1=7", "biome/1=9" }, problems::add);

        assertThat(list.valueFor(NETHER, 0)).isEqualTo(7);
        assertThat(list.valueFor(PLAINS, 0)).isEqualTo(9);
        assertThat(problems).hasSize(2);
    }

    @Test
    void unreadableLinesAreSkippedAndReported() {
        List<String> problems = new ArrayList<>();
        EnvironmentList list = new EnvironmentList(new String[] {
                "biome/minecraft:plains",
                "biome/minecraft:plains=lots",
                "minecraft:plains=1",
                "sky/high=1"
            },
            problems::add
        );

        assertThat(list.isEmpty()).isTrue();
        assertThat(problems).hasSize(4);
    }

    @Test
    void whitespaceAroundTheParts() {
        EnvironmentList list = list(" biome/minecraft:plains = 4 ");

        assertThat(list.valueFor(PLAINS, 0)).isEqualTo(4);
    }

    @Test
    void defaultThemeLinesKeepTheOriginalFormat() {
        assertThat(SpawnThemes.FIRE[0]).isEqualTo("biome/minecraft:desert*=200.0");
        assertThat(SpawnThemes.FIRE[3]).isEqualTo("dimension/the_nether=200.0");
        assertThat(SpawnThemes.WATER[1]).isEqualTo("biome/minecraft:beaches=200.0");
        assertThat(SpawnThemes.WATER[7]).isEqualTo("dimension/the_nether=0.0");
        assertThat(SpawnThemes.dimension("overworld", 0.25F)).isEqualTo("dimension/overworld=0.25");
    }

    @Test
    void everyDefaultThemeLineIsReadable() {
        for (String[] theme : List.of(SpawnThemes.FIRE, SpawnThemes.ICE, SpawnThemes.FOREST, SpawnThemes.MOUNTAIN,
            SpawnThemes.WATER, SpawnThemes.FISHING
        )) {
            List<String> problems = new ArrayList<>();
            new EnvironmentList(theme, problems::add);
            assertThat(problems).isEmpty();
        }
    }

    @Test
    void fireThemeAppliesWhereItSaysItDoes() {
        EnvironmentList fire = list(SpawnThemes.FIRE);

        assertThat(fire.valueFor(DESERT_HILLS, 100)).isEqualTo(200);
        assertThat(fire.valueFor(NETHER, 100)).isEqualTo(200);
        assertThat(fire.valueFor(overworld("minecraft:ice_flats", 12), 100)).isEqualTo(20);
        assertThat(fire.valueFor(PLAINS, 100)).isEqualTo(100);
    }

}
