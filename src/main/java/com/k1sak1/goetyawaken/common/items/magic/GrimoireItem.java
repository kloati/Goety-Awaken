package com.k1sak1.goetyawaken.common.items.magic;

import com.google.common.collect.Multimap;
import com.k1sak1.goetyawaken.common.items.magic.grimoire.GrimoireAffix;
import com.k1sak1.goetyawaken.common.items.magic.grimoire.GrimoireAffixHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GrimoireItem extends Item implements ICurioItem {
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
            com.k1sak1.goetyawaken.common.events.ItemEvents.renameGrimoire(stack, this.level);
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
                com.k1sak1.goetyawaken.common.events.ItemEvents.renameGrimoire(stack, this.level);
                tag.putBoolean(TAG_RANDOMIZED, true);
            }
        }
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        var entity = slotContext.entity();
        if (entity == null) {
            return true;
        }
        var inventoryOpt = top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(entity).resolve();
        if (inventoryOpt.isEmpty()) {
            return true;
        }
        var targetItem = stack.getItem();
        String currentId = slotContext.identifier();
        int currentIndex = slotContext.index();
        var inventory = inventoryOpt.get();
        for (var entry : inventory.getCurios().entrySet()) {
            var handler = entry.getValue();
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack other = handler.getStacks().getStackInSlot(i);
                if (!other.isEmpty() && other.getItem() == targetItem
                        && !(entry.getKey().equals(currentId) && i == currentIndex)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        if (!GrimoireAffixHelper.isEnabled()) {
            return;
        }
        int used = GrimoireAffix.getAffixes(stack).size();
        tooltip.add(Component.translatable("item.goetyawaken.grimoire.desc.infusion")
                .withStyle(net.minecraft.ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.goetyawaken.grimoire.desc.capacity", used, this.level)
                .withStyle(net.minecraft.ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.goetyawaken.grimoire.desc.servant", this.level)
                .withStyle(net.minecraft.ChatFormatting.AQUA));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid,
            ItemStack stack) {
        if (!GrimoireAffixHelper.isFirstOfType(slotContext, stack)) {
            return com.google.common.collect.LinkedHashMultimap.create();
        }
        return GrimoireAffixHelper.getAttributeModifiers(stack, uuid);
    }

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltips, ItemStack stack) {
        if (tooltips.isEmpty()) {
            return tooltips;
        }
        List<Component> result = new ArrayList<>();
        boolean titleReplaced = false;
        for (Component component : tooltips) {
            if (component.getContents() instanceof TranslatableContents contents
                    && contents.getKey() != null
                    && contents.getKey().startsWith("curios.modifiers.")) {
                if (titleReplaced) {
                    break;
                }
                titleReplaced = true;
                result.add(Component.translatable("curios.modifiers.curio").withStyle(component.getStyle()));
                continue;
            }
            result.add(component);
        }
        return result;
    }

    public int getLevel() {
        return level;
    }
}