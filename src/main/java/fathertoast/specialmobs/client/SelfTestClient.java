/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.client;

import fathertoast.specialmobs.SpecialMobsMod;
import fathertoast.specialmobs.bestiary.MobFamily;
import fathertoast.specialmobs.dev.SelfTest;
import fathertoast.specialmobs.entity.ISpecialMob;
import org.apache.commons.io.FileUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.File;

/** The client side of {@link SelfTest}: opens the test world, checks what only the client can see, and closes the game. */
final class SelfTestClient {

    private static final String WORLD_NAME = "specialmobs_selftest";

    private boolean worldOpened;
    private int ticksOnMainMenu;
    private int ticksAfterServerFinished;

    private SelfTestClient() { }

    static void register() {
        MinecraftForge.EVENT_BUS.register(new SelfTestClient());
    }

    private static void checkConfigScreen(Minecraft minecraft) {
        GuiScreen screen = new SpecialMobsGuiFactory().createConfigGui(minecraft.currentScreen);
        int categories = screen instanceof GuiConfig config ? config.configElements.size() : -1;
        SelfTest.check("config screen lists every category", categories == 15, "it has " + categories + " entries");
    }

    /** Without its own renderer a family is drawn as the vanilla mob, with none of the variants' textures or sizes. */
    private static void checkRenderers(Minecraft minecraft) {
        String ownPackage = SelfTestClient.class.getPackage().getName();
        for (MobFamily family : MobFamily.values()) {
            Render<?> renderer = minecraft.getRenderManager().getEntityClassRenderObject(family.base().entityClass());
            String name = renderer == null ? "none" : renderer.getClass().getName();
            SelfTest.check("renderer of " + family.key + " is this mod's", name.startsWith(ownPackage + '.'),
                "it is " + name
            );
        }
    }

    private static void openTestWorld(Minecraft minecraft) {
        // Always start from a fresh world, so earlier runs can not affect this one
        FileUtils.deleteQuietly(new File(minecraft.gameDir, "saves/" + WORLD_NAME));
        WorldSettings settings = new WorldSettings(8675309L, GameType.CREATIVE, false, false, WorldType.FLAT);
        settings.enableCommands();
        SpecialMobsMod.log().info("[selftest] opening test world");
        minecraft.launchIntegratedServer(WORLD_NAME, WORLD_NAME, settings);
    }

    private static void checkCustomTexture(Minecraft minecraft) {
        Entity entity = minecraft.world == null ? null : minecraft.world.getEntityByID(SelfTest.customTextureEntityId);
        String texture = entity instanceof ISpecialMob mob ? mob.getSpecialData().getTexture().toString() : null;
        SelfTest.check("client was told about a texture changed through nbt", SelfTest.CUSTOM_TEXTURE.equals(texture),
            "client has " + texture + " for " + entity
        );
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getMinecraft();
        if (!worldOpened) {
            if (minecraft.currentScreen instanceof GuiMainMenu && ++ticksOnMainMenu > 20) {
                worldOpened = true;
                checkConfigScreen(minecraft);
                checkRenderers(minecraft);
                openTestWorld(minecraft);
            }
        } else if (SelfTest.serverFinished && ++ticksAfterServerFinished == 20) {
            checkCustomTexture(minecraft);
            SelfTest.logResult();
            minecraft.shutdown();
        }
    }

}
