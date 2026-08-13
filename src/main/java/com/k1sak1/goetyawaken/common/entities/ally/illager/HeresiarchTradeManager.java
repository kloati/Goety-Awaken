package com.k1sak1.goetyawaken.common.entities.ally.illager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.k1sak1.goetyawaken.Config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber
public class HeresiarchTradeManager {
    private static List<HeresiarchTrade> tradeList = new ArrayList<>();

    static {
        initializeTrades();
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new TradeReloadListener());
    }

    public static class TradeReloadListener extends SimplePreparableReloadListener<List<HeresiarchTrade>> {
        @Override
        protected List<HeresiarchTrade> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
            List<HeresiarchTrade> newTradeList = new ArrayList<>();
            ResourceLocation resourceId = new ResourceLocation("goetyawaken", "trades/heresiarch_trades.json");
            if (resourceManager.getResource(resourceId).isPresent()) {
                try {
                    var resource = resourceManager.getResource(resourceId).get();
                    try (java.io.Reader reader = resource.openAsReader()) {
                        loadTradesFromReader(reader, newTradeList);
                    }
                } catch (Exception e) {
                }
            }
            return newTradeList;
        }

        @Override
        protected void apply(List<HeresiarchTrade> newTradeList, ResourceManager resourceManager,
                ProfilerFiller profiler) {
            tradeList.clear();
            tradeList.addAll(newTradeList);
        }
    }

    private static void initializeTrades() {
        loadTradesFromConfig();
    }

    private static void loadTradesFromConfig() {
        try {
            java.nio.file.Path configPath = net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get()
                    .resolve("goetyawaken_heresiarch_trades.json");
            if (java.nio.file.Files.exists(configPath)) {
                loadTradesFromFile(configPath.toFile());
            } else {
                java.io.InputStream inputStream = HeresiarchTradeManager.class.getClassLoader()
                        .getResourceAsStream("data/goetyawaken/trades/heresiarch_trades.json");
                if (inputStream != null) {
                    loadTradesFromInputStream(inputStream);
                }
            }
        } catch (Exception e) {
        }
    }

    private static void loadTradesFromFile(java.io.File configFile) {
        try (java.io.FileReader reader = new java.io.FileReader(configFile)) {
            loadTradesFromReader(reader);
        } catch (java.io.IOException e) {
        }
    }

    private static void loadTradesFromInputStream(java.io.InputStream inputStream) {
        try (java.io.InputStreamReader reader = new java.io.InputStreamReader(inputStream)) {
            loadTradesFromReader(reader);
        } catch (java.io.IOException e) {
        }
    }

    private static void loadTradesFromReader(java.io.Reader reader, List<HeresiarchTrade> targetList) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
        JsonArray tradesArray = jsonObject.getAsJsonArray("trades");

        for (JsonElement element : tradesArray) {
            JsonObject tradeObject = element.getAsJsonObject();
            String itemId = tradeObject.get("item_id").getAsString();
            int minCount = tradeObject.get("min_count").getAsInt();
            int maxCount = tradeObject.get("max_count").getAsInt();
            int weight = tradeObject.get("weight").getAsInt();
            int price = tradeObject.get("price").getAsInt();

            String nbtString = null;
            if (tradeObject.has("nbt")) {
                nbtString = tradeObject.get("nbt").getAsString();
            }

            String potionId = null;
            if (tradeObject.has("potion")) {
                potionId = tradeObject.get("potion").getAsString();
            }

            List<HeresiarchTrade.CustomEffect> customEffects = new ArrayList<>();
            if (tradeObject.has("custom_effects") && tradeObject.get("custom_effects").isJsonArray()) {
                JsonArray effectsArray = tradeObject.getAsJsonArray("custom_effects");
                for (JsonElement effElement : effectsArray) {
                    JsonObject eff = effElement.getAsJsonObject();
                    String effectId = eff.get("id").getAsString();
                    int amplifier = eff.has("amplifier") ? eff.get("amplifier").getAsInt() : 0;
                    int duration = eff.has("duration") ? eff.get("duration").getAsInt() : 3600;
                    boolean ambient = !eff.has("ambient") || eff.get("ambient").getAsBoolean();
                    boolean showParticles = !eff.has("show_particles") || eff.get("show_particles").getAsBoolean();
                    customEffects.add(new HeresiarchTrade.CustomEffect(effectId, amplifier, duration, ambient,
                            showParticles));
                }
            }

            Integer customColor = null;
            if (tradeObject.has("color")) {
                customColor = tradeObject.get("color").getAsInt();
            }

            String secondItemId = null;
            int secondPrice = 0;
            if (tradeObject.has("second_item_id") && tradeObject.has("second_price")) {
                secondItemId = tradeObject.get("second_item_id").getAsString();
                secondPrice = tradeObject.get("second_price").getAsInt();
            }

            targetList.add(new HeresiarchTrade(itemId, minCount, maxCount, weight, price, nbtString,
                    potionId, customEffects, customColor, secondItemId, secondPrice));
        }
    }

    private static void loadTradesFromReader(java.io.Reader reader) {
        loadTradesFromReader(reader, tradeList);
    }

    public static List<HeresiarchTrade> getAllTrades() {
        return new ArrayList<>(tradeList);
    }

    public static MerchantOffers generateOffers(RandomSource random, int tradeCount) {
        MerchantOffers offers = new MerchantOffers();
        List<HeresiarchTrade> availableTrades = getAllTrades();

        if (availableTrades.isEmpty()) {
            return offers;
        }

        Set<Integer> usedIndices = new HashSet<>();

        for (int i = 0; i < tradeCount; i++) {
            if (usedIndices.size() >= availableTrades.size()) {
                break;
            }

            int totalWeight = 0;
            for (int j = 0; j < availableTrades.size(); j++) {
                if (!usedIndices.contains(j)) {
                    totalWeight += availableTrades.get(j).getWeight();
                }
            }

            if (totalWeight <= 0) {
                break;
            }

            int randomValue = random.nextInt(totalWeight);
            int currentWeight = 0;
            int selectedIndex = -1;

            for (int j = 0; j < availableTrades.size(); j++) {
                if (!usedIndices.contains(j)) {
                    currentWeight += availableTrades.get(j).getWeight();
                    if (randomValue < currentWeight) {
                        selectedIndex = j;
                        break;
                    }
                }
            }

            if (selectedIndex >= 0 && !usedIndices.contains(selectedIndex)) {
                usedIndices.add(selectedIndex);
                HeresiarchTrade selectedTrade = availableTrades.get(selectedIndex);
                offers.add(selectedTrade.createMerchantOffer(random));
            }
        }

        return offers;
    }

    public static class HeresiarchTrade {
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

        public HeresiarchTrade(String itemId, int minCount, int maxCount, int weight, int price,
                String nbtString, String potionId, List<CustomEffect> customEffects, Integer customColor,
                String secondItemId, int secondPrice) {
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
                            Config.heresiarchServantTradeMaxUses, Config.heresiarchServantTradeMaxUses,
                            0.0F);
                }
            }

            return new MerchantOffer(firstItemStack, resultStack, Config.heresiarchServantTradeMaxUses,
                    Config.heresiarchServantTradeMaxUses, 0.0F);
        }

        public ItemStack getItemStack(RandomSource random) {
            ResourceLocation resourceLocation = new ResourceLocation(this.itemId);
            Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);
            if (item != null) {
                int count = minCount + random.nextInt(maxCount - minCount + 1);
                ItemStack stack = new ItemStack(item, count);
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
    }
}
