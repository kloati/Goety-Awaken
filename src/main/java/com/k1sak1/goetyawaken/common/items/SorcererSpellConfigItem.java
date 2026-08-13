package com.k1sak1.goetyawaken.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.List;

public class SorcererSpellConfigItem extends Item {

    public SorcererSpellConfigItem() {
        super(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) {
            if (player.hasPermissions(2)) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                        () -> com.k1sak1.goetyawaken.client.ClientScreenHelper::openSpellConfigScreen);
            }
        } else {
            if (!player.hasPermissions(2)) {
                player.displayClientMessage(
                        Component.translatable("message.goetyawaken.spell_config.no_permission"), true);
            }
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.goetyawaken.sorcerer_spell_config.desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.goetyawaken.sorcerer_spell_config.admin_only").withStyle(ChatFormatting.DARK_RED));
    }
}
