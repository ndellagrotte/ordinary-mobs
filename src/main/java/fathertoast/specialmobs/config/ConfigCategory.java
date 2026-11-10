/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * One category of the config file. Subclasses read their options in their constructors, which keeps each option's
 * key, default and comment in a single place.
 * <p>
 * The comment suffixes written here are part of the config file's format and match the original mod's.
 */
abstract class ConfigCategory {

    /** Range: { 0.0, INF } */
    static final float[] R_FLT_POS = { 0.0F, Float.POSITIVE_INFINITY };
    /** Range: { 0.0, 1.0 } */
    static final float[] R_FLT_ONE = { 0.0F, 1.0F };

    /** The name of this category in the config file. */
    final String name;

    private final Configuration file;

    ConfigCategory(Configuration file, String name, String comment) {
        this.file = file;
        this.name = name;
        file.addCustomCategoryComment(name, comment);
    }

    /** Converts by way of the decimal text, so 0.6F becomes 0.6 and not 0.6000000238418579. */
    private static double prettyFloatToDouble(float f) {
        return Double.parseDouble(Float.toString(f));
    }

    /** @return The range that decimal options in this category have, as { min, max }. */
    float[] floatRange() {
        return R_FLT_POS;
    }

    boolean prop(String key, boolean defaultValue, String comment) {
        return cprop(key, defaultValue, comment).getBoolean();
    }

    Property cprop(String key, boolean defaultValue, String comment) {
        comment += "\n   >> Boolean:[ Valid_Values={ true, false }, Default=" + defaultValue + " ]";
        return file.get(name, key, defaultValue, comment);
    }

    int prop(String key, int defaultValue, String comment) {
        int min = 0;
        int max = Integer.MAX_VALUE;
        comment += "\n   >> Integer:[ Range={ " + min + ", " + max + " }, Default=" + defaultValue + " ]";
        return file.get(name, key, defaultValue, comment, min, max).getInt();
    }

    float prop(String key, float defaultValue, String comment) {
        float[] range = floatRange();
        comment += "\n   >> Float:[ Range={ " + range[0] + ", " + range[1] + " }, Default=" + defaultValue + " ]";
        return (float) file.get(name, key, prettyFloatToDouble(defaultValue), comment, prettyFloatToDouble(range[0]),
            prettyFloatToDouble(range[1])
        )
            .getDouble();
    }

    /**
     * Reads a list option.
     *
     * @param type        The name of what the list holds, as shown in the comment.
     * @param description A short description of what a line looks like.
     */
    Property cprop(String key, String[] defaultValues, String comment, String type, String description) {
        comment += "\n   >> " + type + ":[ Value={ " + description + " }, Default={ " +
            String.join(", ", defaultValues) + " } ]";
        return file.get(name, key, defaultValues, comment);
    }

}
