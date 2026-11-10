/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.loot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fathertoast.specialmobs.SpecialMobsMod;
import fathertoast.specialmobs.bestiary.MobFamily;
import fathertoast.specialmobs.bestiary.Species;

import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Writes the loot table files that ship in this mod's resources from the loot each species declares in code.
 * <p>
 * This is a development tool. It only runs when the game is started with
 * {@code -Dspecialmobs.generateLoot=<directory>}, and writes to
 * {@code <directory>/<family>/<variant>.json}. To update the shipped tables, point it at
 * {@code src/main/resources/assets/specialmobs/loot_tables/entities}.
 */
public final class LootTableGenerator {

    /** The system property that names the directory to generate into. */
    public static final String PROPERTY = "specialmobs.generateLoot";

    private static final Gson GSON_LOOT_TABLES = new GsonBuilder()
        .registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer())
        .registerTypeAdapter(LootPool.class, new LootPool.Serializer())
        .registerTypeAdapter(LootTable.class, new LootTable.Serializer())
        .registerTypeHierarchyAdapter(LootEntry.class, new LootEntry.Serializer())
        .registerTypeHierarchyAdapter(LootFunction.class, new LootFunctionManager.Serializer())
        .registerTypeHierarchyAdapter(LootCondition.class, new LootConditionManager.Serializer())
        .registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer())
        .setPrettyPrinting()
        .create();

    private LootTableGenerator() { }

    /** Generates the loot tables if the system property asks for it. Items have to be registered by now. */
    public static void runIfRequested() {
        String directory = System.getProperty(PROPERTY);
        if (directory == null || directory.isEmpty()) {
            return;
        }
        Path lootTablesDir = Path.of(directory).toAbsolutePath();
        SpecialMobsMod.log().warn("Generating loot tables into '{}'", lootTablesDir);
        int generated = 0;
        for (MobFamily family : MobFamily.values()) {
            for (Species variant : family.variants()) {
                Consumer<LootTableBuilder> lootBuilder = variant.lootBuilder();
                if (lootBuilder == null) {
                    continue;
                }
                Path file = lootTablesDir.resolve(family.name.toLowerCase(Locale.ROOT))
                    .resolve(variant.key() + ".json");
                try {
                    LootTableBuilder builder = new LootTableBuilder();
                    lootBuilder.accept(builder);
                    Files.createDirectories(file.getParent());
                    try (Writer out = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                        GSON_LOOT_TABLES.toJson(builder.toLootTable(), LootTable.class, out);
                    }
                    generated++;
                } catch (IOException | RuntimeException ex) {
                    SpecialMobsMod.log().error("Failed to generate loot table: '{}'", file, ex);
                }
            }
        }
        SpecialMobsMod.log().warn("Generated {} loot tables", generated);
    }

}
