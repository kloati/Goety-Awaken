package com.k1sak1.goetyawaken.common.items;

import com.google.common.collect.Lists;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
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
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving, int pTimeLeft) {
        int i = this.getUseDuration(pStack) - pTimeLeft;
        float f = getPowerForTime(i, pStack);
        if (f >= 1.0F && !isCharged(pStack) && tryLoadProjectiles(pEntityLiving, pStack)) {
            setCharged(pStack, true);
            SoundSource soundsource = pEntityLiving instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
            pLevel.playSound((Player) null, pEntityLiving.getX(), pEntityLiving.getY(), pEntityLiving.getZ(),
                    SoundEvents.CROSSBOW_LOADING_END, soundsource, 1.0F,
                    1.0F / (pLevel.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
        }
    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pCount) {
        if (!pLevel.isClientSide) {
            int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, pStack);
            SoundEvent soundevent = this.getStartSound(i);
            SoundEvent soundevent1 = i == 0 ? SoundEvents.CROSSBOW_LOADING_MIDDLE : null;
            float f = (float) (pStack.getUseDuration() - pCount) / (float) getChargeDuration(pStack);
            if (f < 0.2F) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
            }

            if (f >= 0.2F && !this.startSoundPlayed) {
                this.startSoundPlayed = true;
                pLevel.playSound((Player) null, pLivingEntity.getX(), pLivingEntity.getY(), pLivingEntity.getZ(),
                        soundevent, SoundSource.PLAYERS, 0.5F, 1.0F);
            }

            if (f >= 0.5F && soundevent1 != null && !this.midLoadSoundPlayed) {
                this.midLoadSoundPlayed = true;
                pLevel.playSound((Player) null, pLivingEntity.getX(), pLivingEntity.getY(), pLivingEntity.getZ(),
                        soundevent1, SoundSource.PLAYERS, 0.5F, 1.0F);
            }
        }
    }

    private SoundEvent getStartSound(int pEnchantmentLevel) {
        switch (pEnchantmentLevel) {
            case 1:
                return SoundEvents.CROSSBOW_QUICK_CHARGE_1;
            case 2:
                return SoundEvents.CROSSBOW_QUICK_CHARGE_2;
            case 3:
                return SoundEvents.CROSSBOW_QUICK_CHARGE_3;
            default:
                return SoundEvents.CROSSBOW_LOADING_START;
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

    private static boolean tryLoadProjectiles(LivingEntity pShooter, ItemStack pCrossbowStack) {
        int multishotLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, pCrossbowStack);
        int shots = 1;

        boolean flag = pShooter instanceof Player && ((Player) pShooter).getAbilities().instabuild;
        ItemStack itemstack = pShooter.getProjectile(pCrossbowStack);
        ItemStack itemstack1 = itemstack.copy();

        if (itemstack.isEmpty() && flag) {
            itemstack = new ItemStack(Items.ARROW);
            itemstack1 = itemstack.copy();
        }

        if (!loadProjectile(pShooter, pCrossbowStack, itemstack, false, flag)) {
            return false;
        }

        return true;
    }

    private static boolean loadProjectile(LivingEntity pShooter, ItemStack pCrossbowStack, ItemStack pAmmoStack,
            boolean pHasAmmo, boolean pIsCreative) {
        if (pAmmoStack.isEmpty()) {
            return false;
        } else {
            boolean flag = pIsCreative && pAmmoStack.getItem() instanceof ArrowItem;
            ItemStack itemstack;
            if (!flag && !pIsCreative && !pHasAmmo) {
                itemstack = pAmmoStack.split(1);
                if (pAmmoStack.isEmpty() && pShooter instanceof Player) {
                    ((Player) pShooter).getInventory().removeItem(pAmmoStack);
                }
            } else {
                itemstack = pAmmoStack.copy();
            }

            addChargedProjectile(pCrossbowStack, itemstack);
            return true;
        }
    }

    private static void addChargedProjectile(ItemStack pCrossbowStack, ItemStack pAmmoStack) {

        CompoundTag compoundtag = pCrossbowStack.getOrCreateTag();
        ListTag listtag;
        if (compoundtag.contains("ChargedProjectiles", 9)) {
            listtag = compoundtag.getList("ChargedProjectiles", 10);
        } else {
            listtag = new ListTag();
        }

        CompoundTag compoundtag1 = new CompoundTag();
        pAmmoStack.save(compoundtag1);
        listtag.add(compoundtag1);
        compoundtag.put("ChargedProjectiles", listtag);
    }

    private static List<ItemStack> getChargedProjectiles(ItemStack pCrossbowStack) {
        List<ItemStack> list = Lists.newArrayList();
        CompoundTag compoundtag = pCrossbowStack.getTag();
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

    private static void clearChargedProjectiles(ItemStack pCrossbowStack) {
        CompoundTag compoundtag = pCrossbowStack.getTag();
        if (compoundtag != null) {
            ListTag listtag = compoundtag.getList("ChargedProjectiles", 9);
            listtag.clear();
            compoundtag.put("ChargedProjectiles", listtag);
        }
    }

    private static float getShootingPower(ItemStack pCrossbowStack) {
        return containsChargedProjectile(pCrossbowStack, Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
    }

    public static boolean containsChargedProjectile(ItemStack pCrossbowStack, Item pAmmoItem) {
        return getChargedProjectiles(pCrossbowStack).stream().anyMatch((p_40870_) -> {
            return p_40870_.is(pAmmoItem);
        });
    }

    public static boolean isCharged(ItemStack pCrossbowStack) {
        CompoundTag compoundtag = pCrossbowStack.getTag();
        return compoundtag != null && compoundtag.getBoolean("Charged");
    }

    public static void setCharged(ItemStack pCrossbowStack, boolean pIsCharged) {
        CompoundTag compoundtag = pCrossbowStack.getOrCreateTag();
        compoundtag.putBoolean("Charged", pIsCharged);
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

    private static AbstractArrow getArrow(Level pLevel, LivingEntity pLivingEntity, ItemStack pCrossbowStack,
            ItemStack pAmmoStack) {
        ArrowItem arrowitem = (ArrowItem) (pAmmoStack.getItem() instanceof ArrowItem ? pAmmoStack.getItem()
                : Items.ARROW);
        AbstractArrow abstractarrow = arrowitem.createArrow(pLevel, pAmmoStack, pLivingEntity);
        if (pLivingEntity instanceof Player) {

            abstractarrow.setCritArrow(true);
        }

        abstractarrow.setSoundEvent(SoundEvents.CROSSBOW_HIT);
        abstractarrow.setShotFromCrossbow(true);
        int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, pCrossbowStack);

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
                String[] harpShootSounds = { "harp_crossbow_shoot_1", "harp_crossbow_shoot_2", "harp_crossbow_shoot_3",
                        "harp_crossbow_shoot_4" };
                String randomSound = harpShootSounds[pLevel.random.nextInt(harpShootSounds.length)];
                net.minecraft.resources.ResourceLocation soundLocation = new net.minecraft.resources.ResourceLocation(
                        "goetyawaken", randomSound);
                net.minecraft.sounds.SoundEvent soundEvent = net.minecraftforge.registries.ForgeRegistries.SOUND_EVENTS
                        .getValue(soundLocation);
                pLevel.playSound((Player) null, pShooter.getX(), pShooter.getY(), pShooter.getZ(),
                        soundEvent, SoundSource.PLAYERS, 1.0F, afloat[Math.min(0, afloat.length - 1)]);
            }

        }

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

    private static void onCrossbowShot(Level pLevel, LivingEntity pShooter, ItemStack pCrossbowStack) {
        if (pShooter instanceof ServerPlayer serverplayer) {

            if (!pLevel.isClientSide) {
                CriteriaTriggers.SHOT_CROSSBOW.trigger(serverplayer, pCrossbowStack);
            }

            serverplayer.awardStat(Stats.ITEM_USED.get(pCrossbowStack.getItem()));
        }

        clearChargedProjectiles(pCrossbowStack);
    }
}
