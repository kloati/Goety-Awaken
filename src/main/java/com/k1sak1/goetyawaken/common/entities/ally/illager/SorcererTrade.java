package com.k1sak1.goetyawaken.common.entities.ally.illager;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import com.k1sak1.goetyawaken.Config;

public class SorcererTrade {
    private final String itemId;
    private final int minLevel;
    private final int maxLevel;
    private final int minCount;
    private final int maxCount;
    private final int weight;
    private final String firstItemId;
    private final int price;
    private final String secondItemId;
    private final int secondPrice;

    public SorcererTrade(String itemId, int minLevel, int maxLevel, int minCount, int maxCount, int weight, int price) {
        this(itemId, minLevel, maxLevel, minCount, maxCount, weight, price, "minecraft:emerald", null, 0);
    }

    public SorcererTrade(String itemId, int minLevel, int maxLevel, int minCount, int maxCount, int weight, int price,
            String secondItemId, int secondPrice) {
        this(itemId, minLevel, maxLevel, minCount, maxCount, weight, price, "minecraft:emerald", secondItemId,
                secondPrice);
    }

    public SorcererTrade(String itemId, int minLevel, int maxLevel, int minCount, int maxCount, int weight, int price,
            String firstItemId, String secondItemId, int secondPrice) {
        this.itemId = itemId;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.minCount = minCount;
        this.maxCount = maxCount;
        this.weight = weight;
        this.firstItemId = firstItemId != null && !firstItemId.isEmpty() ? firstItemId : "minecraft:emerald";
        this.price = price;
        this.secondItemId = secondItemId;
        this.secondPrice = secondPrice;
    }

    public ItemStack getItemStack(Level level) {
        ResourceLocation resourceLocation = new ResourceLocation(this.itemId);
        Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);
        if (item != null) {
            int count = minCount + (level != null ? level.random : net.minecraft.util.RandomSource.create())
                    .nextInt(maxCount - minCount + 1);
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

    public MerchantOffer createMerchantOffer(net.minecraft.util.RandomSource random) {
        ResourceLocation firstResourceLocation = new ResourceLocation(this.firstItemId);
        Item firstItem = ForgeRegistries.ITEMS.getValue(firstResourceLocation);
        if (firstItem == null) {
            firstItem = Items.EMERALD;
        }
        ItemStack firstItemStack = new ItemStack(firstItem, this.price);
        ItemStack resultStack = this.getItemStack(random);

        if (this.secondItemId != null && !this.secondItemId.isEmpty() && this.secondPrice > 0) {
            ResourceLocation secondResourceLocation = new ResourceLocation(this.secondItemId);
            Item secondItem = ForgeRegistries.ITEMS.getValue(secondResourceLocation);
            if (secondItem != null) {
                ItemStack secondItemStack = new ItemStack(secondItem, this.secondPrice);
                return new MerchantOffer(firstItemStack, secondItemStack, resultStack,
                        Config.sorcererServantTradeMaxUses, Config.sorcererServantTradeMaxUses, 0.0F);
            }
        }

        return new MerchantOffer(firstItemStack, resultStack, Config.sorcererServantTradeMaxUses,
                Config.sorcererServantTradeMaxUses, 0.0F);
    }

    public ItemStack getItemStack(net.minecraft.util.RandomSource random) {
        ResourceLocation resourceLocation = new ResourceLocation(this.itemId);
        Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);
        if (item != null) {
            int count = minCount + random.nextInt(maxCount - minCount + 1);
            return new ItemStack(item, count);
        }
        return ItemStack.EMPTY;
    }
}