/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.bestiary;

import fathertoast.specialmobs.entity.blaze.BlazeSpecies;
import fathertoast.specialmobs.entity.cavespider.CaveSpiderSpecies;
import fathertoast.specialmobs.entity.creeper.CreeperSpecies;
import fathertoast.specialmobs.entity.enderman.EndermanSpecies;
import fathertoast.specialmobs.entity.ghast.GhastSpecies;
import fathertoast.specialmobs.entity.lavaslime.LavaSlimeSpecies;
import fathertoast.specialmobs.entity.pigzombie.PigZombieSpecies;
import fathertoast.specialmobs.entity.silverfish.SilverfishSpecies;
import fathertoast.specialmobs.entity.skeleton.SkeletonSpecies;
import fathertoast.specialmobs.entity.slime.SlimeSpecies;
import fathertoast.specialmobs.entity.spider.SpiderSpecies;
import fathertoast.specialmobs.entity.witch.WitchSpecies;
import fathertoast.specialmobs.entity.witherskeleton.WitherSkeletonSpecies;
import fathertoast.specialmobs.entity.zombie.ZombieSpecies;
import org.jspecify.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The vanilla mobs this mod adds variants of. Each family has one vanilla replacement species, which behaves like
 * the vanilla mob, and a number of special variants.
 */
public enum MobFamily {

    CREEPER("Creeper", "creepers", 0x0DA70B, CreeperSpecies::declare, EntityCreeper.class),

    ZOMBIE("Zombie", "zombies", 0x00AFAF, ZombieSpecies::declare, EntityZombie.class, EntityHusk.class),
    ZOMBIE_PIGMAN("PigZombie", "zombie pigmen", 0xEA9393, PigZombieSpecies::declare, EntityPigZombie.class),

    SKELETON("Skeleton", "skeletons", 0xC1C1C1, SkeletonSpecies::declare, EntitySkeleton.class, EntityStray.class),
    WITHER_SKELETON("WitherSkeleton", "wither skeletons", 0x141414, WitherSkeletonSpecies::declare,
        EntityWitherSkeleton.class
    ),

    SLIME("Slime", "slimes", 0x51A03E, SlimeSpecies::declare, EntitySlime.class),
    MAGMA_CUBE("LavaSlime", "magma cubes", 0x340000, LavaSlimeSpecies::declare, EntityMagmaCube.class),

    SPIDER("Spider", "spiders", 0x342D27, SpiderSpecies::declare, EntitySpider.class),
    CAVE_SPIDER("CaveSpider", "cave spiders", 0x0C424E, CaveSpiderSpecies::declare, EntityCaveSpider.class),

    SILVERFISH("Silverfish", "silverfish", 0x6E6E6E, SilverfishSpecies::declare, EntitySilverfish.class),

    ENDERMAN("Enderman", "endermen", 0x161616, EndermanSpecies::declare, EntityEnderman.class),

    WITCH("Witch", "witches", 0x340000, WitchSpecies::declare, EntityWitch.class),

    GHAST("Ghast", "ghasts", 0xF9F9F9, GhastSpecies::declare, EntityGhast.class),

    BLAZE("Blaze", "blazes", 0xF6B201, BlazeSpecies::declare, EntityBlaze.class);

    private static final Map<Class<?>, MobFamily> FAMILY_BY_VANILLA_CLASS = new IdentityHashMap<>();
    private static final Map<Class<?>, Species> SPECIES_BY_ENTITY_CLASS = new IdentityHashMap<>();

    static {
        for (MobFamily family : values()) {
            for (Class<?> replaceable : family.replaceableClasses) {
                FAMILY_BY_VANILLA_CLASS.put(replaceable, family);
            }
            for (Species species : family.allSpecies()) {
                SPECIES_BY_ENTITY_CLASS.put(species.entityClass(), species);
            }
        }
    }

    /** The family name as used in entity names, such as "PigZombie". */
    public final String name;
    /** The plural, human-readable name. With spaces replaced by underscores, it is the family's config category. */
    public final String key;
    /** The base colour of this family's spawn eggs. */
    public final int eggBaseColor;
    /** The network id of the vanilla replacement. The variants follow it one by one. */
    public final int firstNetworkId;
    /** The names of this family's special variants, in network id order. */
    private final String[] variantNames;

    private final Class<?>[] replaceableClasses;

    private final @Nullable Species base;
    private final Species[] variants;

    MobFamily(String name, String key, int eggBaseColor, Consumer<SpeciesList> declaration,
        Class<?>... replaceableClasses
    ) {
        this.name = name;
        this.key = key;
        this.eggBaseColor = eggBaseColor;
        this.replaceableClasses = replaceableClasses;
        firstNetworkId = LegacyIds.firstNetworkId(name);
        variantNames = LegacyIds.variantNames(name);

        SpeciesList declared = new SpeciesList(this, variantNames);
        declaration.accept(declared);
        base = declared.base();
        Species[] declaredVariants = declared.variants();
        variants = declaredVariants == null ? new Species[0] : declaredVariants;
    }

    /**
     * @return The family that replaces an entity, or null if it does not replace it. Only exact vanilla classes are
     *     replaced, so mobs from other mods that extend them are left alone.
     */
    public static @Nullable
    MobFamily replacing(Entity entity) {
        return FAMILY_BY_VANILLA_CLASS.get(entity.getClass());
    }

    /** @return The species of one of this mod's entity classes, or of the nearest superclass that is one. */
    public static Species speciesOf(Class<?> entityClass) {
        for (Class<?> c = entityClass; c != null; c = c.getSuperclass()) {
            Species species = SPECIES_BY_ENTITY_CLASS.get(c);
            if (species != null) {
                return species;
            }
        }
        throw new IllegalArgumentException(entityClass.getName() + " is not an entity from Special Mobs");
    }

    /** @return The names of this family's special variants, in network id order. */
    public String[] variantNames() {
        return variantNames.clone();
    }

    /** @return The species that stands in for the vanilla mob. */
    public Species base() {
        if (base == null) {
            throw new IllegalStateException("Mob family '" + name + "' has no species yet");
        }
        return base;
    }

    /** @return True once this family's species are declared. Only false while a family is not implemented yet. */
    public boolean isAvailable() {
        return base != null;
    }

    /** @return The special variants, in network id order. */
    public List<Species> variants() {
        return List.of(variants);
    }

    /** @return The special variant at a position in {@link #variants()}. */
    public Species variant(int index) {
        return variants[index];
    }

    /** @return The number of special variants this family has available. */
    public int variantCount() {
        return variants.length;
    }

    /** @return The vanilla replacement followed by the special variants, in network id order. */
    public List<Species> allSpecies() {
        if (base == null) {
            return List.of();
        }
        Species[] all = new Species[variants.length + 1];
        all[0] = base;
        System.arraycopy(variants, 0, all, 1, variants.length);
        return List.of(all);
    }

}
