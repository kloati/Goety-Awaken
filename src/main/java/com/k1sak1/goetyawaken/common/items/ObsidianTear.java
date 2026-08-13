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
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ObsidianTear extends Item {
    public ObsidianTear() {
        super(new Properties()
                .stacksTo(64)
                .rarity(Rarity.EPIC));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
            TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("item.goetyawaken.obsidian_tear.description")
                .withStyle(ChatFormatting.GRAY));
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
                entity.playSound(SoundEvents.GENERIC_DRINK, 1.0F, 1.0F);
                String servantName = entity.getCustomName() != null ? entity.getCustomName().getString()
                        : entity.getName().getString();
                player.displayClientMessage(
                        Component.translatable("message.goetyawaken.servant.on_path", servantName)
                                .withStyle(ChatFormatting.RED, ChatFormatting.BOLD),
                        true);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private boolean isServant(LivingEntity entity) {
        return entity instanceof com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
    }
}