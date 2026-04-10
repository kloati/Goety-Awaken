package com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.namelessquotes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class KillSpecialEnemyQuoteLoader extends
        SimplePreparableReloadListener<Map<ResourceLocation, List<KillSpecialEnemyQuoteLoader.SpecialEnemyQuoteConfig>>> {

    private static final String DATA_PATH = "kill_special_enemy_quotes";

    private Map<ResourceLocation, List<SpecialEnemyQuoteConfig>> quoteConfigs = new HashMap<>();

    public KillSpecialEnemyQuoteLoader() {

    }

    @Override
    protected Map<ResourceLocation, List<SpecialEnemyQuoteConfig>> prepare(ResourceManager resourceManager,
            ProfilerFiller profiler) {

        Map<ResourceLocation, List<SpecialEnemyQuoteConfig>> configs = new HashMap<>();

        try {

            net.minecraft.resources.FileToIdConverter filetoidconverter = net.minecraft.resources.FileToIdConverter
                    .json(DATA_PATH);

            var resources = filetoidconverter.listMatchingResources(resourceManager);

            for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
                ResourceLocation resLoc = entry.getKey();
                Resource resource = entry.getValue();

                try (InputStream inputStream = resource.open()) {
                    JsonElement jsonElement = JsonParser
                            .parseReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

                    if (jsonElement.isJsonObject()) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();

                        for (Map.Entry<String, JsonElement> jsonEntry : jsonObject.entrySet()) {
                            try {
                                String entityKey = jsonEntry.getKey();
                                JsonElement arrayElement = jsonEntry.getValue();
                                List<SpecialEnemyQuoteConfig> quoteList = new ArrayList<>();

                                if (arrayElement.isJsonArray()) {
                                    JsonArray array = arrayElement.getAsJsonArray();
                                    for (JsonElement quoteElement : array) {
                                        if (quoteElement.isJsonObject()) {
                                            JsonObject quoteObj = quoteElement.getAsJsonObject();
                                            String key = quoteObj.get("key").getAsString();
                                            float subtitles = quoteObj.has("subtitles")
                                                    ? quoteObj.get("subtitles").getAsFloat()
                                                    : 10.0f;
                                            quoteList.add(new SpecialEnemyQuoteConfig(key, subtitles));
                                        }
                                    }
                                }

                                ResourceLocation entityId = new ResourceLocation(entityKey);
                                configs.put(entityId, quoteList);

                            } catch (Exception e) {

                            }
                        }
                    }
                } catch (Exception e) {

                }
            }

        } catch (Exception e) {

        }

        return configs;
    }

    @Override
    protected void apply(Map<ResourceLocation, List<SpecialEnemyQuoteConfig>> configs, ResourceManager resourceManager,
            ProfilerFiller profiler) {
        this.quoteConfigs = configs;

    }

    public List<SpecialEnemyQuoteConfig> getQuotesForEntity(ResourceLocation entityId) {
        return quoteConfigs.getOrDefault(entityId, Collections.emptyList());
    }

    public boolean hasQuotesForEntity(ResourceLocation entityId) {
        return quoteConfigs.containsKey(entityId);
    }

    public Map<ResourceLocation, List<SpecialEnemyQuoteConfig>> getAllConfigs() {
        return Collections.unmodifiableMap(quoteConfigs);
    }

    public static class SpecialEnemyQuoteConfig {
        public final String key;
        public final float subtitles;

        public SpecialEnemyQuoteConfig(String key, float subtitles) {
            this.key = key;
            this.subtitles = subtitles;
        }
    }
}
