/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.spawn;

import java.util.Random;

/** Picks one of several options at random, where each option's chance is proportional to its weight. */
public final class WeightedChoice {

    private WeightedChoice() { }

    /** @return The sum of all positive weights. */
    public static int totalWeight(int[] weights) {
        int total = 0;
        for (int weight : weights) {
            if (weight > 0) {
                total += weight;
            }
        }
        return total;
    }

    /**
     * @param weights     The weight of each option. Options with a weight of zero or less are never picked.
     * @param totalWeight The result of {@link #totalWeight(int[])} for the weights.
     *
     * @return The index of the picked option, or -1 if no option can be picked.
     */
    public static int pick(int[] weights, int totalWeight, Random random) {
        if (totalWeight <= 0) {
            return -1;
        }
        int remaining = random.nextInt(totalWeight);
        for (int i = 0; i < weights.length; i++) {
            if (weights[i] > 0) {
                remaining -= weights[i];
                if (remaining < 0) {
                    return i;
                }
            }
        }
        return -1;
    }

}
