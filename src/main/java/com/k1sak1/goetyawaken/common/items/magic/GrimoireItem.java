package com.k1sak1.goetyawaken.common.items.magic;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public class GrimoireItem extends Item {
    private final int level;
    private static final String TAG_RANDOMIZED = "Randomized";

    public GrimoireItem(int level) {
        super(new Properties()
                .stacksTo(1)
                .rarity(getRarityByLevel(level)));
        this.level = level;
    }

    private static Rarity getRarityByLevel(int level) {
        switch (level) {
            case 2:
                return Rarity.COMMON;
            case 3:
                return Rarity.COMMON;
            case 4:
                return Rarity.UNCOMMON;
            case 5:
                return Rarity.RARE;
            case 6:
                return Rarity.EPIC;
            default:
                return Rarity.COMMON;
        }
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        super.onCraftedBy(stack, level, player);
        if (!level.isClientSide && !stack.hasCustomHoverName()) {
            com.k1sak1.goetyawaken.common.events.GrimoireRenameHandler.renameGrimoire(stack, this.level);
            CompoundTag tag = stack.getOrCreateTag();
            tag.putBoolean(TAG_RANDOMIZED, true);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (!level.isClientSide && entity instanceof Player) {
            CompoundTag tag = stack.getOrCreateTag();
            if (!tag.getBoolean(TAG_RANDOMIZED) && !stack.hasCustomHoverName()) {
                com.k1sak1.goetyawaken.common.events.GrimoireRenameHandler.renameGrimoire(stack, this.level);
                tag.putBoolean(TAG_RANDOMIZED, true);
            }
        }
    }

    public int getLevel() {
        return level;
    }
}