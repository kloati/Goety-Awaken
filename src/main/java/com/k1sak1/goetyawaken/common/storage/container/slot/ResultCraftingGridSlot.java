package com.k1sak1.goetyawaken.common.storage.container.slot;

import com.k1sak1.goetyawaken.common.blocks.EnderAccessLecternBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;

public class ResultCraftingGridSlot extends ResultSlot {
    private final EnderAccessLecternBlockEntity blockEntity;

    public ResultCraftingGridSlot(Player player, CraftingContainer craftSlots, Container resultSlots,
            int slot, int x, int y, EnderAccessLecternBlockEntity blockEntity) {
        super(player, craftSlots, resultSlots, slot, x, y);
        this.blockEntity = blockEntity;
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        checkTakeAchievements(stack);
        ForgeHooks.setCraftingPlayer(player);
        try {
            if (!player.level().isClientSide) {
                blockEntity.onCrafted(player, stack);
            }
            ForgeEventFactory.firePlayerCraftingEvent(player, stack.copy(), blockEntity.getCraftingMatrix());
        } finally {
            ForgeHooks.setCraftingPlayer(null);
        }
    }
}
