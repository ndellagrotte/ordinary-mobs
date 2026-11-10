/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.config;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BlockTargetTest {

    private final List<String> problems = new ArrayList<>();

    @Test
    void plainBlockId() {
        assertThat(BlockTarget.parse("minecraft:grass", problems::add)).isEqualTo(new BlockTarget("minecraft:grass",
            false, -1
        ));
        assertThat(problems).isEmpty();
    }

    @Test
    void blockIdWithMetadata() {
        assertThat(BlockTarget.parse("minecraft:stone 3", problems::add)).isEqualTo(new BlockTarget("minecraft:stone",
            false, 3
        ));
    }

    @Test
    void wildcardKeepsEverythingBeforeTheStar() {
        assertThat(BlockTarget.parse("somemod:*", problems::add)).isEqualTo(new BlockTarget("somemod:", true, -1));
    }

    @Test
    void badMetadataIsReported() {
        assertThat(BlockTarget.parse("minecraft:stone granite", problems::add)).isNull();
        assertThat(problems).hasSize(1);
    }

    @Test
    void blankLineIsIgnored() {
        assertThat(BlockTarget.parse("  ", problems::add)).isNull();
        assertThat(problems).isEmpty();
    }

}
