/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.spawn;

import fathertoast.specialmobs.bestiary.MobFamily;
import fathertoast.specialmobs.config.Config;
import org.jspecify.annotations.Nullable;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Lets a mob that is about to be replaced skip its spawn initialization, so that only its replacement is
 * initialized.
 * <p>
 * Vanilla initializes a mob (equipment, baby chance, jockeys and so on) just before adding it to the world, which
 * is where the replacer first sees it. Without this, a replaced mob is initialized and then thrown away, and its
 * replacement initialized all over again. The natural spawner and mob spawners ask here first, through mixins.
 * If those mixins are not in effect, nothing is ever recorded here and the replacer decides on its own.
 * <p>
 * Only the server thread uses this.
 */
public final class SpawnInitInterceptor {

    private static final Map<EntityLiving, Decision> DECISIONS = new IdentityHashMap<>();

    private static int deferredCount;

    private SpawnInitInterceptor() { }

    /**
     * Called right before a mob would be initialized on spawning.
     *
     * @return True if the mob will be replaced and its initialization should be skipped.
     */
    public static boolean shouldDeferInit(EntityLiving entity) {
        if (entity.world.isRemote) {
            return false;
        }
        Decision decision = SpecialMobReplacer.decide(entity);
        if (decision == null) {
            return false;
        }
        DECISIONS.put(entity, decision);
        if (decision.initDeferred()) {
            deferredCount++;
        }
        return decision.initDeferred();
    }

    /** @return How many initializations have been skipped so far. Lets the self-test see that the mixins are in effect. */
    public static int deferredCount() {
        return deferredCount;
    }

    /** @return What was decided for a mob when it was about to be initialized, or null if it was not seen then. */
    static @Nullable
    Decision take(EntityLiving entity) {
        return DECISIONS.isEmpty() ? null : DECISIONS.remove(entity);
    }

    /** Forgets decisions for mobs that never made it into the world. */
    static void clear() {
        if (!DECISIONS.isEmpty()) {
            DECISIONS.clear();
        }
    }

    /** What has been decided for a mob between its initialization and it joining the world. */
    public enum Decision {

        /** Becomes a special variant. Its initialization was skipped. */
        SPECIAL,
        /** Becomes the vanilla replacement. Its initialization was skipped. */
        VANILLA_LIKE,
        /** Stays as it is. It was initialized as usual. */
        KEEP;

        /** @return True if the mob's own initialization was skipped, leaving it to the replacement. */
        public boolean initDeferred() {
            return this != KEEP;
        }

    }

}
