/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.client;

import fathertoast.specialmobs.CommonProxy;
import fathertoast.specialmobs.SpecialMobsMod;
import fathertoast.specialmobs.ai.IAngler;
import fathertoast.specialmobs.bestiary.MobFamily;
import fathertoast.specialmobs.config.Config;
import fathertoast.specialmobs.dev.SelfTest;
import fathertoast.specialmobs.entity.ISpecialMob;
import fathertoast.specialmobs.entity.projectile.EntitySpecialFishHook;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {

    /** The renderers are written for the vanilla mob classes, which every entity class of the family extends. */
    @SuppressWarnings("unchecked")
    private static <T extends Entity> void register(MobFamily family, IRenderFactory<? super T> renderFactory) {
        RenderingRegistry.registerEntityRenderingHandler((Class<T>) family.base().entityClass(), renderFactory);
    }

    /** Applies the options after they were changed on the in-game config screen. */
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (SpecialMobsMod.MOD_ID.equals(event.getModID())) {
            Config.reload();
        }
    }

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
        if (SelfTest.isRequested()) {
            SelfTestClient.register();
        }
        if (Config.get().GENERAL.FANCY_FISHING_MOBS) {
            // Replaces the vanilla property so the rod also shows as cast while a fishing mob has its line out
            Items.FISHING_ROD.addPropertyOverride(new ResourceLocation("cast"),
                new IAngler.ItemFishingRodPropertyGetter()
            );
        }
    }

    @Override
    public void applyTextures(int entityId, String[] texturePaths) {
        // Messages arrive on the network thread, the world may only be touched from the client thread
        Minecraft minecraft = Minecraft.getMinecraft();
        minecraft.addScheduledTask(() -> {
            if (minecraft.world == null) {
                return;
            }
            Entity entity = minecraft.world.getEntityByID(entityId);
            if (entity instanceof ISpecialMob mob) {
                mob.getSpecialData().loadTextures(texturePaths);
            }
        });
    }

}
