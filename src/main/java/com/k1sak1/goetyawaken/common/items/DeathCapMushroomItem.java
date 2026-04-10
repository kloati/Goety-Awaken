package com.k1sak1.goetyawaken.common.items;

import com.k1sak1.goetyawaken.init.ModEffects;
import com.k1sak1.goetyawaken.init.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

import javax.annotation.Nullable;
import java.util.List;

public class DeathCapMushroomItem extends Item {
    public static final String USAGES_TAG = "DeathCapUsages";

    public DeathCapMushroomItem() {
        super(new Item.Properties()
                .food(new FoodProperties.Builder()
                        .nutrition(4)
                        .saturationMod(1.2F)
                        .alwaysEat()
                        .build())
                .stacksTo(1));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entityLiving) {
        if (entityLiving instanceof Player player && !player.getAbilities().instabuild) {
            player.getFoodData().eat(4, 1.2F);
        }

        if (!world.isClientSide && entityLiving instanceof Player player) {

            player.addEffect(new MobEffectInstance(ModEffects.BERSERK.get(), 600, 3));
            int currentUsages = getUsages(stack);
            if (currentUsages <= 1) {
                stack.shrink(1);
            } else {
                setUsages(stack, currentUsages - 1);
            }
            player.getCooldowns().addCooldown(this, 1200);
        }

        if (entityLiving != null) {
            Random random = new Random();
            int soundChoice = random.nextInt(3);
            SoundEvent selectedSound = switch (soundChoice) {
                case 0 -> ModSounds.DEATHCAPMUSHROOMEAT1.get();
                case 1 -> ModSounds.DEATHCAPMUSHROOMEAT2.get();
                case 2 -> ModSounds.DEATHCAPMUSHROOMEAT3.get();
                default -> ModSounds.DEATHCAPMUSHROOMEAT1.get();
            };
            entityLiving.playSound(selectedSound, 1.0F, 1.0F);
        }

        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        int usages = getUsages(stack);
        tooltip.add(Component.translatable("item.goetyawaken.death_cap_mushroom.uses", usages)
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(
                Component.translatable("item.goetyawaken.death_cap_mushroom.tooltip").withStyle(ChatFormatting.WHITE));
    }

    public static int getUsages(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains(USAGES_TAG)) {
            return stack.getTag().getInt(USAGES_TAG);
        }
        return 10;
    }

    public static void setUsages(ItemStack stack, int usages) {
        if (!stack.hasTag()) {
            stack.setTag(new net.minecraft.nbt.CompoundTag());
        }
        stack.getTag().putInt(USAGES_TAG, usages);
    }
}