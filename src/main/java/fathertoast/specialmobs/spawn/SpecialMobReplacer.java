/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.spawn;

import fathertoast.specialmobs.SpecialMobsMod;
import fathertoast.specialmobs.bestiary.MobFamily;
import fathertoast.specialmobs.bestiary.Species;
import fathertoast.specialmobs.config.Config;
import fathertoast.specialmobs.entity.ISpecialMob;
import fathertoast.specialmobs.spawn.SpawnInitInterceptor.Decision;
import org.jspecify.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Swaps vanilla mobs for this mod's mobs as they enter the world.
 * <p>
 * A vanilla mob is caught when it joins the world, which covers every way of spawning one. It is kept out of the
 * world, and at the end of the server tick a mob from this mod is spawned in its place with the same data. Waiting
 * for the end of the tick means nothing is added to the world while the world is busy adding or loading entities,
 * and lets the original pick up everything that is done to it right after it spawns, such as a spawn egg's name.
 */
public final class SpecialMobReplacer {

    /**
     * Marks a vanilla mob in its Forge data as already passed over, so it is not considered again. Only used while
     * replacing vanilla mobs is turned off for its family.
     */
    private static final String TAG_INIT = "SpecialMobsInit";

    private final Deque<Pending> pending = new ArrayDeque<>();
    private final Set<Entity> pendingOriginals = Collections.newSetFromMap(new IdentityHashMap<>());

    /**
     * Decides what should happen to a newly spawned mob.
     *
     * @return The decision, or null if the mob is none of this mod's business.
     */
    static @Nullable
    Decision decide(EntityLiving entity) {
        if (!Config.get().GENERAL.SPAWNING) {
            return null;
        }
        MobFamily family = MobFamily.replacing(entity);
        if (family == null) {
            return null;
        }
        boolean replaceVanilla = Config.get().family(family).REPLACE_VANILLA;
        if (!replaceVanilla && hasInitFlag(entity)) {
            return null;
        }
        World world = entity.world;
        float specialChance = WeightTable.get(family, world, new BlockPos(entity)).specialChance;
        if (world.rand.nextFloat() < specialChance) {
            return Decision.SPECIAL;
        }
        return replaceVanilla ? Decision.VANILLA_LIKE : Decision.KEEP;
    }

    private static boolean hasInitFlag(Entity entity) {
        NBTTagCompound forgeData = entity.getEntityData();
        return forgeData.hasKey(TAG_INIT, 99) && forgeData.getBoolean(TAG_INIT);
    }

    /** @return The replacement for a vanilla mob, not yet in the world, or null if the mob can not be replaced now. */
    private static @Nullable
    Swap createReplacement(Pending entry) {
        EntityLiving original = entry.original;
        World world = original.world;
        BlockPos pos = new BlockPos(original);
        if (original.isDead ||
            DimensionManager.getWorld(world.provider.getDimension()) != world ||
            !world.isBlockLoaded(pos)) {
            return null;
        }

        Species species = entry.special
            ? WeightTable.get(entry.family, world, pos).pickVariant(world.rand)
            : entry.family.base();
        EntityLiving replacement;
        try {
            replacement = species.create(world);
        } catch (RuntimeException ex) {
            SpecialMobsMod.log()
                .error("Encountered an exception while constructing entity '{}'", species.registryName(), ex);
            return null;
        }

        NBTTagCompound tag = original.writeToNBT(new NBTTagCompound());
        tag.removeTag("Passengers");
        replacement.readFromNBT(tag);

        // The copied data carries the vanilla mob's attributes, which wipes out the changes the variant made to its own
        if (replacement instanceof ISpecialMob specialMob) {
            specialMob.reapplyTypeAttributes();
        }

        // A mob whose initialization was held back is initialized here for the first and only time. Otherwise the
        // original has been initialized already, and only a special variant is initialized again to get its gear.
        if (entry.initDeferred || entry.special) {
            replacement.onInitialSpawn(world.getDifficultyForLocation(pos), null);
        }

        return new Swap(entry, replacement, original.getRidingEntity(), new ArrayList<>(original.getPassengers()));
    }

