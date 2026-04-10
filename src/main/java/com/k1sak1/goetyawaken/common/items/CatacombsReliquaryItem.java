package com.k1sak1.goetyawaken.common.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CatacombsReliquaryItem extends Item {
    private static final String FIRE_IMMUNE_TAG = "GoetyAwakenFireImmune";

    public CatacombsReliquaryItem() {
        super(new Properties()
                .stacksTo(64)
                .rarity(Rarity.RARE));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target,
            InteractionHand hand) {
        if (target.getMobType() == MobType.UNDEAD) {
            if (!hasFireImmunity(target)) {
                applyFireImmunity(target);
                if (target.getRemainingFireTicks() > 0) {
                    target.clearFire();
                }
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                return InteractionResult.sidedSuccess(player.level().isClientSide);
            } else {
                return InteractionResult.FAIL;
            }
        }

        return InteractionResult.PASS;
    }

    private boolean hasFireImmunity(LivingEntity entity) {
        return entity.getPersistentData().getBoolean(FIRE_IMMUNE_TAG);
    }

    private void applyFireImmunity(LivingEntity entity) {
        entity.getPersistentData().putBoolean(FIRE_IMMUNE_TAG, true);
        MobEffectInstance fireResistance = new MobEffectInstance(
                MobEffects.FIRE_RESISTANCE,
                MobEffectInstance.INFINITE_DURATION,
                0,
                false,
                false,
                false);
        entity.addEffect(fireResistance);
    }

    public static void removeFireImmunity(LivingEntity entity) {
        entity.getPersistentData().remove(FIRE_IMMUNE_TAG);
        entity.removeEffect(MobEffects.FIRE_RESISTANCE);
    }

    public static boolean isFireImmune(LivingEntity entity) {
        return entity.getPersistentData().getBoolean(FIRE_IMMUNE_TAG);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(
                Component.translatable("item.goetyawaken.catacombs_reliquary.desc").withStyle(ChatFormatting.WHITE));
    }
}