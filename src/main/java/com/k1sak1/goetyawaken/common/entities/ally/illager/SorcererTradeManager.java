package com.k1sak1.goetyawaken.common.entities.ally.illager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

public class SorcererTradeManager {
    private static List<SorcererTrade> tradeList = new ArrayList<>();

    static {
        initializeTrades();
    }

    private static void initializeTrades() {
        loadTradesFromConfig();
    }
    
    private static void loadTradesFromConfig() {
        try {
            java.nio.file.Path configPath = net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get().resolve("goetyawaken_sorcerer_trades.json");
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
    
    private static void loadTradesFromReader(java.io.Reader reader) {
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
            
            tradeList.add(new SorcererTrade(itemId, minLevel, maxLevel, minCount, maxCount, weight, price));
        }
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
}