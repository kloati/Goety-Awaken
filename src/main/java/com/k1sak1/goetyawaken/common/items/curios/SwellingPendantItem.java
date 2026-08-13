package com.k1sak1.goetyawaken.common.items.curios;

import com.Polarice3.Goety.common.items.curios.SingleStackItem;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.ItemHelper;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.common.items.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

public class SwellingPendantItem extends SingleStackItem {

    public static final String SLIME_VALUE_KEY = "slime_value";
    private static final TagKey<Item> SLIMEBALLS_TAG = ItemTags.create(new ResourceLocation("forge", "slimeballs"));

    public SwellingPendantItem() {
        super(new Item.Properties().stacksTo(1).rarity(net.minecraft.world.item.Rarity.UNCOMMON));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof Player player) {
            if (!stack.hasTag()) {
                stack.setTag(new CompoundTag());
                stack.getOrCreateTag().putInt(SLIME_VALUE_KEY, 0);
            } else if (CuriosFinder.hasCurio(player, this)) {
                if (getSlimeValue(stack) < getMaxSlimeValue()) {
                    ItemStack found = ItemHelper.findItem(player, s -> isSlimeball(s));
                    if (!found.isEmpty()) {
                        found.shrink(1);
                        increaseSlimeValue(stack);
                        if (player.level().random.nextFloat() < 0.05F) {
                            player.level().playSound(null, player, SoundEvents.SLIME_SQUISH_SMALL,
                                    SoundSource.PLAYERS, 0.3F, 1.5F + player.level().random.nextFloat() * 0.5F);
                        }
                    }
                }
            }
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        CompoundTag compound = pStack.getOrCreateTag();
        compound.putInt(SLIME_VALUE_KEY, 0);
    }

    public void increaseSlimeValue(ItemStack stack) {
        if (stack.getTag() != null) {
            stack.getOrCreateTag().putInt(SLIME_VALUE_KEY, Math.min(getSlimeValue(stack) + 1, getMaxSlimeValue()));
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.getTag() != null;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (stack.getTag() != null) {
            int power = stack.getTag().getInt(SLIME_VALUE_KEY);
            return Math.round((power * 13.0F / getMaxSlimeValue()));
        } else {
            return 0;
        }
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float f = Math.max(0.0F, (float) (1.0F - amountColor(stack)) / 2.0F);
        return Mth.hsvToRgb(1.0F, f, f);
    }

    public double amountColor(ItemStack stack) {
        if (stack.getTag() != null) {
            int i = stack.getTag().getInt(SLIME_VALUE_KEY);
            return 1.0D - (i / (double) getMaxSlimeValue());
        } else {
            return 1.0D;
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }

    public static int getMaxSlimeValue() {
        return Math.max(Config.swellingPendantMaxSlimeValue, 1);
    }

    public static boolean isSlimeball(ItemStack stack) {
        return stack.is(Items.SLIME_BALL) || stack.is(SLIMEBALLS_TAG);
    }

    public static int getSlimeValue(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains(SLIME_VALUE_KEY)) {
            return stack.getTag().getInt(SLIME_VALUE_KEY);
        }
        return 0;
    }

    public static void setSlimeValue(ItemStack stack, int value) {
        stack.getOrCreateTag().putInt(SLIME_VALUE_KEY, Math.min(value, getMaxSlimeValue()));
    }

    public static int consumeSlimeValue(ItemStack stack, int amount) {
        int current = getSlimeValue(stack);
        int consumed = Math.min(current, amount);
        setSlimeValue(stack, current - consumed);
        return consumed;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("item.goetyawaken.swelling_pendant.description"));
        int slimeValue = getSlimeValue(stack);
        tooltip.add(
                Component.translatable("info.goetyawaken.swelling_pendant.slime_value", slimeValue, getMaxSlimeValue())
                        .withStyle(ChatFormatting.GREEN));
    }

    public static boolean hasSwellingPendant(LivingEntity entity) {
        return CuriosFinder.hasCurio(entity, ModItems.SWELLING_PENDANT.get());
    }

    public static ItemStack findPendantStack(LivingEntity entity) {
        return CuriosFinder.findCurio(entity, ModItems.SWELLING_PENDANT.get());
    }
}
