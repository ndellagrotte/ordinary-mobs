/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.client;

import fathertoast.specialmobs.CommonProxy;
import fathertoast.specialmobs.entity.ISpecialMob;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public class ClientProxy extends CommonProxy {

    @Override
    public void applyTextures(int entityId, String[] texturePaths) {
        // Messages arrive on the network thread, the world may only be touched from the client thread
        Minecraft minecraft = Minecraft.getMinecraft();
        minecraft.addScheduledTask(() -> {
            if (minecraft.world == null) {
                return;
            }
            Entity entity = minecraft.world.getEntityByID(entityId);
            if (entity instanceof ISpecialMob mob) {
                mob.getSpecialData().loadTextures(texturePaths);
            }
        });
    }

}
