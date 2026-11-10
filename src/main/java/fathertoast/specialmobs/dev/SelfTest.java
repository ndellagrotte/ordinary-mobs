/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.dev;

import fathertoast.specialmobs.SpecialMobsMod;
import fathertoast.specialmobs.bestiary.MobFamily;
import fathertoast.specialmobs.bestiary.Species;
import fathertoast.specialmobs.entity.ISpecialMob;
import fathertoast.specialmobs.entity.MobHelper;
import fathertoast.specialmobs.entity.SpecialMobData;
import fathertoast.specialmobs.spawn.SpawnInitInterceptor;
import org.jspecify.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * An in-game check of the things that can only be seen with a world running: that every species can be spawned,
 * saved and loaded, and that vanilla mobs are replaced the way they should be.
 * <p>
 * This is a development tool. It only runs when the game is started with {@code -Dspecialmobs.selfTest=true}, for
 * example with {@code gradlew runClient -Pextra_jvm_args=-Dspecialmobs.selfTest=true}. The client then opens a
 * fresh flat world by itself, the checks run on the server side of that world, the results go to the log, and the
 * game closes. The last line it logs starts with {@code SELFTEST RESULT}.
 */
public final class SelfTest {

    /** The system property that turns the self-test on. */
    public static final String PROPERTY = "specialmobs.selfTest";

    /** How many ticks to watch natural spawning for before filling the world with test mobs. */
    private static final int NATURAL_SPAWN_TICKS = 300;

    /** The texture one mob is given through nbt. The client checks that it hears about it. */
    public static final String CUSTOM_TEXTURE = "minecraft:textures/entity/zombie/husk.png";

    private static final List<String> FAILURES = new ArrayList<>();

    private static int checks;

    /** Set once the server side is done, which lets the client finish up and close the game. */
    public static volatile boolean serverFinished;
    /** The network id of the mob with the custom texture, or -1 while there is none. */
    public static volatile int customTextureEntityId = -1;

    private int ticks = -1;

    private final List<Entity> summoned = new ArrayList<>();
    private final List<Species> summonedSpecies = new ArrayList<>();
    private final List<EntityLiving> originals = new ArrayList<>();
    private final List<EntityLiving> chunkLoaded = new ArrayList<>();

    private @Nullable EntityLiving jockeySpider;
    private @Nullable EntityLiving jockeySkeleton;
    private @Nullable EntityLiving chickenRider;
    private @Nullable EntityChicken chicken;
    private BlockPos origin = BlockPos.ORIGIN;

    private SelfTest() { }

    /** @return True if the game was started with the self-test turned on. */
    public static boolean isRequested() {
        return Boolean.getBoolean(PROPERTY);
    }

    /** Starts the server side of the self-test. */
    public static void register() {
        MinecraftForge.EVENT_BUS.register(new SelfTest());
    }

    /** Records the outcome of one check. */
    public static synchronized void check(String name, boolean passed, String detail) {
        checks++;
        if (passed) {
            SpecialMobsMod.log().info("[selftest] PASS {}", name);
        } else {
            FAILURES.add(name);
            SpecialMobsMod.log().error("[selftest] FAIL {}: {}", name, detail);
        }
    }

    /** Logs the final result. */
    public static synchronized void logResult() {
        if (FAILURES.isEmpty()) {
            SpecialMobsMod.log().info("SELFTEST RESULT: PASSED all {} checks", checks);
        } else {
            SpecialMobsMod.log()
                .error("SELFTEST RESULT: FAILED {} of {} checks: {}", FAILURES.size(), checks, FAILURES);
        }
    }

