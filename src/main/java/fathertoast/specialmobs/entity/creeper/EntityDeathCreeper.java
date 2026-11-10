/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.creeper;

import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityDeathCreeper extends Entity_SpecialCreeper {

    private static final ResourceLocation[] TEXTURES = {
        new ResourceLocation(getTexturePath("death")),
        new ResourceLocation(getTexturePath("death_eyes"))
    };

    public EntityDeathCreeper(World world) {
        super(world);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addGuaranteedDrop("base", "Gunpowder", Items.GUNPOWDER, 1);
        loot.addRareDrop("rare", "TNT", Blocks.TNT);
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

    @Override
    protected void initTypeAI() {
        setExplodesWhenBurning(true);
    }

    // The explosion caused by this creeper.
    @Override
    public void explodeByType(boolean powered, boolean griefing) {
        float power = (float) (explosionRadius + 1) * (powered ? 2.0F : 1.0F);
        world.createExplosion(this, posX, posY, posZ, power, griefing);
    }

}
