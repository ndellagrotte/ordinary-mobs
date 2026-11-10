/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.zombie;

import fathertoast.specialmobs.ai.IAngler;
import fathertoast.specialmobs.config.Config;
import fathertoast.specialmobs.entity.SpecialMobData;
import fathertoast.specialmobs.entity.projectile.EntitySpecialFishHook;
import fathertoast.specialmobs.loot.LootEntryItemBuilder;
import fathertoast.specialmobs.loot.LootPoolBuilder;
import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityFishingZombie extends Entity_SpecialZombie implements IAngler {

    private static final DataParameter<Boolean> IS_LINE_OUT = EntityDataManager.createKey(EntityFishingZombie.class,
        DataSerializers.BOOLEAN
    );

    // Ticks until this angler can cast its lure again.
    private int rodTime = 0;

    // This angler's lure entity.
    private EntitySpecialFishHook fishHook = null;

    public EntityFishingZombie(World world) {
        super(world);
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
        loot.addPool(new LootPoolBuilder("rare")
                .addConditions(LootPoolBuilder.RARE_CONDITIONS)
                .addEntry(new LootEntryItemBuilder("Fishing rod", Items.FISHING_ROD).enchant(30, true).toLootEntry())
                .toLootPool()
        );
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        getDataManager().register(IS_LINE_OUT, Boolean.FALSE);
    }

    /** Called to modify the mob's attributes based on the variant. */
    @Override
    protected void applyTypeAttributes() {
        experienceValue += 2;

        getSpecialData().multAttribute(SharedMonsterAttributes.MOVEMENT_SPEED, 0.8);
    }

    @Override
    protected void initTypeAI() {
        getSpecialData().rangedAttackSpread = 10.0F;
        getSpecialData().rangedAttackCooldown = 32;
        getSpecialData().rangedAttackMaxCooldown = 48;
        getSpecialData().rangedAttackMaxRange = 10.0F;
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        super.setEquipmentBasedOnDifficulty(difficulty);

        setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.FISHING_ROD));
        if (getItemStackFromSlot(EntityEquipmentSlot.FEET).isEmpty()) {
            ItemStack booties = new ItemStack(Items.LEATHER_BOOTS);
            Items.LEATHER_BOOTS.setColor(booties, 0xFFFF00);
            setItemStackToSlot(EntityEquipmentSlot.FEET, booties);
        }
        setCanPickUpLoot(false);
    }

    // IAngler implementations below this ================

    // Called every tick while this entity is alive.
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (rodTime > 0) {
            rodTime--;
        }
        if (!world.isRemote && rodTime <= 0 && !isLineOut()) {
            EntityLivingBase target = getAttackTarget();
            if (target != null) {
                SpecialMobData<?> data = getSpecialData();

                float distanceSq = (float) getDistanceSq(target);
                if (distanceSq > 16.0F &&
                    distanceSq < data.rangedAttackMaxRange * data.rangedAttackMaxRange &&
                    getEntitySenses().canSee(target)) {
                    new EntitySpecialFishHook(world, this, target);
                    world.spawnEntity(getFishHook());
                    playSound(SoundEvents.ENTITY_BOBBER_THROW, 0.5F, 0.4F / (rand.nextFloat() * 0.4F + 0.8F));
                    rodTime = rand.nextInt(Math.max(1, data.rangedAttackMaxCooldown - data.rangedAttackCooldown)) +
                        data.rangedAttackCooldown;
                }
            }
        }
    }

    /**
     * Sets this angler's fish hook.
     *
     * @param hook The angler's new fish hook.
     */
    @Override
    public void setFishHook(EntitySpecialFishHook hook) {
        fishHook = hook;
        getDataManager().set(IS_LINE_OUT, hook != null);
    }

    /**
     * Gets this angler's fish hook.
     *
     * @return The angler's current fish hook, null if the angler does not have one out.
     */
    @Override
    public EntitySpecialFishHook getFishHook() {
        return fishHook;
    }

    /**
     * @return Whether the angler's line is out.
     */
    @Override
    public boolean isLineOut() {
        return getDataManager().get(IS_LINE_OUT);
    }

    @Override
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slot) {
        if (!Config.get().GENERAL.FANCY_FISHING_MOBS && EntityEquipmentSlot.MAINHAND.equals(slot)) {
            ItemStack held = super.getItemStackFromSlot(slot);
            if (world.isRemote && !held.isEmpty() && held.getItem() instanceof ItemFishingRod && isLineOut()) {
                return new ItemStack(Items.STICK);
            }
            return held;
        }
        return super.getItemStackFromSlot(slot);
    }

}
