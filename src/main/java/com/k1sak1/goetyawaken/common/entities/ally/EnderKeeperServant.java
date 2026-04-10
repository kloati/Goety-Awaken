package com.k1sak1.goetyawaken.common.entities.ally;

import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.projectiles.FlyingItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class EnderKeeperServant extends NeoEnderKeeper implements ICustomAttributes {
    public EnderKeeperServant(EntityType<? extends NeoEnderKeeper> type, Level worldIn) {
        super(type, worldIn);
        this.setPersistenceRequired();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.setConfigurableAttributes();
        this.setHealth(this.getMaxHealth());
        return pSpawnData;
    }

    public boolean canBeLeashed(LivingEntity player) {
        return true;
    }

    public boolean canChangeGoalTarget() {
        return true;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {

    }

    @Override
    public void die(DamageSource pCause) {
        if (!this.level().isClientSide) {
            ItemStack eyeOfOverwatchStack = new ItemStack(
                    com.k1sak1.goetyawaken.common.items.ModItems.EYE_OF_OVERWATCH.get());
            if (this.getTrueOwner() != null) {
                FlyingItem flyingItem = new FlyingItem(
                        ModEntityType.FLYING_ITEM.get(),
                        this.level(),
                        this.getX(),
                        this.getY() + 1.0D,
                        this.getZ());
                flyingItem.setOwner(this.getTrueOwner());
                flyingItem.setItem(eyeOfOverwatchStack);
                flyingItem.setParticle(net.minecraft.core.particles.ParticleTypes.SOUL);
                flyingItem.setSecondsCool(30);
                this.level().addFreshEntity(flyingItem);
            } else {
                ItemEntity itemEntity = this.spawnAtLocation(eyeOfOverwatchStack);
                if (itemEntity != null) {
                    itemEntity.setExtendedLifetime();
                }
            }
        }
        super.die(pCause);
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return com.k1sak1.goetyawaken.Config.enderKeeperServantLimit;
    }

    public void warnKill(Player player) {
        this.killChance = 60;
        player.displayClientMessage(
                net.minecraft.network.chat.Component.translatable("info.goety.servant.tryKill", this.getDisplayName()),
                true);
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0) {
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.getTrueOwner() == player && this.getHealth() < this.getMaxHealth()) {
            ItemStack itemstack = player.getItemInHand(hand);
            if (!this.level().isClientSide) {
                float healAmount = this.getHealAmount(itemstack);
                if (healAmount > 0) {
                    float oldHealth = this.getHealth();
                    this.heal(healAmount);
                    float newHealth = this.getHealth();
                    if (newHealth > oldHealth) {
                        if (!player.isCreative()) {
                            itemstack.shrink(1);
                            if (itemstack.getItem() == com.Polarice3.Goety.common.items.ModItems.VOID_BOTTLE.get()) {
                                player.getInventory().placeItemBackInInventory(
                                        new ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE));
                            } else if (itemstack.getItem() == com.Polarice3.Goety.common.items.ModItems.VOID_BUCKET
                                    .get()) {
                                player.getInventory().placeItemBackInInventory(
                                        new ItemStack(net.minecraft.world.item.Items.BUCKET));
                            }
                        }
                        this.playSound(net.minecraft.sounds.SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    private float getHealAmount(ItemStack stack) {
        net.minecraft.world.item.Item item = stack.getItem();
        if (item == com.Polarice3.Goety.common.items.ModItems.VOID_BOTTLE.get()) {
            return this.getMaxHealth() * 0.01F;
        } else if (item == com.Polarice3.Goety.common.items.ModItems.VOID_BUCKET.get()) {
            return this.getMaxHealth() * 0.05F;
        } else if (item == com.Polarice3.Goety.common.blocks.ModBlocks.VOID_BLOCK.get().asItem()) {
            return this.getMaxHealth() * 0.05F;
        } else if (item == com.Polarice3.Goety.common.items.ModItems.VOID_SHARD.get()) {
            return this.getMaxHealth() * 0.15F;
        } else if (item == com.Polarice3.Goety.common.items.ModItems.VOID_ECHO.get()) {
            return this.getMaxHealth() * 0.30F;
        }
        return 0.0F;
    }
}