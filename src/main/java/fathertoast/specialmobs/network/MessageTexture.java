/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.network;

import fathertoast.specialmobs.SpecialMobsMod;
import fathertoast.specialmobs.entity.ISpecialMob;
import fathertoast.specialmobs.entity.SpecialMobData;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/** Tells clients which textures a special mob uses, for mobs whose textures were changed through nbt. */
public class MessageTexture implements IMessage {

    private static final int TEXTURE_COUNT = 3;

    private int entityId;
    private String[] texturePaths = new String[0];

    /** Used when receiving the message. */
    public MessageTexture() { }

    public <T extends Entity & ISpecialMob> MessageTexture(T entity) {
        entityId = entity.getEntityId();
        SpecialMobData<?> data = entity.getSpecialData();
        texturePaths = new String[] {
            data.getTexture().toString(),
            pathOf(data.getTextureEyes()),
            pathOf(data.getTextureOverlay()),
        };
    }

    private static String pathOf(@Nullable ResourceLocation texture) {
        return texture == null ? "" : texture.toString();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        texturePaths = new String[TEXTURE_COUNT];
        for (int i = 0; i < TEXTURE_COUNT; i++) {
            texturePaths[i] = ByteBufUtils.readUTF8String(buf);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        for (int i = 0; i < TEXTURE_COUNT; i++) {
            ByteBufUtils.writeUTF8String(buf, texturePaths[i]);
        }
    }

    /**
     * Receives the message on the client. This class is also loaded on dedicated servers when the message is
     * registered, so it only reaches client code through the sided proxy.
     */
    public static class Handler implements IMessageHandler<MessageTexture, IMessage> {

        @Override
        public @Nullable
        IMessage onMessage(MessageTexture message, MessageContext ctx) {
            SpecialMobsMod.proxy.applyTextures(message.entityId, message.texturePaths);
            return null;
        }

    }

}
