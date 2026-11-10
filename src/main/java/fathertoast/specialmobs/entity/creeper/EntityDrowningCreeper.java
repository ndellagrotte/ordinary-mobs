/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.creeper;

import fathertoast.specialmobs.loot.LootEntryItemBuilder;
import fathertoast.specialmobs.loot.LootPoolBuilder;
import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityDrowningCreeper extends Entity_SpecialCreeper {

    private static final ResourceLocation[] TEXTURES = {
        new ResourceLocation(getTexturePath("drowning")),
        new ResourceLocation(getTexturePath("drowning_eyes"))
    };

    public EntityDrowningCreeper(World world) {
        super(world);
        getSpecialData().setImmuneToBurning(true);
        getSpecialData().setCanBreatheInWater(true);
        getSpecialData().setIgnoreWaterPush(true);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addPool(new LootPoolBuilder("common")
                .addEntry(new LootEntryItemBuilder("Fish", Items.FISH)
                        .setCount(0, 2)
                        .addLootingBonus(0, 1)
                        .smeltIfBurning()
                        .toLootEntry()
                )
                .toLootPool()
        );
        loot.addUncommonDrop("uncommon", "Undersea loot", Items.GOLD_NUGGET, Items.PRISMARINE_SHARD,
            Items.PRISMARINE_CRYSTALS
        );
    }

    @Override
    public ResourceLocation[] getDefaultTextures() {
        return TEXTURES;
    }

    /** Called to modify the mob's attributes based on the variant. */
    @Override
    protected void applyTypeAttributes() {
        experienceValue += 2;

        getSpecialData().addAttribute(SharedMonsterAttributes.MAX_HEALTH, 10.0);
    }

    // The explosion caused by this creeper.
    @Override
    public void explodeByType(boolean powered, boolean griefing) {
        float power = (float) explosionRadius * (powered ? 2.0F : 1.0F) + 3.0F;
        world.createExplosion(this, posX, posY, posZ, griefing ? 1.0F : power, false);
        if (!griefing) {
            return;
        }

        IBlockState cobblestone = Blocks.COBBLESTONE.getDefaultState();
        IBlockState silverfish = Blocks.MONSTER_EGG
            .getDefaultState()
            .withProperty(BlockSilverfish.VARIANT, BlockSilverfish.EnumType.COBBLESTONE);
        IBlockState water = Blocks.WATER.getDefaultState();
        int radius = (int) Math.floor(power);
        int rMinusOneSq = (radius - 1) * (radius - 1);
        BlockPos center = new BlockPos(this);

        BlockPos pos;
        int distSq;
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    distSq = x * x + y * y + z * z;
                    if (distSq <= radius * radius) {
                        pos = center.add(x, y, z);
                        // Cobblestone casing
                        if (distSq > rMinusOneSq) {
                            if (cobblestone.getBlock().canPlaceBlockAt(world, pos)) {
                                if (rand.nextFloat() < 0.25F) {
                                    world.setBlockState(pos, silverfish, 2);
                                } else {
                                    world.setBlockState(pos, cobblestone, 2);
                                }
                            }
                        }
                        // Water fill
                        else if (water.getBlock().canPlaceBlockAt(world, pos)) {
                            world.setBlockState(pos, water, 2);
                        }
                    }
                }
            }
        }
    }

}
