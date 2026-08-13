package com.k1sak1.goetyawaken.common.items;

import com.google.common.collect.Lists;
import com.k1sak1.goetyawaken.init.ModSounds;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class HarpCrossbowItem extends CrossbowItem {
    private boolean startSoundPlayed = false;
    private boolean midLoadSoundPlayed = false;

    public HarpCrossbowItem() {
        super(new Item.Properties()
                .durability(1024));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (isCharged(itemstack)) {
            performShooting(pLevel, pPlayer, pHand, itemstack, getShootingPower(itemstack), 1.0F);
            setCharged(itemstack, false);
            return InteractionResultHolder.consume(itemstack);
        } else if (!pPlayer.getProjectile(itemstack).isEmpty()) {
            if (!isCharged(itemstack)) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
                pPlayer.startUsingItem(pHand);
            }

            return InteractionResultHolder.consume(itemstack);
        } else {
            return InteractionResultHolder.fail(itemstack);
        }
    }

    @Override
    public void releaseUsing(ItemStack p_40875_, Level p_40876_, LivingEntity p_40877_, int p_40878_) {
        int i = this.getUseDuration(p_40875_) - p_40878_;
        float f = getPowerForTime(i, p_40875_);
        if (f >= 1.0F && !isCharged(p_40875_) && tryLoadProjectiles(p_40877_, p_40875_)) {
            setCharged(p_40875_, true);
            SoundSource soundsource = p_40877_ instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
            p_40876_.playSound((Player) null, p_40877_.getX(), p_40877_.getY(), p_40877_.getZ(),
                    SoundEvents.CROSSBOW_LOADING_END, soundsource, 1.0F,
                    1.0F / (p_40876_.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
        }

    }

    private static float getPowerForTime(int pUseTime, ItemStack pCrossbowStack) {
        float f = (float) pUseTime / (float) getChargeDuration(pCrossbowStack);
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.UNCOMMON;
    }

    private static boolean tryLoadProjectiles(LivingEntity p_40860_, ItemStack p_40861_) {
        boolean flag = p_40860_ instanceof Player && ((Player) p_40860_).getAbilities().instabuild;
        ItemStack itemstack = p_40860_.getProjectile(p_40861_);
        ItemStack itemstack1 = itemstack.copy();

        for (int k = 0; k < 1; ++k) {
            if (k > 0) {
                itemstack = itemstack1.copy();
            }

            if (itemstack.isEmpty() && flag) {
                itemstack = new ItemStack(Items.ARROW);
                itemstack1 = itemstack.copy();
            }

            if (!loadProjectile(p_40860_, p_40861_, itemstack, k > 0, flag)) {
                return false;
            }
        }

        return true;
    }

    private static boolean loadProjectile(LivingEntity p_40863_, ItemStack p_40864_, ItemStack p_40865_,
            boolean p_40866_, boolean p_40867_) {
        if (p_40865_.isEmpty()) {
            return false;
        } else {
            boolean flag = p_40867_ && p_40865_.getItem() instanceof ArrowItem;
            ItemStack itemstack;
            if (!flag && !p_40867_ && !p_40866_) {
                itemstack = p_40865_.split(1);
                if (p_40865_.isEmpty() && p_40863_ instanceof Player) {
                    ((Player) p_40863_).getInventory().removeItem(p_40865_);
                }
            } else {
                itemstack = p_40865_.copy();
            }

            addChargedProjectile(p_40864_, itemstack);
            return true;
        }
    }

    private static void addChargedProjectile(ItemStack p_40929_, ItemStack p_40930_) {
        CompoundTag compoundtag = p_40929_.getOrCreateTag();
        ListTag listtag;
        if (compoundtag.contains("ChargedProjectiles", 9)) {
            listtag = compoundtag.getList("ChargedProjectiles", 10);
        } else {
            listtag = new ListTag();
        }

        CompoundTag compoundtag1 = new CompoundTag();
        p_40930_.save(compoundtag1);
        listtag.add(compoundtag1);
        compoundtag.put("ChargedProjectiles", listtag);
    }

    private static List<ItemStack> getChargedProjectiles(ItemStack p_40942_) {
        List<ItemStack> list = Lists.newArrayList();
        CompoundTag compoundtag = p_40942_.getTag();
        if (compoundtag != null && compoundtag.contains("ChargedProjectiles", 9)) {
            ListTag listtag = compoundtag.getList("ChargedProjectiles", 10);
            if (listtag != null) {
                for (int i = 0; i < listtag.size(); ++i) {
                    CompoundTag compoundtag1 = listtag.getCompound(i);
                    list.add(ItemStack.of(compoundtag1));
                }
            }
        }

        return list;
    }

    private static void clearChargedProjectiles(ItemStack p_40944_) {
        CompoundTag compoundtag = p_40944_.getTag();
        if (compoundtag != null) {
            ListTag listtag = compoundtag.getList("ChargedProjectiles", 9);
            listtag.clear();
            compoundtag.put("ChargedProjectiles", listtag);
        }

    }

    private static float getShootingPower(ItemStack p_40946_) {
        return containsChargedProjectile(p_40946_, Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
    }

    private static void shootProjectile(Level pLevel, LivingEntity pShooter, InteractionHand pHand,
            ItemStack pCrossbowStack, ItemStack pAmmoStack, float pSoundPitch, boolean pIsCreativeMode, float pVelocity,
            float pInaccuracy, float pProjectileAngle) {
        if (!pLevel.isClientSide) {
            boolean flag = pAmmoStack.is(Items.FIREWORK_ROCKET);
            Projectile projectile;
            if (flag) {
                projectile = new FireworkRocketEntity(pLevel, pAmmoStack, pShooter, pShooter.getX(),
                        pShooter.getEyeY() - (double) 0.15F, pShooter.getZ(), true);
            } else {
                projectile = getArrow(pLevel, pShooter, pCrossbowStack, pAmmoStack);
                if (pIsCreativeMode || pProjectileAngle != 0.0F) {
                    ((AbstractArrow) projectile).pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }
            }

            if (pShooter instanceof CrossbowAttackMob) {
                CrossbowAttackMob crossbowattackmob = (CrossbowAttackMob) pShooter;
                crossbowattackmob.shootCrossbowProjectile(crossbowattackmob.getTarget(), pCrossbowStack, projectile,
                        pProjectileAngle);
            } else {
                Vec3 vec31 = pShooter.getUpVector(1.0F);
                Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(
                        (double) (pProjectileAngle * ((float) Math.PI / 180F)), vec31.x, vec31.y, vec31.z);
                Vec3 vec3 = pShooter.getViewVector(1.0F);
                Vector3f vector3f = vec3.toVector3f().rotate(quaternionf);

                projectile.shoot((double) vector3f.x(), (double) vector3f.y(), (double) vector3f.z(), pVelocity,
                        pInaccuracy);
            }

            pCrossbowStack.hurtAndBreak(flag ? 3 : 1, pShooter, (p_40858_) -> {
                p_40858_.broadcastBreakEvent(pHand);

            });
            pLevel.addFreshEntity(projectile);

        }
    }

    private static AbstractArrow getArrow(Level p_40915_, LivingEntity p_40916_, ItemStack p_40917_,
            ItemStack p_40918_) {
        ArrowItem arrowitem = (ArrowItem) (p_40918_.getItem() instanceof ArrowItem ? p_40918_.getItem() : Items.ARROW);
        AbstractArrow abstractarrow = arrowitem.createArrow(p_40915_, p_40918_, p_40916_);
        if (p_40916_ instanceof Player) {
            abstractarrow.setCritArrow(true);
        }

        abstractarrow.setSoundEvent(SoundEvents.CROSSBOW_HIT);
        abstractarrow.setShotFromCrossbow(true);
        int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, p_40917_);
        if (i > 0) {
            abstractarrow.setPierceLevel((byte) i);
        }

        return abstractarrow;
    }

    public static void performShooting(Level pLevel, LivingEntity pShooter, InteractionHand pUsedHand,
            ItemStack pCrossbowStack, float pVelocity, float pInaccuracy) {
        if (pShooter instanceof Player player) {
            if (net.minecraftforge.event.ForgeEventFactory.onArrowLoose(pCrossbowStack, pShooter.level(), player, 1,
                    true) < 0)
                return;
        } else if (!(pShooter instanceof Player)) {
        }
        List<ItemStack> list = getChargedProjectiles(pCrossbowStack);
        float[] afloat = getShotPitches(pShooter.getRandom());
        int multishotLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, pCrossbowStack);
        int totalArrows = 5 + (multishotLevel * 2);
        float[] angles = calculateAngles(totalArrows);
        for (int i = 0; i < list.size(); ++i) {
            ItemStack originalAmmo = list.get(i);
            boolean flag = pShooter instanceof Player && ((Player) pShooter).getAbilities().instabuild;
            if (!originalAmmo.isEmpty()) {
                for (int j = 0; j < totalArrows; j++) {
                    if (j < angles.length) {
                        ItemStack ammoCopy = originalAmmo.copy();
                        shootProjectile(pLevel, pShooter, pUsedHand, pCrossbowStack, ammoCopy,
                                afloat[Math.min(j, afloat.length - 1)], flag, pVelocity, pInaccuracy, angles[j]);
                    }
                }
            }
        }
        pLevel.playSound((Player) null, pShooter.getX(), pShooter.getY(), pShooter.getZ(),
                ModSounds.HARP_CROSSBOW_SHOOT.get(), SoundSource.PLAYERS, 1.0F,
                afloat[Math.min(0, afloat.length - 1)]);
        onCrossbowShot(pLevel, pShooter, pCrossbowStack);
    }

    private static float[] calculateAngles(int arrowCount) {
        float[] angles = new float[arrowCount];

        if (arrowCount == 1) {
            angles[0] = 0.0F;
        } else {
            float angleStep = 15.0F;
            if (arrowCount > 5) {
                angleStep = 7.5F;
            }

            int centerIndex = arrowCount / 2;
            for (int i = 0; i < arrowCount; i++) {
                angles[i] = (i - centerIndex) * angleStep;
            }
        }

        return angles;
    }

    private static float[] getShotPitches(RandomSource pRandom) {
        boolean flag = pRandom.nextBoolean();
        return new float[] { 1.0F, getRandomShotPitch(flag, pRandom), getRandomShotPitch(!flag, pRandom),
                getRandomShotPitch(flag, pRandom), getRandomShotPitch(!flag, pRandom) };
    }

    private static float getRandomShotPitch(boolean pIsHighPitched, RandomSource pRandom) {
        float f = pIsHighPitched ? 0.63F : 0.43F;
        return 1.0F / (pRandom.nextFloat() * 0.5F + 1.8F) + f;
    }

    private static void onCrossbowShot(Level p_40906_, LivingEntity p_40907_, ItemStack p_40908_) {
        if (p_40907_ instanceof ServerPlayer serverplayer) {
            if (!p_40906_.isClientSide) {
                CriteriaTriggers.SHOT_CROSSBOW.trigger(serverplayer, p_40908_);
            }

            serverplayer.awardStat(Stats.ITEM_USED.get(p_40908_.getItem()));
        }

        clearChargedProjectiles(p_40908_);
    }
}
