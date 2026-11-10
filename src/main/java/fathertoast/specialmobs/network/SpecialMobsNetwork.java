/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.network;

import fathertoast.specialmobs.entity.ISpecialMob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/** This mod's network channel. */
public final class SpecialMobsNetwork {

    private static final String CHANNEL_NAME = "SM|FX";

    private static SimpleNetworkWrapper channel;

    private SpecialMobsNetwork() { }

    /** Creates the channel. Messages are registered on both sides, since the server has to be able to send them. */
    public static void init() {
        channel = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL_NAME);
        channel.registerMessage(MessageTexture.Handler.class, MessageTexture.class, 0, Side.CLIENT);
        MinecraftForge.EVENT_BUS.register(SpecialMobsNetwork.class);
    }

    /** Sends a mob's textures to every player that can currently see it. */
    public static <T extends Entity & ISpecialMob> void sendTextures(T entity) {
        channel.sendToAllTracking(new MessageTexture(entity), entity);
    }

    /** Sends a mob's non-default textures to a player that has just come into range of it. */
    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ISpecialMob mob &&
            mob.getSpecialData().hasCustomTextures() &&
            event.getEntityPlayer() instanceof EntityPlayerMP player) {
            channel.sendTo(new MessageTexture(asSpecialMob(event.getTarget())), player);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity & ISpecialMob> T asSpecialMob(Entity entity) {
        return (T) entity;
    }

}
