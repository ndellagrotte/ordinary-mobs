/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.config;

import org.jspecify.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.function.Consumer;

/**
 * One line of a block list in the config: {@code mod_id:block_id}, {@code mod_id:block_id meta} or the wildcard
 * {@code mod_id:*}, which stands for every block whose id starts with what comes before the star.
 *
 * @param id       The block id, or the id prefix for a wildcard.
 * @param wildcard True if this stands for every block whose id starts with {@link #id}.
 * @param meta     The block metadata to match, or -1 for any.
 */
public record BlockTarget(String id, boolean wildcard, int meta) {

    /**
     * Reads one config line.
     *
     * @return The target, or null if the line is not usable.
     */
    public static @Nullable
    BlockTarget parse(String line, Consumer<String> problems) {
        String[] idAndMeta = line.trim().split(" ", 2);
        String id = idAndMeta[0];
        if (id.isEmpty()) {
            return null;
        }
        if (idAndMeta.length > 1) {
            try {
                return new BlockTarget(id, false, Integer.parseInt(idAndMeta[1].trim()));
            } catch (NumberFormatException ex) {
                problems.accept("Ignoring config line '" + line + "' - the block metadata is not a number");
                return null;
            }
        }
        if (id.endsWith("*")) {
            return new BlockTarget(id.substring(0, id.length() - 1), true, -1);
        }
        return new BlockTarget(id, false, -1);
    }

    /**
     * Finds the blocks named by a list of config lines. This needs the block registry to be complete, so it must
     * not be called before the init phase.
     */
    public static Set<Block> resolveBlocks(String[] lines, Consumer<String> problems) {
        Set<Block> blocks = Collections.newSetFromMap(new IdentityHashMap<>());
        for (String line : lines) {
            BlockTarget target = parse(line, problems);
            if (target == null) {
                continue;
            }
            if (target.wildcard) {
                for (ResourceLocation blockId : Block.REGISTRY.getKeys()) {
                    if (blockId.toString().startsWith(target.id)) {
                        blocks.add(Block.REGISTRY.getObject(blockId));
                    }
                }
            } else {
                Block block = findBlock(target.id);
                if (block == Blocks.AIR) {
                    problems.accept("Missing or invalid block! (" + target.id + ")");
                } else {
                    blocks.add(block);
                }
            }
        }
        blocks.remove(Blocks.AIR);
        return blocks;
    }

    private static Block findBlock(String id) {
        Block block = Block.REGISTRY.getObject(new ResourceLocation(id));
        if (block == Blocks.AIR) {
            try {
                block = Block.getBlockById(Integer.parseInt(id));
            } catch (NumberFormatException ex) {
                // Not a numerical id either
            }
        }
        return block;
    }

}
