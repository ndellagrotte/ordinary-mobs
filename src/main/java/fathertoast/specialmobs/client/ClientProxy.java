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
import fathertoast.specialmobs.compat.jer.JerPlugin;
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
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {

    /** The mod id of Just Enough Resources, an optional mod that shows mob drops. */
    static final String JER_MOD_ID = "jeresources";

    /** The renderers are written for the vanilla mob classes, which every entity class of the family extends. */
    @SuppressWarnings("unchecked")
    private static <T extends Entity> void register(MobFamily family, IRenderFactory<? super T> renderFactory) {
        RenderingRegistry.registerEntityRenderingHandler((Class<T>) family.base().entityClass(), renderFactory);
    }

    /**
     * Each family's renderer is registered for its vanilla replacement only. The variants extend that class and so
     * get the same renderer.
     */
    @Override
    public void registerRenderers() {
        register(MobFamily.BLAZE, RenderSpecialBlaze::new);
        register(MobFamily.CAVE_SPIDER, RenderSpecialSpider::new);
        register(MobFamily.CREEPER, RenderSpecialCreeper::new);
        register(MobFamily.ENDERMAN, RenderSpecialEnderman::new);
        register(MobFamily.GHAST, RenderSpecialGhast::new);
        register(MobFamily.MAGMA_CUBE, RenderSpecialLavaSlime::new);
        register(MobFamily.ZOMBIE_PIGMAN, RenderSpecialZombie::new);
        register(MobFamily.SILVERFISH, RenderSpecialSilverfish::new);
        register(MobFamily.SKELETON, RenderSpecialSkeleton::new);
        register(MobFamily.SLIME, RenderSpecialSlime::new);
        register(MobFamily.SPIDER, RenderSpecialSpider::new);
        register(MobFamily.WITCH, RenderSpecialWitch::new);
        register(MobFamily.WITHER_SKELETON, RenderSpecialSkeleton::new);
        register(MobFamily.ZOMBIE, RenderSpecialZombie::new);

        RenderingRegistry.registerEntityRenderingHandler(EntitySpecialFishHook.class, RenderSpecialFishHook::new);
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
        if (Loader.isModLoaded(JER_MOD_ID)) {
            // Every JER type stays inside JerPlugin, which is only loaded here
            JerPlugin.register();
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
