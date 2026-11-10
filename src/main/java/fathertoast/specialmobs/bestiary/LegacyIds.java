/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.bestiary;

import java.util.ArrayList;
import java.util.List;

/**
 * The entity names and network ids used by the original 1.12.2 releases, in registration order.
 * <p>
 * These are a compatibility contract: saved worlds store the lower-cased entity name, and the network id has to
 * match between client and server. Nothing in here touches game classes, so the table can be unit tested.
 */
public final class LegacyIds {

    /** Each row is a family name followed by its variant names, in the original registration order. */
    private static final String[][] FAMILIES = {
        {
            "Creeper",
            "Dark",
            "Death",
            "Dirt",
            "Doom",
            "Drowning",
            "Ender",
            "Fire",
            "Gravel",
            "Jumping",
            "Lightning",
            "Mini",
            "Splitting"
        },
        { "Zombie", "Brute", "Fire", "Fishing", "Giant", "Hungry", "Husk", "Plague" },
        { "PigZombie", "Brute", "Fishing", "Giant", "Hungry", "Knight", "Plague", "Vampire" },
        { "Skeleton", "Brute", "Fire", "Gatling", "Giant", "Knight", "Ninja", "Poison", "Sniper", "Spitfire", "Stray" },
        { "WitherSkeleton", "Brute", "Gatling", "Giant", "Knight", "Ninja", "Sniper", "Spitfire" },
        { "Slime", "Blackberry", "Blueberry", "Caramel", "Grape", "Lemon", "Strawberry", "Watermelon" },
        { "LavaSlime", "Flying", "Hardened", "Sticky", "Volatile" },
        { "Spider", "Baby", "Desert", "Flying", "Giant", "Hungry", "Mother", "Pale", "Poison", "Web", "Witch" },
        { "CaveSpider", "Baby", "Flying", "Mother", "Web", "Witch" },
        { "Silverfish", "Blinding", "Fishing", "Flying", "Poison", "Tough" },
        { "Enderman", "Blinding", "Icy", "Lightning", "Mini", "Mirage", "Thief" },
        { "Witch", "Domination", "Shadows", "Undead", "Wilds", "Wind" },
        { "Ghast", "Baby", "Fighter", "King", "Queen", "Unholy" },
        { "Blaze", "Cinder", "Conflagration", "Ember", "Hellfire", "Inferno", "Jolt", "Wildfire" },
    };

    /** The entity name of the fishing mobs' hook, registered after every mob. */
    public static final String FISH_HOOK_NAME = "SMFishHook";
    /** The network id of the fishing mobs' hook. */
    public static final int FISH_HOOK_ID = 111;

    private LegacyIds() { }

    /** @return The variant names of a family, in registration order. */
    public static String[] variantNames(String familyName) {
        String[] row = row(familyName);
        String[] names = new String[row.length - 1];
        System.arraycopy(row, 1, names, 0, names.length);
        return names;
    }

    /** @return The network id of a family's vanilla replacement; its variants follow it one by one. */
    public static int firstNetworkId(String familyName) {
        int id = 0;
        for (String[] row : FAMILIES) {
            if (row[0].equals(familyName)) {
                return id;
            }
            id += row.length;
        }
        throw new IllegalArgumentException("Unknown mob family '" + familyName + "'");
    }

    /** @return The entity name of a family's vanilla replacement. */
    public static String baseEntityName(String familyName) {
        return "Special" + familyName;
    }

    /** @return The entity name of a family's special variant. */
    public static String variantEntityName(String familyName, String variantName) {
        return variantName + familyName;
    }

    /** @return Every entity name this mod registers; the position in the list is the network id. */
    public static List<String> allEntityNames() {
        List<String> names = new ArrayList<>();
        for (String[] row : FAMILIES) {
            names.add(baseEntityName(row[0]));
            for (int i = 1; i < row.length; i++) {
                names.add(variantEntityName(row[0], row[i]));
            }
        }
        names.add(FISH_HOOK_NAME);
        return names;
    }

    private static String[] row(String familyName) {
        for (String[] row : FAMILIES) {
            if (row[0].equals(familyName)) {
                return row;
            }
        }
        throw new IllegalArgumentException("Unknown mob family '" + familyName + "'");
    }

}
