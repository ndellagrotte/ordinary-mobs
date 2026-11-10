/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.config.environment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/**
 * A config value that changes with the location. The most specific entry that applies wins: a single biome beats a
 * biome group, which beats a dimension. Between entries of the same kind, the one listed first wins.
 */
public final class EnvironmentList {

    private final EnvironmentEntry[] entries;

    /**
     * @param lines    The config lines, one entry each.
     * @param problems Told about each line that can not be read.
     */
    public EnvironmentList(String[] lines, Consumer<String> problems) {
        List<EnvironmentEntry> parsed = new ArrayList<>(lines.length);
        for (String line : lines) {
            EnvironmentEntry entry = EnvironmentEntry.parse(line, problems);
            if (entry != null) {
                parsed.add(entry);
            }
        }
        // The sort is stable, so entries with the same scope keep the order they were listed in
        parsed.sort(Comparator.comparingInt(EnvironmentEntry::scope));
        entries = parsed.toArray(new EnvironmentEntry[0]);
    }

    /** @return True if no entry could ever apply. */
    public boolean isEmpty() {
        return entries.length == 0;
    }

    /** @return The value for a location, or the default value when no entry applies there. */
    public float valueFor(SpawnLocation location, float defaultValue) {
        for (EnvironmentEntry entry : entries) {
            if (entry.matches(location)) {
                return entry.value();
            }
        }
        return defaultValue;
    }

}
