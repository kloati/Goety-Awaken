package com.k1sak1.goetyawaken.common.items;

import com.Polarice3.Goety.api.items.ISoulRepair;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.init.ModSounds;

import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public class DarkNetheriteBowItem extends BowItem implements ISoulRepair {

    public DarkNetheriteBowItem() {
        super(new Properties()
                .durability(768)
                .rarity(Rarity.RARE));
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving, int pTimeLeft) {
        if (pEntityLiving instanceof Player player) {
            boolean flag = player.getAbilities().instabuild
                    || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, pStack) > 0;
            ItemStack itemstack = player.getProjectile(pStack);

            int i = this.getUseDuration(pStack) - pTimeLeft;
            i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(pStack, pLevel, player, i,
                    !itemstack.isEmpty() || flag);
            if (i < 0)
                return;

            if (!itemstack.isEmpty() || flag) {
                if (itemstack.isEmpty()) {
                    itemstack = new ItemStack(Items.ARROW);
                }

                float f = getPowerForTime(i);
                if (!((double) f < 0.1D)) {
                    boolean flag1 = player.getAbilities().instabuild || (itemstack.getItem() instanceof ArrowItem
                            && ((ArrowItem) itemstack.getItem()).isInfinite(itemstack, pStack, player));

                    if (!pLevel.isClientSide) {
                        ArrowItem arrowitem = (ArrowItem) (itemstack.getItem() instanceof ArrowItem
                                ? itemstack.getItem()
                                : Items.ARROW);
                        AbstractArrow abstractarrow = arrowitem.createArrow(pLevel, itemstack, player);
                        abstractarrow = customArrow(abstractarrow);
                        abstractarrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, f * 4.8F,
                                1.0F);
                        if (abstractarrow instanceof Arrow arrow) {
                            arrow.addEffect(new MobEffectInstance(GoetyEffects.WANE.get(), 120, 0));
                        }
                        if (f >= 1.0F) {
                            abstractarrow.setCritArrow(true);
                        }

                        int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, pStack);
                        if (j > 0) {
                            abstractarrow.setBaseDamage(abstractarrow.getBaseDamage() + (double) j * 0.5D + 0.5D);
                        }

                        int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, pStack);
                        if (k > 0) {
                            abstractarrow.setKnockback(k);
                        }

                        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, pStack) > 0) {
                            abstractarrow.setSecondsOnFire(100);
                        }

                        pStack.hurtAndBreak(1, player, (p_289501_) -> {
                            p_289501_.broadcastBreakEvent(player.getUsedItemHand());
                        });

                        abstractarrow.pickup = AbstractArrow.Pickup.DISALLOWED;
                        if (flag1 || player.getAbilities().instabuild
                                && (itemstack.is(Items.SPECTRAL_ARROW) || itemstack.is(Items.TIPPED_ARROW))) {
                            abstractarrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                        }
                        pLevel.addFreshEntity(abstractarrow);
                    }

                    pLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                            ModSounds.APOSTLE_SHOOT.get(), SoundSource.PLAYERS, 1.0F,
                            1.0F / (pLevel.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    if (!flag1 && !player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                        if (itemstack.isEmpty()) {
                            player.getInventory().removeItem(itemstack);
                        }
                    }
                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        } else {
            ItemStack itemstack = pEntityLiving.getProjectile(pStack);
            int i = this.getUseDuration(pStack) - pTimeLeft;
            if (i < 0)
                return;

            if (!itemstack.isEmpty()
                    || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, pStack) > 0) {
                if (itemstack.isEmpty()) {
                    itemstack = new ItemStack(Items.ARROW);
                }

                float f = getPowerForTime(i);
                if (!((double) f < 0.1D)) {
                    if (!pLevel.isClientSide) {
                        ArrowItem arrowitem = (ArrowItem) (itemstack.getItem() instanceof ArrowItem
                                ? itemstack.getItem()
                                : Items.ARROW);
                        AbstractArrow abstractarrow = arrowitem.createArrow(pLevel, itemstack, pEntityLiving);
                        abstractarrow = customArrow(abstractarrow);
                        abstractarrow.shootFromRotation(pEntityLiving, pEntityLiving.getXRot(), pEntityLiving.getYRot(),
                                0.0F, f * 4.8F,
                                1.0F);

                        pLevel.addFreshEntity(abstractarrow);
                    }

                    pLevel.playSound(null, pEntityLiving.getX(), pEntityLiving.getY(), pEntityLiving.getZ(),
                            ModSounds.APOSTLE_SHOOT.get(), SoundSource.HOSTILE, 1.0F,
                            1.0F / (pLevel.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                }
            }
        }
    }

    @Override
    public AbstractArrow customArrow(AbstractArrow arrow) {
        if (arrow instanceof Arrow vanillaArrow) {
            vanillaArrow.addEffect(new MobEffectInstance(GoetyEffects.WANE.get(), 120, 0));
        }
        return arrow;
    }

    public static float getPowerForTime(int chargeTime) {
        float f = (float) chargeTime / 30.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    @Override
    public void repairTick(ItemStack stack, Entity entityIn, boolean isSelected) {
        if (entityIn instanceof Player player) {
            com.Polarice3.Goety.utils.ItemHelper.repairTick(stack, entityIn, isSelected);
        }
    }
}
