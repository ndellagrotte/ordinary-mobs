/*
 * Copyright (C) 2019 FatherToast (original author)
 * Copyright (C) 2026 Nicholas DellaGrotte
 * SPDX-License-Identifier: GPL-3.0-only
 */

package fathertoast.specialmobs.entity.creeper;

import fathertoast.specialmobs.entity.SpecialMobData;
import fathertoast.specialmobs.loot.LootTableBuilder;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntitySplittingCreeper extends Entity_SpecialCreeper {

    private static final String TAG_BABIES = "ExtraBabies";

    private static final ResourceLocation[] TEXTURES = {
        new ResourceLocation(getTexturePath("splitting")),
        new ResourceLocation(getTexturePath("splitting_eyes"))
    };

    // The number of extra mini creepers spawned on explosion.
    private int extraBabies;

    public EntitySplittingCreeper(World world) {
        super(world);
    }

    public static void buildLootTable(LootTableBuilder loot) {
        addBaseLoot(loot);

        ResourceLocation name = EntityList.getKey(EntityCreeper.class);
        ItemStack spawnEgg = new ItemStack(Items.SPAWN_EGG);
        ItemMonsterPlacer.applyEntityIdToItemStack(spawnEgg, name);
        loot.addUncommonDrop("uncommon", "Spawn egg", spawnEgg);
    }

    @Override
    public ResourceLocation[] getDefaultTextures() {
        return TEXTURES;
    }

    /** Called to modify the mob's attributes based on the variant. */
    @Override
    protected void applyTypeAttributes() {
        extraBabies = rand.nextInt(4);

        experienceValue += 2;

        getSpecialData().addAttribute(SharedMonsterAttributes.MAX_HEALTH, 20.0);
    }

    @Override
    protected void initTypeAI() {
        setExplodesWhenShot(true);
    }

    // The explosion caused by this creeper.
    @Override
    public void explodeByType(boolean powered, boolean griefing) {
        float power = (float) explosionRadius * (powered ? 2.0F : 1.0F);
        world.createExplosion(this, posX, posY, posZ, power - 1.0F, false);

        int babiesToSpawn = extraBabies + (int) (power * power) / 2;

        BlockPos pos = new BlockPos(this);
        EntityMiniCreeper baby;
        for (int i = 0; i < babiesToSpawn; i++) {
            baby = new EntityMiniCreeper(world);
            baby.copyLocationAndAnglesFrom(this);
            baby.setAttackTarget(getAttackTarget());
            baby.onInitialSpawn(world.getDifficultyForLocation(pos), null);

            baby.motionX = (rand.nextDouble() - 0.5) * power / 3.0;
            baby.motionY = 0.3 + 0.3 * rand.nextDouble(); // Used to cause floor clip bug; remove if it happens again
            baby.motionZ = (rand.nextDouble() - 0.5) * power / 3.0;
            baby.onGround = false;

            if (powered) {
                baby.getDataManager().set(Entity_SpecialCreeper.POWERED, true);
            }
            world.spawnEntity(baby);
        }
        playSound(SoundEvents.ENTITY_EGG_THROW, 1.0F, 2.0F / (rand.nextFloat() * 0.4F + 0.8F));
    }

    // Saves this entity to NBT.
    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        NBTTagCompound saveTag = SpecialMobData.getSaveLocation(tag);

        saveTag.setByte(TAG_BABIES, (byte) extraBabies);
    }

    // Reads this entity from NBT.
    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        NBTTagCompound saveTag = SpecialMobData.getSaveLocation(tag);

        if (saveTag.hasKey(TAG_BABIES, SpecialMobData.NBT_TYPE_PRIMITIVE)) {
            extraBabies = saveTag.getByte(TAG_BABIES);
        }
    }

}
