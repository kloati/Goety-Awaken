package com.k1sak1.goetyawaken.common.items.magic.grimoire;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GrimoireValueRegistry {
    private static final Gson GSON = new Gson();
    public static final String FOLDER = "grimoire_value";

    private static final Map<ResourceLocation, Integer> ITEM_VALUES = new HashMap<>();
    private static final Map<ResourceLocation, Integer> TAG_VALUES = new HashMap<>();

    public static class ReloadListener extends SimpleJsonResourceReloadListener {
        public ReloadListener() {
            super(GSON, FOLDER);
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager manager,
                ProfilerFiller profiler) {
            Map<ResourceLocation, Integer> items = new HashMap<>();
            Map<ResourceLocation, Integer> tags = new HashMap<>();
            for (Map.Entry<ResourceLocation, JsonElement> entry : objects.entrySet()) {
                try {
                    if (!entry.getValue().isJsonObject()) {
                        continue;
                    }
                    JsonObject json = entry.getValue().getAsJsonObject();
                    if (json.has("items")) {
                        JsonElement itemsElement = json.get("items");
                        if (itemsElement.isJsonArray()) {
                            JsonArray itemArray = itemsElement.getAsJsonArray();
                            for (int i = 0; i < itemArray.size(); i++) {
                                JsonElement element = itemArray.get(i);
                                if (!element.isJsonObject() || !element.getAsJsonObject().has("id")
                                        || !element.getAsJsonObject().has("value")) {
                                    continue;
                                }
                                JsonObject itemObj = element.getAsJsonObject();
                                ResourceLocation id = new ResourceLocation(itemObj.get("id").getAsString());
                                if (!ForgeRegistries.ITEMS.containsKey(id)) {
                                    continue;
                                }
                                items.put(id, Math.max(0, itemObj.get("value").getAsInt()));
                            }
                        } else if (itemsElement.isJsonObject()) {
                            JsonObject itemObj = itemsElement.getAsJsonObject();
                            for (String key : itemObj.keySet()) {
                                try {
                                    items.put(new ResourceLocation(key), Math.max(0, itemObj.get(key).getAsInt()));
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                    if (json.has("tags")) {
                        JsonElement tagsElement = json.get("tags");
                        if (tagsElement.isJsonArray()) {
                            JsonArray tagArray = tagsElement.getAsJsonArray();
                            for (int i = 0; i < tagArray.size(); i++) {
                                JsonElement element = tagArray.get(i);
                                if (!element.isJsonObject() || !element.getAsJsonObject().has("id")
                                        || !element.getAsJsonObject().has("value")) {
                                    continue;
                                }
                                JsonObject tagObj = element.getAsJsonObject();
                                ResourceLocation id = new ResourceLocation(tagObj.get("id").getAsString());
                                TagKey<Item> tagKey = TagKey.create(Registries.ITEM, id);
                                if (ForgeRegistries.ITEMS.tags() == null
                                        || !ForgeRegistries.ITEMS.tags().isKnownTagName(tagKey)) {
                                    continue;
                                }
                                tags.put(id, Math.max(0, tagObj.get("value").getAsInt()));
                            }
                        } else if (tagsElement.isJsonObject()) {
                            JsonObject tagObj = tagsElement.getAsJsonObject();
                            for (String key : tagObj.keySet()) {
                                try {
                                    tags.put(new ResourceLocation(key), Math.max(0, tagObj.get(key).getAsInt()));
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
            ITEM_VALUES.clear();
            ITEM_VALUES.putAll(items);
            TAG_VALUES.clear();
            TAG_VALUES.putAll(tags);
        }
    }

    public static int getValue(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (itemId != null) {
            Integer itemValue = ITEM_VALUES.get(itemId);
            if (itemValue != null) {
                return itemValue;
            }
            for (Map.Entry<ResourceLocation, Integer> entry : TAG_VALUES.entrySet()) {
                TagKey<Item> tagKey = TagKey.create(Registries.ITEM, entry.getKey());
                ITag<Item> tag = ForgeRegistries.ITEMS.tags().getTag(tagKey);
                if (tag != null && tag.contains(stack.getItem())) {
                    return entry.getValue();
                }
            }
        }
        return fallbackValue(stack.getItem());
    }

    public static int sumValues(Collection<ItemStack> stacks) {
        int total = 0;
        for (ItemStack stack : stacks) {
            total += getValue(stack);
        }
        return total;
    }

    private static int fallbackValue(Item item) {
        Rarity rarity = item.getRarity(ItemStack.EMPTY);
        int value;
        switch (rarity) {
            case EPIC:
                value = 12;
                break;
            case RARE:
                value = 9;
                break;
            case UNCOMMON:
                value = 6;
                break;
            default:
                value = 3;
                break;
        }
        return value;
    }
}
