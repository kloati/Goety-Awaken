package com.k1sak1.goetyawaken.common.items;

import com.k1sak1.goetyawaken.common.entities.ally.illager.ApostleServant;
import com.k1sak1.goetyawaken.common.upgrades.ApostleUpgradeManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class ObsidianTear extends Item {
    public ObsidianTear() {
        super(new Properties()
                .stacksTo(64)
                .rarity(Rarity.EPIC));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity,
            InteractionHand hand) {
        if (isServant(entity)) {
            if (entity instanceof ApostleServant) {
                return InteractionResult.FAIL;
            }

            if (ApostleUpgradeManager.isMarkedForUpgrade(entity)) {
                return InteractionResult.FAIL;
            }

            if (!player.level().isClientSide()) {
                ApostleUpgradeManager.markEntityForUpgrade(entity, player);
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
            }

            entity.playSound(SoundEvents.GENERIC_DRINK, 1.0F, 1.0F);
            if (entity.getCustomName() != null) {
                player.displayClientMessage(Component
                        .translatable("message.goetyawaken.servant.on_path", entity.getCustomName().getString())
                        .withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
            } else {
                player.displayClientMessage(
                        Component.translatable("message.goetyawaken.servant.on_path", entity.getName().getString())
                                .withStyle(ChatFormatting.RED, ChatFormatting.BOLD),
                        true);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private boolean isServant(LivingEntity entity) {
        return entity instanceof com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant
                || entity instanceof com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
    }
}