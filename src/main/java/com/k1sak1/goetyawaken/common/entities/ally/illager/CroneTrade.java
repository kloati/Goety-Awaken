package com.k1sak1.goetyawaken.common.entities.ally.illager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.registries.ForgeRegistries;
import com.Polarice3.Goety.common.effects.brew.BrewEffectInstance;
import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.Polarice3.Goety.utils.BrewUtils;
import com.k1sak1.goetyawaken.Config;

import java.util.ArrayList;
import java.util.List;

public class CroneTrade {
    private final String itemId;
    private final int minCount;
    private final int maxCount;
    private final int weight;
    private final int price;
    private final String nbtString;
    private final String potionId;
    private final List<CustomEffect> customEffects;
    private final Integer customColor;
    private final String secondItemId;
    private final int secondPrice;
    private final boolean isBrewTrade;
    private final String brewTypeId;
    private final List<BrewCustomEffect> brewEffects;

    public CroneTrade(String itemId, int minCount, int maxCount, int weight, int price,
            String nbtString, String potionId, List<CustomEffect> customEffects, Integer customColor,
            String secondItemId, int secondPrice) {
        this(itemId, minCount, maxCount, weight, price, nbtString, potionId, customEffects, customColor,
                secondItemId, secondPrice, false, null, new ArrayList<>());
    }

    public CroneTrade(String itemId, int minCount, int maxCount, int weight, int price,
            String nbtString, String potionId, List<CustomEffect> customEffects, Integer customColor,
            String secondItemId, int secondPrice,
            boolean isBrewTrade, String brewTypeId, List<BrewCustomEffect> brewEffects) {
        this.itemId = itemId;
        this.minCount = minCount;
        this.maxCount = maxCount;
        this.weight = weight;
        this.price = price;
        this.nbtString = nbtString;
        this.potionId = potionId;
        this.customEffects = customEffects != null ? customEffects : new ArrayList<>();
        this.customColor = customColor;
        this.secondItemId = secondItemId;
        this.secondPrice = secondPrice;
        this.isBrewTrade = isBrewTrade;
        this.brewTypeId = brewTypeId;
        this.brewEffects = brewEffects != null ? brewEffects : new ArrayList<>();
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

    public MerchantOffer createMerchantOffer(RandomSource random) {
        int fullPrice = this.price;
        ItemStack firstItemStack = new ItemStack(Items.EMERALD, fullPrice);
        ItemStack resultStack = this.getItemStack(random);

        if (this.secondItemId != null && !this.secondItemId.isEmpty() && this.secondPrice > 0) {
            ResourceLocation secondResourceLocation = new ResourceLocation(this.secondItemId);
            Item secondItem = ForgeRegistries.ITEMS.getValue(secondResourceLocation);
            if (secondItem != null) {
                ItemStack secondItemStack = new ItemStack(secondItem, this.secondPrice);
                return new MerchantOffer(firstItemStack, secondItemStack, resultStack,
                        Config.croneServantTradeMaxUses, Config.croneServantTradeMaxUses,
                        0.0F);
            }
        }

        return new MerchantOffer(firstItemStack, resultStack, Config.croneServantTradeMaxUses,
                Config.croneServantTradeMaxUses, 0.0F);
    }

    public ItemStack getItemStack(RandomSource random) {
        ResourceLocation resourceLocation = new ResourceLocation(this.itemId);
        Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);
        if (item != null) {
            int count = minCount + random.nextInt(maxCount - minCount + 1);
            ItemStack stack = new ItemStack(item, count);
            if (this.isBrewTrade && !this.brewEffects.isEmpty()) {
                return createBrewStack(stack, random);
            }
            if (this.potionId != null && !this.potionId.isEmpty()) {
                ResourceLocation potionResource = new ResourceLocation(this.potionId);
                Potion potion = ForgeRegistries.POTIONS.getValue(potionResource);
                if (potion != null) {
                    PotionUtils.setPotion(stack, potion);
                }
            }

            if (!this.customEffects.isEmpty()) {
                List<MobEffectInstance> effects = new ArrayList<>();
                for (CustomEffect ce : this.customEffects) {
                    ResourceLocation effectRes = new ResourceLocation(ce.id);
                    MobEffect mobEffect = ForgeRegistries.MOB_EFFECTS.getValue(effectRes);
                    if (mobEffect != null) {
                        effects.add(new MobEffectInstance(mobEffect, ce.duration, ce.amplifier, ce.ambient,
                                ce.showParticles));
                    }
                }
                if (!effects.isEmpty()) {
                    PotionUtils.setCustomEffects(stack, effects);
                }
            }

            if (this.customColor != null) {
                CompoundTag displayTag = stack.getOrCreateTagElement("display");
                displayTag.putInt("CustomPotionColor", this.customColor);
            }

            if (this.nbtString != null && !this.nbtString.isEmpty()) {
                try {
                    CompoundTag tag = TagParser.parseTag(this.nbtString);
                    stack.setTag(tag);
                } catch (Exception e) {
                }
            }
            return stack;
        }
        return ItemStack.EMPTY;
    }

    private ItemStack createBrewStack(ItemStack baseStack, RandomSource random) {
        List<MobEffectInstance> mobEffects = new ArrayList<>();
        List<BrewEffectInstance> brewEffects = new ArrayList<>();

        for (BrewCustomEffect bce : this.brewEffects) {
            if (bce.isBrewEffect) {
                try {
                    String fullBrewEffectId = bce.id.contains(".") ? bce.id : "effect.goety." + bce.id;
                    BrewEffect brewEffect = com.Polarice3.Goety.common.effects.brew.BrewEffects.INSTANCE
                            .getBrewEffect(fullBrewEffectId);
                    if (brewEffect != null) {
                        brewEffects.add(new BrewEffectInstance(brewEffect, bce.duration, bce.amplifier));
                    }
                } catch (Exception e) {
                }
            } else {
                try {
                    ResourceLocation effectRes = new ResourceLocation(bce.id);
                    MobEffect mobEffect = ForgeRegistries.MOB_EFFECTS.getValue(effectRes);
                    if (mobEffect != null) {
                        mobEffects.add(new MobEffectInstance(mobEffect, bce.duration, bce.amplifier));
                    }
                } catch (Exception e) {
                }
            }
        }

        if (!mobEffects.isEmpty() || !brewEffects.isEmpty()) {
            baseStack = BrewUtils.setCustomEffects(baseStack, mobEffects, brewEffects);
            baseStack.getOrCreateTag().putInt("CustomPotionColor",
                    BrewUtils.getColor(mobEffects, brewEffects));
        }

        return baseStack;
    }

    public static class CustomEffect {
        public final String id;
        public final int amplifier;
        public final int duration;
        public final boolean ambient;
        public final boolean showParticles;

        public CustomEffect(String id, int amplifier, int duration, boolean ambient, boolean showParticles) {
            this.id = id;
            this.amplifier = amplifier;
            this.duration = duration;
            this.ambient = ambient;
            this.showParticles = showParticles;
        }
    }

    public static class BrewCustomEffect {
        public final String id;
        public final int amplifier;
        public final int duration;
        public final boolean isBrewEffect;

        public BrewCustomEffect(String id, int amplifier, int duration, boolean isBrewEffect) {
            this.id = id;
            this.amplifier = amplifier;
            this.duration = duration;
            this.isBrewEffect = isBrewEffect;
        }
    }
}
