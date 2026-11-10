/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.client;

import fathertoast.specialmobs.ai.INinja;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

/** Draws a hiding ninja mob as the block it is disguised as. */
final class NinjaDisguiseRenderer {

    private NinjaDisguiseRenderer() { }

    // Renders the entity as a block if it is a hiding ninja. Returns false if this doesn't render anything (the ninja is not hiding).
    static <T extends EntityLiving & INinja> boolean tryRendering(Render<?> renderer, boolean renderOutlines,
        int teamColor, T entity, double x, double y, double z, float entityYaw, float partialTicks
    ) {
        IBlockState block = entity.getDisguiseBlock();
        if (block != null && block.getRenderType() == EnumBlockRenderType.MODEL) {
            renderer.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();

            if (renderOutlines) {
                GlStateManager.enableColorMaterial();
                GlStateManager.enableOutlineMode(teamColor);
            }

            bufferBuilder.begin(7, DefaultVertexFormats.BLOCK);

            BlockPos pos = new BlockPos(entity);
            GlStateManager.translate((float) (x - pos.getX() - 0.5), (float) (y - pos.getY()),
                (float) (z - pos.getZ() - 0.5)
            );

            BlockRendererDispatcher blockRenderManager = Minecraft.getMinecraft().getBlockRendererDispatcher();
            blockRenderManager.getBlockModelRenderer()
                .renderModel(entity.world, blockRenderManager.getModelForState(block), block, pos, bufferBuilder, false,
                    MathHelper.getPositionRandom(pos)
                );

            tessellator.draw();

            if (renderOutlines) {
                GlStateManager.disableOutlineMode();
                GlStateManager.disableColorMaterial();
            }

            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
            return true;
        }
        return false;
    }

}
