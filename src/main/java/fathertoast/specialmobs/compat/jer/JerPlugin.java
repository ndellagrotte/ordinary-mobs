/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.compat.jer;

import fathertoast.specialmobs.SpecialMobsMod;
import fathertoast.specialmobs.bestiary.MobFamily;
import fathertoast.specialmobs.bestiary.Species;
import fathertoast.specialmobs.entity.ISpecialMob;
import fathertoast.specialmobs.entity.ghast.EntityFighterGhast;
import fathertoast.specialmobs.entity.ghast.EntityKingGhast;
import fathertoast.specialmobs.entity.ghast.EntityQueenGhast;
import fathertoast.specialmobs.entity.ghast.EntityUnholyGhast;
import fathertoast.specialmobs.entity.pigzombie.EntityGiantPigZombie;
import fathertoast.specialmobs.entity.skeleton.EntityGiantSkeleton;
import fathertoast.specialmobs.entity.skeleton.EntitySpitfireSkeleton;
import fathertoast.specialmobs.entity.spider.EntityGiantSpider;
import fathertoast.specialmobs.entity.spider.EntityHungrySpider;
import fathertoast.specialmobs.entity.spider.EntityMotherSpider;
import fathertoast.specialmobs.entity.witherskeleton.EntityGiantWitherSkeleton;
import fathertoast.specialmobs.entity.witherskeleton.EntitySpitfireWitherSkeleton;
import fathertoast.specialmobs.entity.zombie.EntityGiantZombie;
import jeresources.api.IJERAPI;
import jeresources.api.IMobRegistry;
import jeresources.api.JERPlugin;
import jeresources.api.conditionals.LightLevel;
import jeresources.api.render.IMobRenderHook;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Lists every special variant in the mob tab of Just Enough Resources, with its drops, experience and light level.
 * <p>
 * JER is optional, and this is the only class that refers to it: it must only be loaded when JER is.
 */
@SideOnly(Side.CLIENT)
public final class JerPlugin {

    /*
     * How much the mobs that do not fit JER's preview are shrunk. JER picks the preview scale from the hitbox, in
     * steps, and these are drawn larger than that leaves room for.
     */
    private static final float GIANT_FIT = 0.9F;
    private static final float GIANT_SPIDER_FIT = 0.67F;
    private static final float MOTHER_SPIDER_FIT = 0.72F;
    private static final float SMALL_GHAST_FIT = 0.75F;
    private static final float QUEEN_GHAST_FIT = 0.84F;
    private static final float KING_GHAST_FIT = 0.7F;

    /** Set by JER before this mod's init. JER finds the field through the annotation. */
    @JERPlugin
    public static IJERAPI api;

    private static int registered;

    private JerPlugin() { }

    /** Registers the variants. JER collects registrations until all mods have loaded, so this belongs in init. */
    public static void register() {
        IJERAPI jer = api;
        if (jer == null) {
            SpecialMobsMod.log().warn("JER: it is loaded but did not provide its API, no mob drops are registered");
            return;
        }
        IMobRegistry mobs = jer.getMobRegistry();
        // Asked for once: outside a world, every call makes a new placeholder world
        World world = jer.getWorld();

        int count = 0;
        for (MobFamily family : MobFamily.values()) {
            // The vanilla replacements are left out, they drop what JER already shows for the vanilla mob
            for (Species species : family.variants()) {
                try {
                    if (register(mobs, world, species)) {
                        count++;
                    }
                } catch (RuntimeException | LinkageError ex) {
                    SpecialMobsMod.log().error("JER: skipped {}", species.entityName(), ex);
                }
            }
        }
        registered = count;

        fit(mobs, EntityGiantZombie.class, GIANT_FIT);
        fit(mobs, EntityGiantPigZombie.class, GIANT_FIT);
        fit(mobs, EntityGiantSkeleton.class, GIANT_FIT);
        fit(mobs, EntitySpitfireSkeleton.class, GIANT_FIT);
        fit(mobs, EntityGiantWitherSkeleton.class, GIANT_FIT);
        fit(mobs, EntitySpitfireWitherSkeleton.class, GIANT_FIT);
        fit(mobs, EntityGiantSpider.class, GIANT_SPIDER_FIT);
        fit(mobs, EntityHungrySpider.class, GIANT_SPIDER_FIT);
        fit(mobs, EntityMotherSpider.class, MOTHER_SPIDER_FIT);
        fit(mobs, EntityFighterGhast.class, SMALL_GHAST_FIT);
        fit(mobs, EntityUnholyGhast.class, SMALL_GHAST_FIT);
        fit(mobs, EntityQueenGhast.class, QUEEN_GHAST_FIT);
        fit(mobs, EntityKingGhast.class, KING_GHAST_FIT);

        SpecialMobsMod.log().info("JER: registered the drops of {} special variants", count);
    }

    /** @return The number of variants that were handed to JER. */
    public static int registered() {
        return registered;
    }

    /** @return False if the variant has no loot table of its own, and so nothing to show. */
    private static boolean register(IMobRegistry mobs, World world, Species species) {
        ResourceLocation lootTable = species.lootTable();
        if (lootTable == null) {
            return false;
        }
        EntityLiving entity = species.create(world);
        ISpecialMob mob = (ISpecialMob) entity;
        mob.getSpecialData().prepareForDisplay();

        MobFamily family = species.family();
        LightLevel light;
        if (family == MobFamily.BLAZE) {
            light = LightLevel.blaze;
        } else if (entity instanceof EntityMob && family != MobFamily.ZOMBIE_PIGMAN) {
            // Zombie pigmen are the one family of monsters that spawns in any light
            light = LightLevel.hostile;
        } else {
            light = LightLevel.any;
        }

        int experience = mob.getExperience();
        if (family == MobFamily.SLIME || family == MobFamily.MAGMA_CUBE) {
            // They drop their size (1, 2 or 4) plus the variant's bonus, which is all their accessor returns
            mobs.register(entity, light, 1 + experience, 4 + experience, lootTable);
        } else {
            mobs.register(entity, light, experience, lootTable);
        }
        return true;
    }

    /**
     * Shrinks a mob in JER's preview. JER ignores the scale of the render info, so the hook scales the model itself,
     * as JER's own hooks do. The scaling is around the mob's feet, which stay where they are.
     */
    private static void fit(IMobRegistry mobs, Class<? extends EntityLivingBase> entityClass, float scale) {
        IMobRenderHook<EntityLivingBase> hook = (info, entity) -> {
            GlStateManager.scale(scale, scale, scale);
            return info;
        };
        mobs.registerRenderHook(entityClass, hook);
    }

}
