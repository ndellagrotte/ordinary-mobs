/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.spawn;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class WeightedChoiceTest {

    @Test
    void totalIgnoresZeroAndNegativeWeights() {
        assertThat(WeightedChoice.totalWeight(new int[] { 100, 0, -50, 20 })).isEqualTo(120);
    }

    @Test
    void nothingIsPickedWhenEveryWeightIsZero() {
        int[] weights = { 0, 0, -5 };

        assertThat(WeightedChoice.pick(weights, WeightedChoice.totalWeight(weights), new Random(1))).isEqualTo(-1);
    }

    @Test
    void optionsWithoutWeightAreNeverPicked() {
        int[] weights = { 0, 100, -20, 50 };
        int total = WeightedChoice.totalWeight(weights);
        Random random = new Random(42);

        for (int i = 0; i < 2000; i++) {
            assertThat(WeightedChoice.pick(weights, total, random)).isIn(1, 3);
        }
    }

    @Test
    void picksFollowTheWeights() {
        int[] weights = { 200, 100, 20 };
        int total = WeightedChoice.totalWeight(weights);
        Random random = new Random(7);
        int[] counts = new int[weights.length];

        int rolls = 32000;
        for (int i = 0; i < rolls; i++) {
            counts[WeightedChoice.pick(weights, total, random)]++;
        }

        for (int i = 0; i < weights.length; i++) {
            double expected = rolls * (double) weights[i] / total;
            assertThat((double) counts[i]).isBetween(expected * 0.9, expected * 1.1);
        }
    }

}
