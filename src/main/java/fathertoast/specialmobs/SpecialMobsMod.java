/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs;

import fathertoast.specialmobs.mixin.EntityCreeperAccessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
    modid = SpecialMobsMod.MOD_ID,
    name = SpecialMobsMod.NAME,
    version = SpecialMobsMod.VERSION,
    acceptedMinecraftVersions = "[1.12.2]"
)
public class SpecialMobsMod {

    /** The mod id. This is a compatibility contract with saved worlds and pack configs, so it is not a build token. */
    public static final String MOD_ID = "specialmobs";
    public static final String NAME = "Special Mobs";
    public static final String VERSION = "@{MOD_VERSION}";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Mixin check: creeper powered parameter id {}", EntityCreeperAccessor.specialmobs$getPowered().getId());
    }

}
