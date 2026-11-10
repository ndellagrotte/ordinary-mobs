/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs;

import fathertoast.specialmobs.bestiary.LegacyIds;
import fathertoast.specialmobs.bestiary.MobFamily;
import fathertoast.specialmobs.bestiary.Species;
import fathertoast.specialmobs.config.BlockTarget;
import fathertoast.specialmobs.config.Config;
import fathertoast.specialmobs.dev.SelfTest;
import fathertoast.specialmobs.loot.LootTableGenerator;
import fathertoast.specialmobs.network.SpecialMobsNetwork;
import fathertoast.specialmobs.spawn.SpecialMobReplacer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.io.File;

@Mod(modid = SpecialMobsMod.MOD_ID, name = SpecialMobsMod.NAME, version = SpecialMobsMod.VERSION,
    acceptedMinecraftVersions = "[1.12.2]", guiFactory = "fathertoast.specialmobs.client.SpecialMobsGuiFactory"
)
@Mod.EventBusSubscriber(modid = SpecialMobsMod.MOD_ID)
public class SpecialMobsMod {

    /** The mod id. This is a compatibility contract with saved worlds and pack configs, so it is not a build token. */
    public static final String MOD_ID = "specialmobs";
    public static final String NAME = "Special Mobs";
    public static final String VERSION = "@{MOD_VERSION}";

    /** The namespace used by this mod. */
    public static final String NAMESPACE = MOD_ID + ":";
    /** The path to the entity textures folder. */
    public static final String TEXTURE_PATH = NAMESPACE + "textures/entity/";

    /** The name of the config file, unchanged from the original mod. */
    private static final String CONFIG_FILE_NAME = "Special_Mobs.cfg";

    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    /** Handles what differs between the client and the dedicated server. */
    @SidedProxy(modId = MOD_ID, clientSide = "fathertoast.specialmobs.client.ClientProxy",
        serverSide = "fathertoast.specialmobs.CommonProxy"
    ) public static CommonProxy proxy;

    /** @return The logger used by this mod. */
    public static Logger log() {
        return LOGGER;
    }

    /** Registers this mod's entities, along with their loot tables and spawn eggs. */
    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        boolean spawnEggs = Config.get().GENERAL.SPAWN_EGGS;
        int variants = 0;
        for (MobFamily family : MobFamily.values()) {
            for (Species species : family.allSpecies()) {
                EntityEntryBuilder<EntityLiving> entry = EntityEntryBuilder.<EntityLiving>create()
                    .entity(species.entityClass())
                    .factory(species::create)
                    .id(species.registryName(), species.networkId())
                    .name(species.entityName())
                    .tracker(80, 3, true);
                if (species.isSpecial()) {
                    variants++;
                    if (spawnEggs) {
                        entry.egg(family.eggBaseColor, species.info().eggSpotsColor());
                    }
                }
                event.getRegistry().register(entry.build());

                ResourceLocation lootTable = species.lootTable();
                if (lootTable != null) {
                    LootTableList.register(lootTable);
                }
            }
        }
        log().info("Registered {} special variants!", variants);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Config.load(log(), new File(event.getModConfigurationDirectory(), CONFIG_FILE_NAME));

        SpecialMobsNetwork.init();
        proxy.registerRenderers();

        // The configured blocks are added back during init, once every mod's blocks exist
        for (Block block : ForgeRegistries.BLOCKS) {
            if (EntityEnderman.getCarriable(block)) {
                EntityEnderman.setCarriable(block, false);
            }
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        for (Block block : BlockTarget.resolveBlocks(Config.get().ENDERMEN.CARRIABLE_BLOCKS, log()::error)) {
            EntityEnderman.setCarriable(block, true);
        }

        proxy.init();
        MinecraftForge.EVENT_BUS.register(new SpecialMobReplacer());
        LootTableGenerator.runIfRequested();
        if (SelfTest.isRequested()) {
            SelfTest.register();
        }
    }

}
