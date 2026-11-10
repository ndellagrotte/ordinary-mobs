/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.config.environment;

import java.util.Locale;

/**
 * A namespaced id, split the same way the game splits a resource location but without needing any game classes.
 *
 * @param namespace The lower-case namespace, "minecraft" when the id declares none.
 * @param path      The lower-case path.
 */
public record ResourceName(String namespace, String path) {

    public static final String DEFAULT_NAMESPACE = "minecraft";

    /** Splits an id such as "desert" or "biomesoplenty:alps". */
    public static ResourceName parse(String id) {
        String namespace = DEFAULT_NAMESPACE;
        String path = id;
        int colon = id.indexOf(':');
        if (colon >= 0) {
            path = id.substring(colon + 1);
            // The game ignores namespaces shorter than two characters, so this does as well
            if (colon > 1) {
                namespace = id.substring(0, colon);
            }
        }
        return new ResourceName(namespace.isEmpty() ? DEFAULT_NAMESPACE : namespace.toLowerCase(Locale.ROOT),
            path.toLowerCase(Locale.ROOT)
        );
    }

    @Override
    public String toString() {
        return namespace + ":" + path;
    }

}