    // Runs after the replacer, which also acts at the end of the tick
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || serverFinished) {
            return;
        }
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null || server.getPlayerList().getPlayers().isEmpty()) {
            return;
        }
        WorldServer world = server.getWorld(0);
        EntityPlayerMP player = server.getPlayerList().getPlayers().get(0);
        ticks++;
        try {
            step(server, world, player);
        } catch (RuntimeException ex) {
            SpecialMobsMod.log().error("[selftest] step at tick {} threw", ticks, ex);
            check("no exception at tick " + ticks, false, ex.toString());
            serverFinished = true;
        }
    }

    private void step(MinecraftServer server, WorldServer world, EntityPlayerMP player) {
        int afterNatural = ticks - NATURAL_SPAWN_TICKS;
        if (ticks == 0) {
            server.setDifficultyForAllWorlds(EnumDifficulty.NORMAL);
            world.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
            world.setWorldTime(18000);
            origin = new BlockPos(player);
            player.connection.setPlayerLocation(origin.getX() + 0.5, origin.getY(), origin.getZ() + 0.5, 0.0F, 15.0F);
        } else if (afterNatural == 0) {
            checkNaturalSpawning(world);
            placeSpawner(world);
        } else if (afterNatural == 60) {
            checkSpawner(world);
            summonEverySpecies(world);
        } else if (afterNatural == 80) {
            checkSummoned(world);
            giveCustomTexture();
            checkHelpers(world, player);
        } else if (afterNatural == 100) {
            spawnVanillaMobs(world);
        } else if (afterNatural == 103) {
            checkReplacements(world);
            checkNoVanillaMobsLeft(world);
            for (Entity entity : summoned) {
                ((EntityLiving) entity).setNoAI(false);
            }
        } else if (afterNatural == 220) {
            // The mobs have had a while to run their AI. Anything that throws there crashes the game instead.
            check("mobs ran their AI without crashing", true, "");
            serverFinished = true;
        }
    }

    private void checkNaturalSpawning(WorldServer world) {
        int special = 0;
        for (Entity entity : world.loadedEntityList) {
            if (entity instanceof ISpecialMob) {
                special++;
            }
        }
        check("natural spawns produced this mod's mobs", special > 0, "none found after " + ticks + " ticks");
        check("natural spawn initialization was deferred to the replacement", SpawnInitInterceptor.deferredCount() > 0,
            "the spawner mixin never deferred an initialization"
        );
        checkNoVanillaMobsLeft(world);
    }

    private int deferredBeforeSpawner;

    private void placeSpawner(WorldServer world) {
        deferredBeforeSpawner = SpawnInitInterceptor.deferredCount();
        // Natural spawning would muddy the count, so it is turned off from here on
        world.getGameRules().setOrCreateGameRule("doMobSpawning", "false");
        BlockPos pos = origin.add(-12, 0, -12);
        world.setBlockState(pos, Blocks.MOB_SPAWNER.getDefaultState());
        TileEntity spawner = world.getTileEntity(pos);
        if (spawner == null) {
            check("mob spawner placed", false, "no tile entity");
            return;
        }
        NBTTagCompound tag = spawner.writeToNBT(new NBTTagCompound());
        NBTTagCompound spawnData = new NBTTagCompound();
        spawnData.setString("id", "minecraft:zombie");
        tag.setTag("SpawnData", spawnData);
        tag.removeTag("SpawnPotentials");
        tag.setShort("Delay", (short) 2);
        tag.setShort("MinSpawnDelay", (short) 5);
        tag.setShort("MaxSpawnDelay", (short) 10);
        tag.setShort("SpawnCount", (short) 4);
        tag.setShort("MaxNearbyEntities", (short) 60);
        tag.setShort("RequiredPlayerRange", (short) 64);
        spawner.readFromNBT(tag);
    }

    private void checkSpawner(WorldServer world) {
        check("mob spawner initialization was deferred to the replacement",
            SpawnInitInterceptor.deferredCount() > deferredBeforeSpawner,
            "the mob spawner mixin never deferred an initialization"
        );
        world.setBlockToAir(origin.add(-12, 0, -12));
    }

    private void summonEverySpecies(WorldServer world) {
        int row = 0;
        for (MobFamily family : MobFamily.values()) {
            int column = 0;
            for (Species species : family.allSpecies()) {
                double x = origin.getX() + (column - 6) * 3 + 0.5;
                double z = origin.getZ() + 10 + row * 4 + 0.5;
                EntityLiving mob = species.create(world);
                mob.setLocationAndAngles(x, origin.getY(), z, 180.0F, 0.0F);
                mob.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(mob)), null);
                mob.setNoAI(true);
                mob.enablePersistence();
                boolean spawned = world.spawnEntity(mob);
                check("summon " + species.entityName(), spawned, "spawnEntity returned false");
                if (spawned) {
                    summoned.add(mob);
                    summonedSpecies.add(species);
                }
                column++;
            }
            row++;
        }
    }

    private void checkSummoned(WorldServer world) {
        for (int i = 0; i < summoned.size(); i++) {
            Entity mob = summoned.get(i);
            Species species = summonedSpecies.get(i);
            String name = species.entityName();

            ResourceLocation key = EntityList.getKey(mob);
            check("registry name of " + name, species.registryName().equals(key), "was " + key);

            NBTTagCompound tag = new NBTTagCompound();
            boolean saved = mob.writeToNBTOptional(tag);
            Entity copy = saved ? EntityList.createEntityFromNBT(tag, world) : null;
            check("save and load " + name, copy != null && copy.getClass() == mob.getClass(),
                saved
                    ? "loaded as " + (copy == null ? null : copy.getClass().getName())
                    : "did not save, dead=" + mob.isDead
            );

            ResourceLocation lootTable = species.lootTable();
            if (lootTable != null) {
                check("loot table of " + name,
                    world.getLootTableManager().getLootTableFromLocation(lootTable) != LootTable.EMPTY_LOOT_TABLE,
                    lootTable + " is missing or empty"
                );
            }
        }
    }

    private void checkHelpers(WorldServer world, EntityPlayerMP player) {
        EntityLiving attacker = (EntityLiving) summoned.get(0);

        EntityPig hurtPig = new EntityPig(world);
        hurtPig.setLocationAndAngles(origin.getX() - 20.5, origin.getY(), origin.getZ() + 0.5, 0.0F, 0.0F);
        world.spawnEntity(hurtPig);
        MobHelper.dealDirectDamage(attacker, hurtPig, 2.0F);
        check("direct damage takes health",
            hurtPig.isEntityAlive() && hurtPig.getHealth() == hurtPig.getMaxHealth() - 2.0F,
            "health is " + hurtPig.getHealth()
        );

        EntityPig dyingPig = new EntityPig(world);
        dyingPig.setLocationAndAngles(origin.getX() - 22.5, origin.getY(), origin.getZ() + 0.5, 0.0F, 0.0F);
        world.spawnEntity(dyingPig);
        dyingPig.setHealth(1.0F);
        MobHelper.dealDirectDamage(attacker, dyingPig, 2.0F);
        check("lethal direct damage is a proper kill",
            !dyingPig.isEntityAlive() &&
                dyingPig.getLastDamageSource() != null &&
                dyingPig.getLastDamageSource().getTrueSource() == attacker,
            "alive=" + dyingPig.isEntityAlive() + ", last damage " + dyingPig.getLastDamageSource()
        );

        // Throws if the accessor mixin is not in effect
        MobHelper.resetFloatingCheck(player);
        check("flying check can be reset", true, "");
    }

    private void giveCustomTexture() {
        for (Entity mob : summoned) {
            if (mob.getClass().getSimpleName().equals("EntityFireZombie")) {
                NBTTagCompound tag = mob.writeToNBT(new NBTTagCompound());
                SpecialMobData.getSaveLocation(tag).setString("Texture", CUSTOM_TEXTURE);
                mob.readFromNBT(tag);
                customTextureEntityId = mob.getEntityId();
                return;
            }
        }
        check("custom texture mob found", false, "no fire zombie was summoned");
    }

    private void spawnVanillaMobs(WorldServer world) {
        int row = 0;
        for (MobFamily family : MobFamily.values()) {
            for (Class<?> vanillaClass : family.replaceableClasses()) {
                for (int i = 0; i < 6; i++) {
                    EntityLiving mob = newVanillaMob(world, vanillaClass, -30 - row * 3, i * 3);
                    mob.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(mob)), null);
                    originals.add(mob);
                    world.spawnEntity(mob);
                }
                row++;
            }
        }

        // A spider jockey: both are replaced, and the new skeleton has to end up on the new spider
        jockeySpider = newVanillaMob(world, EntitySpider.class, -30, -6);
        jockeySkeleton = newVanillaMob(world, EntitySkeleton.class, -30, -6);
        world.spawnEntity(jockeySpider);
        world.spawnEntity(jockeySkeleton);
        jockeySkeleton.startRiding(jockeySpider, true);

        // A chicken jockey: only the zombie is replaced, and its replacement has to end up on the same chicken
        chicken = new EntityChicken(world);
        chicken.setLocationAndAngles(origin.getX() - 36.5, origin.getY(), origin.getZ() - 6.5, 0.0F, 0.0F);
        world.spawnEntity(chicken);
        EntityZombie babyZombie = (EntityZombie) newVanillaMob(world, EntityZombie.class, -36, -6);
        babyZombie.setChild(true);
        chickenRider = babyZombie;
        world.spawnEntity(babyZombie);
        babyZombie.startRiding(chicken, true);

        // Mobs that come in with their chunk: these get the vanilla replacement and must keep what they have
        for (int i = 0; i < 5; i++) {
            EntityZombie mob = (EntityZombie) newVanillaMob(world, EntityZombie.class, -42, i * 3);
            mob.setChild(true);
            mob.setCustomNameTag("Loaded " + i);
            mob.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_HOE));
            Chunk chunk = world.getChunk(new BlockPos(mob));
            chunk.addEntity(mob);
            chunkLoaded.add(mob);
        }
        world.loadEntities(new ArrayList<>(chunkLoaded));
    }

    private EntityLiving newVanillaMob(WorldServer world, Class<?> vanillaClass, int dx, int dz) {
        EntityLiving mob = (EntityLiving) EntityList.newEntity(vanillaClass.asSubclass(Entity.class), world);
        mob.setLocationAndAngles(origin.getX() + dx + 0.5, origin.getY(), origin.getZ() + dz + 0.5, 0.0F, 0.0F);
        mob.enablePersistence();
        return mob;
    }

    private void checkReplacements(WorldServer world) {
        int special = 0;
        int vanillaLike = 0;
        int wrong = 0;
        StringBuilder problems = new StringBuilder();
        for (EntityLiving original : originals) {
            Entity replacement = world.getEntityFromUuid(original.getUniqueID());
            MobFamily family = MobFamily.replacing(original);
            if (!original.isDead || !(replacement instanceof ISpecialMob mob) || mob.getSpecies().family() != family) {
                wrong++;
                problems.append(original.getClass().getSimpleName()).append(" -> ").append(replacement).append("; ");
                continue;
            }
            EntityLiving living = (EntityLiving) replacement;
            if (mob.getSpecies().isSpecial()) {
                special++;
                // Slimes derive their attributes from their size, which is random
                if (!(living instanceof EntitySlime)) {
                    EntityLiving fresh = mob.getSpecies().create(world);
                    double expected = fresh.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue();
                    double actual = living.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue();
                    if (expected != actual || living.getHealth() != living.getMaxHealth()) {
                        wrong++;
                        problems.append(mob.getSpecies().entityName())
                            .append(" has ")
                            .append(living.getHealth())
                            .append('/')
                            .append(actual)
                            .append(" health, expected base ")
                            .append(expected)
                            .append("; ");
                    }
                }
            } else {
                vanillaLike++;
            }
        }
        check("every vanilla mob was replaced by one of its family", wrong == 0, problems.toString());
        check("replacements are a mix of special and vanilla-like", special > 0 && vanillaLike > 0,
            special + " special, " + vanillaLike + " vanilla-like"
        );

        Entity newSpider = jockeySpider == null ? null : world.getEntityFromUuid(jockeySpider.getUniqueID());
        Entity newSkeleton = jockeySkeleton == null ? null : world.getEntityFromUuid(jockeySkeleton.getUniqueID());
        check("spider jockey keeps exactly its rider",
            newSpider instanceof ISpecialMob &&
                newSkeleton instanceof ISpecialMob &&
                newSkeleton.getRidingEntity() == newSpider &&
                newSpider.getPassengers().size() == 1,
            "spider " + newSpider + " carries " + (newSpider == null ? null : newSpider.getPassengers()) +
                ", skeleton " + newSkeleton + " rides " + (newSkeleton == null ? null : newSkeleton.getRidingEntity())
        );

        Entity newRider = chickenRider == null ? null : world.getEntityFromUuid(chickenRider.getUniqueID());
        check("chicken jockey keeps exactly its rider",
            newRider instanceof ISpecialMob &&
                chicken != null &&
                newRider.getRidingEntity() == chicken &&
                chicken.getPassengers().size() == 1,
            "rider " + newRider + ", chicken carries " + (chicken == null ? null : chicken.getPassengers())
        );

        int loadedWrong = 0;
        StringBuilder loadedProblems = new StringBuilder();
        for (EntityLiving original : chunkLoaded) {
            Entity replacement = world.getEntityFromUuid(original.getUniqueID());
            Chunk chunk = world.getChunk(new BlockPos(original));
            boolean stillInChunk = false;
            for (var entities : chunk.getEntityLists()) {
                stillInChunk |= entities.contains(original);
            }
            boolean ok = replacement instanceof ISpecialMob mob &&
                !mob.getSpecies().isSpecial() &&
                replacement instanceof EntityZombie zombie &&
                zombie.isChild() &&
                original.getCustomNameTag().equals(zombie.getCustomNameTag()) &&
                zombie.getHeldItemMainhand().getItem() == Items.GOLDEN_HOE &&
                !stillInChunk &&
                original.isDead;
            if (!ok) {
                loadedWrong++;
                loadedProblems.append(original.getCustomNameTag())
                    .append(" -> ")
                    .append(replacement)
                    .append(stillInChunk ? " (original still in chunk)" : "")
                    .append("; ");
            }
        }
        check("mobs loaded with their chunk are swapped without being changed", loadedWrong == 0,
            loadedProblems.toString()
        );
    }

    private static void checkNoVanillaMobsLeft(WorldServer world) {
        List<String> left = new ArrayList<>();
        for (Entity entity : world.loadedEntityList) {
            if (MobFamily.replacing(entity) != null) {
                left.add(entity.getClass().getSimpleName());
            }
        }
        check("no replaceable vanilla mobs are in the world", left.isEmpty(), left.toString());
    }

}
