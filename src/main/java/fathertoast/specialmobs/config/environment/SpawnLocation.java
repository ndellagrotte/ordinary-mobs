/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.config.environment;

/**
 * Everything the environment config entries can ask about a location.
 *
 * @param dimensionName The name of the dimension's type, such as "the_nether".
 * @param dimensionId   The numerical id of the dimension.
 * @param biome         The id of the biome. For a mutated biome this is the biome it is a mutation of.
 * @param biomeId       The numerical id of that same biome.
 */
public record SpawnLocation(String dimensionName, int dimensionId, ResourceName biome, int biomeId) { }
