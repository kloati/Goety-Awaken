package com.k1sak1.goetyawaken.common.items.magic.grimoire;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AffixPool {
    private static final Gson GSON = new Gson();
    public static final String FOLDER = "grimoire_affixes";

    private static final List<AffixDefinition> DEFINITIONS = new ArrayList<>();

    public static class AffixDefinition {
        public final ResourceLocation attribute;
        public final AttributeModifier.Operation op;
        public final int weight;
        public final double[][] tierRanges;

        public AffixDefinition(ResourceLocation attribute, AttributeModifier.Operation op, int weight,
                double[][] tierRanges) {
            this.attribute = attribute;
            this.op = op;
            this.weight = weight;
            this.tierRanges = tierRanges;
        }
    }

    public static class ReloadListener extends SimpleJsonResourceReloadListener {
        public ReloadListener() {
            super(GSON, FOLDER);
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager manager,
                ProfilerFiller profiler) {
            List<AffixDefinition> loaded = new ArrayList<>();
            for (Map.Entry<ResourceLocation, JsonElement> entry : objects.entrySet()) {
                try {
                    JsonElement root = entry.getValue();
                    if (root.isJsonObject() && root.getAsJsonObject().has("affixes")
                            && root.getAsJsonObject().get("affixes").isJsonArray()) {
                        JsonArray array = root.getAsJsonObject().getAsJsonArray("affixes");
                        for (int i = 0; i < array.size(); i++) {
                            AffixDefinition definition = parseEntry(entry.getKey(), array.get(i));
                            if (definition != null) {
                                loaded.add(definition);
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
            DEFINITIONS.clear();
            DEFINITIONS.addAll(loaded);
        }
    }

    private static AffixDefinition parseEntry(ResourceLocation id, JsonElement element) {
        if (!element.isJsonObject()) {
            return null;
        }
        JsonObject obj = element.getAsJsonObject();
        if (!obj.has("attribute") || !obj.has("op") || !obj.has("weight") || !obj.has("tiers")) {
            return null;
        }
        ResourceLocation attr;
        try {
            attr = new ResourceLocation(obj.get("attribute").getAsString());
        } catch (Exception e) {
            return null;
        }
        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(attr);
        if (attribute == null) {
            for (Attribute candidate : ForgeRegistries.ATTRIBUTES) {
                ResourceLocation key = ForgeRegistries.ATTRIBUTES.getKey(candidate);
                if (key != null && key.getPath().equals(attr.getPath())) {
                    attribute = candidate;
                    break;
                }
            }
        }
        if (attribute == null) {
            return null;
        }
        AttributeModifier.Operation op = parseOp(obj.get("op").getAsString());
        if (op == null) {
            return null;
        }
        if (!obj.get("tiers").isJsonArray()) {
            return null;
        }
        JsonArray tiers = obj.getAsJsonArray("tiers");
        if (tiers.size() != 5) {
            return null;
        }
        double[][] ranges = new double[5][2];
        for (int t = 0; t < 5; t++) {
            JsonElement tierElement = tiers.get(t);
            if (!tierElement.isJsonObject() || !tierElement.getAsJsonObject().has("min")
                    || !tierElement.getAsJsonObject().has("max")) {
                return null;
            }
            JsonObject tierObj = tierElement.getAsJsonObject();
            double min = tierObj.get("min").getAsDouble();
            double max = tierObj.get("max").getAsDouble();
            if (min > max) {
                return null;
            }
            ranges[t][0] = min;
            ranges[t][1] = max;
        }
        Double cap = obj.has("cap") ? obj.get("cap").getAsDouble() : null;
        if (cap != null && ranges[4][1] > cap) {
            return null;
        }
        int weight = Math.max(1, obj.get("weight").getAsInt());
        return new AffixDefinition(attr, op, weight, ranges);
    }

    private static AttributeModifier.Operation parseOp(String op) {
        if ("addition".equals(op)) {
            return AttributeModifier.Operation.ADDITION;
        }
        if ("multiply_base".equals(op)) {
            return AttributeModifier.Operation.MULTIPLY_BASE;
        }
        return null;
    }

    public static AffixDefinition getRandomDefinition(RandomSource random) {
        if (DEFINITIONS.isEmpty()) {
            return null;
        }
        int total = 0;
        for (AffixDefinition definition : DEFINITIONS) {
            total += definition.weight;
        }
        int roll = random.nextInt(total);
        for (AffixDefinition definition : DEFINITIONS) {
            roll -= definition.weight;
            if (roll < 0) {
                return definition;
            }
        }
        return DEFINITIONS.get(DEFINITIONS.size() - 1);
    }

    public static boolean isEmpty() {
        return DEFINITIONS.isEmpty();
    }
}
