package com.k1sak1.goetyawaken.common.research;

import com.google.common.collect.Maps;
import com.Polarice3.Goety.common.research.Research;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class ResearchList {
    public static Map<String, Research> RESEARCH_LIST = Maps.newHashMap();
    public static Research ROYAL = new Research("royal");

    public static void registerResearch(String id, Research research) {
        RESEARCH_LIST.put(id, research);
    }

    public static Map<String, Research> getResearchList() {
        Map<String, Research> researches = Maps.newHashMap();
        researches.put(ROYAL.getId(), ROYAL);
        if (!RESEARCH_LIST.isEmpty()) {
            researches.putAll(RESEARCH_LIST);
        }
        return researches;
    }

    public static Map<ResourceLocation, Research> getResearchIdList() {
        Map<ResourceLocation, Research> researches = Maps.newHashMap();
        for (Research research : getResearchList().values()) {
            researches.put(research.getLocation(), research);
        }
        return researches;
    }

    public static Research getResearch(ResourceLocation resourceLocation) {
        if (getResearchIdList().containsKey(resourceLocation)) {
            return getResearchIdList().get(resourceLocation);
        }
        return null;
    }

    public static Research getResearch(String id) {
        if (getResearchList().containsKey(id)) {
            return getResearchList().get(id);
        }
        return null;
    }
}