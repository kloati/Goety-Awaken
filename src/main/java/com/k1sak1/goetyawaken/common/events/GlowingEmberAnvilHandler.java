package com.k1sak1.goetyawaken.common.events;

import com.Polarice3.Goety.client.inventory.container.DarkAnvilMenu;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.common.items.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = "goetyawaken", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GlowingEmberAnvilHandler {

    private static final String ENHANCEMENT_COUNT_TAG = "GlowingEmberEnhancementCount";
    private static final int MAX_ENHANCEMENTS = Config.GLOWING_EMBER_MAX_ENHANCEMENTS.get();
    private static final int EXPERIENCE_COST = 30;

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        Player player = event.getPlayer();

        if (!right.is(ModItems.GILDED_INGOT.get())) {
            return;
        }

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(left);
        boolean isEnchantedBook = left.getItem() instanceof EnchantedBookItem &&
                !EnchantedBookItem.getEnchantments(left).isEmpty();

        if (enchantments.isEmpty() && !isEnchantedBook) {
            return;
        }

        int enhancementCount = getEnhancementCount(left);
        if (enhancementCount >= MAX_ENHANCEMENTS) {
            return;
        }

        ItemStack output = left.copy();
        Map<Enchantment, Integer> newEnchantments = new HashMap<>(enchantments);

        if (isEnchantedBook) {
            CompoundTag tag = left.getTag();
            if (tag != null && tag.contains("StoredEnchantments", 9)) {
                var storedEnchantments = tag.getList("StoredEnchantments", 10);
                for (int i = 0; i < storedEnchantments.size(); i++) {
                    CompoundTag enchantmentTag = storedEnchantments.getCompound(i);
                    ResourceLocation enchantmentId = new ResourceLocation(enchantmentTag.getString("id"));
                    Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.getOptional(enchantmentId).orElse(null);
                    if (enchantment != null) {
                        int level = enchantmentTag.getInt("lvl");
                        newEnchantments.put(enchantment, level + 1);
                    }
                }
            }
        } else {
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                Enchantment enchantment = entry.getKey();
                int level = entry.getValue();
                newEnchantments.put(enchantment, level + 1);
            }
        }
        EnchantmentHelper.setEnchantments(newEnchantments, output);
        CompoundTag outputTag = output.getOrCreateTag();
        outputTag.putInt(ENHANCEMENT_COUNT_TAG, enhancementCount + 1);
        int currentRepairCost = output.getBaseRepairCost();
        output.setRepairCost(currentRepairCost + 1);
        event.setOutput(output);
        event.setCost(EXPERIENCE_COST);
        event.setMaterialCost(1);

        if (player.containerMenu instanceof DarkAnvilMenu darkAnvilMenu) {
            darkAnvilMenu.repairItemCountCost = 1;
        }
    }

    public static int getEnhancementCount(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(ENHANCEMENT_COUNT_TAG)) {
            return 0;
        }
        return tag.getInt(ENHANCEMENT_COUNT_TAG);
    }

    public static boolean isMaxEnhanced(ItemStack stack) {
        return getEnhancementCount(stack) >= MAX_ENHANCEMENTS;
    }
}
