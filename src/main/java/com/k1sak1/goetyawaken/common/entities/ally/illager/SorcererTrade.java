package com.k1sak1.goetyawaken.common.entities.ally.illager;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class SorcererTrade {
    private final String itemId;
    private final int minLevel;
    private final int maxLevel;
    private final int minCount;
    private final int maxCount;
    private final int weight;
    private final int price;

    public SorcererTrade(String itemId, int minLevel, int maxLevel, int minCount, int maxCount, int weight, int price) {
        this.itemId = itemId;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.minCount = minCount;
        this.maxCount = maxCount;
        this.weight = weight;
        this.price = price;
    }

    public ItemStack getItemStack(Level level) {
        ResourceLocation resourceLocation = new ResourceLocation(this.itemId);
        Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);
        if (item != null) {
            int count = minCount + level.random.nextInt(maxCount - minCount + 1);
            return new ItemStack(item, count);
        }
        return ItemStack.EMPTY;
    }

    public boolean isAvailableForLevel(int level) {
        return level >= this.minLevel && level <= this.maxLevel;
    }

    public boolean isAffordable(int money) {
        return money >= this.price;
    }

    public int getWeight() {
        return this.weight;
    }

    public int getPrice() {
        return this.price;
    }

    public String getItemId() {
        return this.itemId;
    }

    public int getMinLevel() {
        return this.minLevel;
    }

    public int getMaxLevel() {
        return this.maxLevel;
    }
}