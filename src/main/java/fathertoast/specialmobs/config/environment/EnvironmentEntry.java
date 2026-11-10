/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.config.environment;

import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/**
 * One line of an environment list in the config: a value that applies to a biome, to a group of biomes sharing an
 * id prefix, or to a dimension.
 * <p>
 * Lines look like {@code biome/mod_id:biome_id=value}, {@code biome/mod_id:prefix*=value} and
 * {@code dimension/dimension_name=value}.
 */
public sealed interface EnvironmentEntry {

    /**
     * Reads one config line.
     *
     * @param line     The line to read.
     * @param problems Told what is wrong with the line when it can not be read.
     *
     * @return The entry, or null if the line is not usable.
     */
    static @Nullable
    EnvironmentEntry parse(String line, Consumer<String> problems) {
        String[] targetAndValue = line.split("=", 2);
        if (targetAndValue.length < 2) {
            problems.accept("Ignoring config line '" + line + "' - contains no value parameter");
            return null;
        }
        float value;
        try {
            value = Float.parseFloat(targetAndValue[1].trim());
        } catch (NumberFormatException ex) {
            problems.accept("Ignoring config line '" + line + "' - the value is not a number");
            return null;
        }

        String[] typeAndId = targetAndValue[0].trim().split("/", 2);
        if (typeAndId.length < 2) {
            problems.accept("Ignoring config line '" + line +
                    "' - must declare environment (e.g., 'biome/' or 'dimension/')"
            );
            return null;
        }
        String id = typeAndId[1].trim();
        if (typeAndId[0].equalsIgnoreCase("biome")) {
            if (id.endsWith("*")) {
                ResourceName prefix = ResourceName.parse(id.substring(0, id.length() - 1));
                return new BiomePrefix(prefix.namespace(), prefix.path(), value);
            }
            Integer numericalId = parseInt(id);
            if (numericalId != null) {
                problems.accept("Numerical id used in config line '" + line + "'. Please use the biome's name.");
                return new ExactBiome(null, numericalId, value);
            }
            return new ExactBiome(ResourceName.parse(id), -1, value);
        }
        if (typeAndId[0].equalsIgnoreCase("dimension")) {
            Integer numericalId = parseInt(id);
            if (numericalId != null) {
                problems.accept("Numerical id used in config line '" + line + "'. Please use the dimension's name.");
                return new Dimension(null, numericalId, value);
            }
            return new Dimension(id, 0, value);
        }
        problems.accept("Ignoring config line '" + line + "' - unrecognized environment '" + typeAndId[0] + "'");
        return null;
    }

    private static @Nullable
    Integer parseInt(String text) {
        try {
            return Integer.valueOf(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /** @return The value to use where this entry applies. */
    float value();

    /** @return How wide this entry's reach is. Entries with a narrower reach are checked first. */
    int scope();

    /** @return True if this entry applies to the location. */
    boolean matches(SpawnLocation location);

    /** A value for one biome, named by its id or, as a legacy fallback, its numerical id. */
    record ExactBiome(@Nullable ResourceName biome, int numericalId, float value) implements EnvironmentEntry {

        @Override
        public int scope() {
            return 0;
        }

        @Override
        public boolean matches(SpawnLocation location) {
            return biome != null ? biome.equals(location.biome()) : numericalId == location.biomeId();
        }

    }

    /** A value for every biome in a namespace whose path starts with a prefix. */
    record BiomePrefix(String namespace, String pathPrefix, float value) implements EnvironmentEntry {

        @Override
        public int scope() {
            return 1;
        }

        @Override
        public boolean matches(SpawnLocation location) {
            return namespace.equals(location.biome().namespace()) && location.biome().path().startsWith(pathPrefix);
        }

    }

    /** A value for a whole dimension, named by its type name or, as a legacy fallback, its numerical id. */
    record Dimension(@Nullable String name, int numericalId, float value) implements EnvironmentEntry {

        @Override
        public int scope() {
            return 2;
        }

        @Override
        public boolean matches(SpawnLocation location) {
            return name != null
                ? name.equalsIgnoreCase(location.dimensionName())
                : numericalId == location.dimensionId();
        }

    }

}
