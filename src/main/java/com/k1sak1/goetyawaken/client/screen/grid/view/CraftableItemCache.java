package com.k1sak1.goetyawaken.client.screen.grid.view;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class CraftableItemCache {
    private static Set<Item> cache;
    private static int lastRecipeCount = -1;

    private CraftableItemCache() {
    }

    public static boolean isCraftable(Item item) {
        Set<Item> set = getOrBuild();
        return set.contains(item);
    }

    public static void invalidate() {
        cache = null;
        lastRecipeCount = -1;
    }

    private static Set<Item> getOrBuild() {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) {
            return Collections.emptySet();
        }

        RecipeManager mgr = level.getRecipeManager();
        int count = mgr.getRecipes().size();

        if (cache != null && count == lastRecipeCount) {
            return cache;
        }

        Set<Item> result = new HashSet<>();
        for (Recipe<?> recipe : mgr.getAllRecipesFor(RecipeType.CRAFTING)) {
            try {
                ItemStack out = recipe.getResultItem(level.registryAccess());
                if (out != null && !out.isEmpty()) {
                    result.add(out.getItem());
                }
            } catch (Throwable ignored) {

            }
        }

        cache = result;
        lastRecipeCount = count;
        return cache;
    }
}
