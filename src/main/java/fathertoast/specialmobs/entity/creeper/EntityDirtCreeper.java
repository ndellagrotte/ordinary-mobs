/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.creeper;

import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityDirtCreeper extends Entity_SpecialCreeper {

    private static final ResourceLocation[] TEXTURES = { new ResourceLocation(getTexturePath("dirt")) };

    public EntityDirtCreeper(World world) {
        super(world);
        getSpecialData().setImmuneToBurning(true);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addCommonDrop("common", "Dirt", Blocks.DIRT);
        loot.addSemicommonDrop("semicommon", "Bread", Items.BREAD);
        loot.addRareDrop("rare", "Root veggie", Items.CARROT, Items.POTATO);
    }

    @Override
    public ResourceLocation[] getDefaultTextures() {
        return TEXTURES;
    }

    /** Called to modify the mob's attributes based on the variant. */
    @Override
    protected void applyTypeAttributes() {
        experienceValue += 1;

        getSpecialData().addAttribute(SharedMonsterAttributes.ARMOR, 6.0);
    }

    // The explosion caused by this creeper.
    @Override
    public void explodeByType(boolean powered, boolean griefing) {
        float power = (float) explosionRadius * (powered ? 2.0F : 1.0F);
        world.createExplosion(this, posX, posY, posZ, power, false);
        if (!griefing) {
            return;
        }

        IBlockState dirt = Blocks.DIRT.getDefaultState();
        int radius = (int) Math.floor(power);
        BlockPos center = new BlockPos(this);

        BlockPos pos;
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z <= radius * radius) {
                        pos = center.add(x, y, z);
                        if (dirt.getBlock().canPlaceBlockAt(world, pos)) {
                            world.setBlockState(pos, dirt, 2);
                        }
                    }
                }
            }
        }
    }

}
