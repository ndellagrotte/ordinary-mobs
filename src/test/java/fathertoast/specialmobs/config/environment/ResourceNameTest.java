/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.config.environment;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceNameTest {

    @Test
    void idWithoutNamespaceIsVanilla() {
        assertThat(ResourceName.parse("desert")).isEqualTo(new ResourceName("minecraft", "desert"));
    }

    @Test
    void namespaceAndPathAreLowerCased() {
        assertThat(ResourceName.parse("BiomesOPlenty:Alps")).isEqualTo(new ResourceName("biomesoplenty", "alps"));
    }

    @Test
    void emptyPathIsAllowedForNamespaceWildcards() {
        assertThat(ResourceName.parse("biomesoplenty:")).isEqualTo(new ResourceName("biomesoplenty", ""));
        assertThat(ResourceName.parse("")).isEqualTo(new ResourceName("minecraft", ""));
    }

    @Test
    void printsAsNamespaceColonPath() {
        assertThat(ResourceName.parse("mesa_rock")).hasToString("minecraft:mesa_rock");
    }

}
