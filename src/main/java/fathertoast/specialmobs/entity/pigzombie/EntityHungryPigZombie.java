/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.pigzombie;

import fathertoast.specialmobs.entity.MobHelper;
import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class EntityHungryPigZombie extends Entity_SpecialPigZombie {

    private static final ResourceLocation[] TEXTURES = { new ResourceLocation(getTexturePath("hungry")) };

    public EntityHungryPigZombie(World world) {
        super(world);
        getSpecialData().setRegenerationTime(30);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);
        loot.addCommonDrop("common", "Bones", Items.BONE);
        loot.addUncommonDrop("uncommon", "Food", Items.BEEF, Items.CHICKEN, Items.MUTTON, Items.PORKCHOP, Items.RABBIT,
            Items.COOKIE
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
        getSpecialData().multAttribute(SharedMonsterAttributes.MOVEMENT_SPEED, 1.3);
    }

    @Override
    protected void initTypeAI() {
        disableRangedAI();
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        super.setEquipmentBasedOnDifficulty(difficulty);

        if (getHeldItemMainhand().getItem() instanceof ItemBow) {
            setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
        setCanPickUpLoot(false);
    }

    // Overridden to modify attack effects.
    @Override
    public void onTypeAttack(Entity target) {
        float healAmount = 2.0F;
        if (target instanceof EntityPlayer && ForgeEventFactory.getMobGriefingEvent(world, this)) {
            ItemStack food = MobHelper.stealRandomFood((EntityPlayer) target);
            if (!food.isEmpty()) {
                healAmount += Math.max(((ItemFood) food.getItem()).getHealAmount(food), 0.0F);
                playSound(SoundEvents.ENTITY_PLAYER_BURP, 0.5F, rand.nextFloat() * 0.1F + 0.9F);
            }
        } else {
            healAmount += 2.0F;
        }
        heal(healAmount);
    }

}
