/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs;

/** The things this mod does differently on the client. This is the server side, which does none of them. */
public class CommonProxy {

    /** Registers the renderers for this mod's entities. */
    public void registerRenderers() { }

    /** Sets up anything that has to wait for the init phase. */
    public void init() { }

    /**
     * Applies textures received from the server to a special mob.
     *
     * @param entityId     The network id of the mob.
     * @param texturePaths The base, eyes and overlay texture; the last two are empty if the mob has none.
     */
    public void applyTextures(int entityId, String[] texturePaths) { }

}
