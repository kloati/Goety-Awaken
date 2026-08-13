package com.k1sak1.goetyawaken.common.entities.ally.illager;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.k1sak1.goetyawaken.Config;

@Mod.EventBusSubscriber
public class SorcererTradeManager {
    private static List<SorcererTrade> tradeList = new ArrayList<>();

    static {
        initializeTrades();
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new TradeReloadListener());
    }

    public static class TradeReloadListener extends SimplePreparableReloadListener<List<SorcererTrade>> {
        @Override
        protected List<SorcererTrade> prepare(ResourceManager resourceManager,
                net.minecraft.util.profiling.ProfilerFiller profiler) {
            List<SorcererTrade> newTradeList = new ArrayList<>();
            ResourceLocation resourceId = new ResourceLocation("goetyawaken", "trades/sorcerer_trades.json");
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
        protected void apply(List<SorcererTrade> newTradeList, ResourceManager resourceManager,
                net.minecraft.util.profiling.ProfilerFiller profiler) {
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
                    .resolve("goetyawaken_sorcerer_trades.json");
            if (java.nio.file.Files.exists(configPath)) {
                loadTradesFromFile(configPath.toFile());
            } else {
                java.io.InputStream inputStream = SorcererTradeManager.class.getClassLoader()
                        .getResourceAsStream("data/goetyawaken/trades/sorcerer_trades.json");
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

    private static void loadTradesFromReader(java.io.Reader reader, List<SorcererTrade> targetList) {
        com.google.gson.Gson gson = new com.google.gson.Gson();
        com.google.gson.JsonObject jsonObject = gson.fromJson(reader, com.google.gson.JsonObject.class);
        com.google.gson.JsonArray tradesArray = jsonObject.getAsJsonArray("trades");

        for (com.google.gson.JsonElement element : tradesArray) {
            com.google.gson.JsonObject tradeObject = element.getAsJsonObject();
            String itemId = tradeObject.get("item_id").getAsString();
            int minLevel = tradeObject.get("min_level").getAsInt();
            int maxLevel = tradeObject.get("max_level").getAsInt();
            int minCount = tradeObject.get("min_count").getAsInt();
            int maxCount = tradeObject.get("max_count").getAsInt();
            int weight = tradeObject.get("weight").getAsInt();
            int price = tradeObject.get("price").getAsInt();

            String firstItemId = null;
            if (tradeObject.has("first_item_id")) {
                firstItemId = tradeObject.get("first_item_id").getAsString();
            }

            String secondItemId = null;
            int secondPrice = 0;
            if (tradeObject.has("second_item_id") && tradeObject.has("second_price")) {
                secondItemId = tradeObject.get("second_item_id").getAsString();
                secondPrice = tradeObject.get("second_price").getAsInt();
            }

            targetList.add(new SorcererTrade(itemId, minLevel, maxLevel, minCount, maxCount, weight, price,
                    firstItemId, secondItemId, secondPrice));
        }
    }

    private static void loadTradesFromReader(java.io.Reader reader) {
        loadTradesFromReader(reader, tradeList);
    }

    public static List<SorcererTrade> getAvailableTrades(int level, int money) {
        List<SorcererTrade> availableTrades = new ArrayList<>();
        for (SorcererTrade trade : tradeList) {
            if (trade.isAvailableForLevel(level) && trade.isAffordable(money)) {
                availableTrades.add(trade);
            }
        }
        return availableTrades;
    }

    public static SorcererTrade getRandomTrade(List<SorcererTrade> availableTrades, RandomSource random) {
        if (availableTrades.isEmpty()) {
            return null;
        }

        int totalWeight = 0;
        for (SorcererTrade trade : availableTrades) {
            totalWeight += trade.getWeight();
        }

        if (totalWeight <= 0) {
            return null;
        }

        int randomValue = random.nextInt(totalWeight);
        int currentWeight = 0;

        for (SorcererTrade trade : availableTrades) {
            currentWeight += trade.getWeight();
            if (randomValue < currentWeight) {
                return trade;
            }
        }

        return availableTrades.get(0);
    }

    public static List<SorcererTrade> getAllTrades() {
        return new ArrayList<>(tradeList);
    }

    public static List<SorcererTrade> getAvailableTradesForLevel(int level) {
        List<SorcererTrade> availableTrades = new ArrayList<>();
        for (SorcererTrade trade : tradeList) {
            if (trade.isAvailableForLevel(level)) {
                availableTrades.add(trade);
            }
        }
        return availableTrades;
    }

    public static MerchantOffers generateOffersForLevel(int level, RandomSource random, int tradeCount) {
        MerchantOffers offers = new MerchantOffers();
        List<SorcererTrade> availableTrades = getAvailableTradesForLevel(level);

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
                SorcererTrade selectedTrade = availableTrades.get(selectedIndex);
                offers.add(selectedTrade.createMerchantOffer(random));
            }
        }

        return offers;
    }
}