    /** Gets rid of a vanilla mob that has been replaced. It never made it into the world's entity list. */
    private static void discard(EntityLiving original) {
        original.dismountRidingEntity();
        original.removePassengers();
        original.setDead();
        if (original.addedToChunk) {
            Chunk chunk = original.world.getChunkProvider().getLoadedChunk(original.chunkCoordX, original.chunkCoordZ);
            if (chunk != null) {
                chunk.removeEntity(original);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote) {
            return;
        }
        Entity entity = event.getEntity();
        MobFamily family = MobFamily.replacing(entity);
        // Force-spawned entities are added no matter what this event says, so they can not be kept out of the world
        if (family == null || entity.forceSpawn || entity.isDead || pendingOriginals.contains(entity)) {
            return;
        }
        EntityLiving original = (EntityLiving) entity;

        if (entity.addedToChunk) {
            // Loaded with its chunk instead of newly spawned. It is not made special and not initialized again,
            // but a vanilla mob from before this mod was added still becomes the vanilla replacement.
            if (Config.get().GENERAL.SPAWNING && Config.get().family(family).REPLACE_VANILLA) {
                enqueue(new Pending(family, original, false, true, false));
                event.setCanceled(true);
            }
            return;
        }

        Decision decision = SpawnInitInterceptor.take(original);
        if (decision == null) {
            decision = decide(original);
            if (decision == null) {
                return;
            }
        }
        if (decision == Decision.KEEP) {
            entity.getEntityData().setBoolean(TAG_INIT, true);
            return;
        }
        enqueue(new Pending(family, original, decision == Decision.SPECIAL, false, decision.initDeferred()));
        event.setCanceled(true);
    }

    private void enqueue(Pending entry) {
        pending.addLast(entry);
        pendingOriginals.add(entry.original);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        // Initializing a replacement can spawn more mobs to replace, such as a jockey's rider, so go round again
        while (!pending.isEmpty()) {
            List<Pending> round = new ArrayList<>(pending);
            pending.clear();
            replaceAll(round);
        }
        pendingOriginals.clear();
        SpawnInitInterceptor.clear();
    }

    private void replaceAll(List<Pending> round) {
        List<Swap> swaps = new ArrayList<>(round.size());
        for (Pending entry : round) {
            Swap swap = createReplacement(entry);
            if (swap != null) {
                swaps.add(swap);
            }
        }

        // Originals are only taken out once every replacement exists, so riders and mounts can be matched up
        Map<Entity, Entity> replacementOf = new IdentityHashMap<>();
        for (Swap swap : swaps) {
            EntityLiving original = swap.pending.original;
            World world = original.world;
            if (!world.spawnEntity(swap.replacement)) {
                // Something refused the replacement. A mob loaded with its chunk is still saved with it, leave it be.
                continue;
            }
            replacementOf.put(original, swap.replacement);
            discard(original);
        }

        for (Swap swap : swaps) {
            Entity replacement = replacementOf.get(swap.pending.original);
            if (replacement == null) {
                continue;
            }
            if (swap.mount != null) {
                replacement.startRiding(replacementOf.getOrDefault(swap.mount, swap.mount), true);
            }
            for (Entity passenger : swap.passengers) {
                // A passenger that was replaced this round is put on its mount by its own swap
                if (!replacementOf.containsKey(passenger) && !passenger.isDead) {
                    passenger.startRiding(replacement, true);
                }
            }
        }
    }

    /** A vanilla mob waiting to be replaced. */
    private record Pending(MobFamily family,
        EntityLiving original,
        boolean special,
        boolean fromChunk,
        boolean initDeferred
    ) { }

    /** A replacement made this round, with what its original was riding and carrying at the time. */
    private record Swap(Pending pending, EntityLiving replacement, @Nullable Entity mount, List<Entity> passengers) { }

}
