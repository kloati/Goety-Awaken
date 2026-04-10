package com.k1sak1.goetyawaken.utils;

import com.k1sak1.goetyawaken.GoetyAwaken;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class AlternativeTagChecker {
    private static final Map<String, Set<ResourceLocation>> MANUAL_TAGS = new HashMap<>();

    static {
        initializeManualTags();
    }

    private static void initializeManualTags() {
        Set<ResourceLocation> dauntlessItems = new HashSet<>();
        dauntlessItems.add(GoetyAwaken.location("claymore"));
        dauntlessItems.add(GoetyAwaken.location("obsidian_claymore"));
        dauntlessItems.add(GoetyAwaken.location("starless_night"));
        addItemsToTag(dauntlessItems, "goety", "blade_of_ender");
        addItemsToTag(dauntlessItems, "dungeons_gear", "claymore");
        addItemsToTag(dauntlessItems, "mcdw", "sword_claymore", "sword_the_starless_night");
        addItemsToTag(dauntlessItems, "irons_spellbooks", "claymore", "boreal_blade", "spellbreaker",
                "legionnaire_flamberge", "keeper_flamberge");
        addItemsToTag(dauntlessItems, "simplyswords", "iron_claymore", "gold_claymore", "diamond_claymore",
                "netherite_claymore", "runic_claymore", "watcher_claymore", "brimstone_claymore",
                "waking_lichblade", "awakened_lichblade");
        addItemsToTag(dauntlessItems, "legendary_monsters", "soul_great_sword", "the_great_frost", "chorus_blade");
        addItemsToTag(dauntlessItems, "twilightforest", "giant_sword");
        addItemsToTag(dauntlessItems, "savageandravage", "cleaver_of_beheading");
        addItemsToTag(dauntlessItems, "radiation_zone_reborn", "dustorm_greatsword");
        addItemsToTag(dauntlessItems, "tconstruct", "cleaver");
        addItemsToTag(dauntlessItems, "born_in_chaos_v1", "darkwarblade");
        addItemsToTag(dauntlessItems, "manametalmod", "true_ancient_thulium_sword");
        addItemsToTag(dauntlessItems, "aoa3", "runic_greatblade");
        addItemsToTag(dauntlessItems, "enigmaticlegacy", "etherium_sword");
        addItemsToTag(dauntlessItems, "cataclysm", "the_incinerator");
        addItemsToTag(dauntlessItems, "goetydelight", "starless_night");
        addItemsToTag(dauntlessItems, "spore", "greatsword");
        addItemsToTag(dauntlessItems, "species", "spectralibur");
        addItemsToTag(dauntlessItems, "armamentarium", "dragonslayer");

        MANUAL_TAGS.put("dauntless_glove_boost", dauntlessItems);
        Set<ResourceLocation> assassinItems = new HashSet<>();
        assassinItems.add(GoetyAwaken.location("truth_seeker"));
        addItemsToTag(assassinItems, "goety", "fanged_dagger", "hungry_dagger");
        addItemsToTag(assassinItems, "cataclysm", "athame");
        addItemsToTag(assassinItems, "irons_spellbooks", "amethyst_rapier");
        addItemsToTag(assassinItems, "torchesbecomesunlight", "winter_pass");
        addItemsToTag(assassinItems, "alexscaves", "desolate_dagger");
        addItemsToTag(assassinItems, "graveyard", "bone_dagger");
        addItemsToTag(assassinItems, "mahoutsukai", "dagger");
        addItemsToTag(assassinItems, "epicfight", "iron_dagger", "golden_dagger", "netherite_dagger", "diamond_dagger");
        addItemsToTag(assassinItems, "born_in_chaos_v1", "intoxicating_dagger");

        MANUAL_TAGS.put("assassin_glove_boost", assassinItems);
    }

    private static void addItemsToTag(Set<ResourceLocation> tagSet, String namespace, String... paths) {
        for (String path : paths) {
            tagSet.add(new ResourceLocation(namespace, path));
        }
    }

    public static boolean isItemInDauntlessBoostTag(ItemStack stack) {
        return isItemInTag(stack, "dauntless_glove_boost");
    }

    public static boolean isItemInAssassinBoostTag(ItemStack stack) {
        return isItemInTag(stack, "assassin_glove_boost");
    }

    private static boolean isItemInTag(ItemStack stack, String tagName) {
        Item item = stack.getItem();
        ResourceLocation itemLocation = ForgeRegistries.ITEMS.getKey(item);

        if (itemLocation == null) {
            return false;
        }

        Set<ResourceLocation> tagItems = MANUAL_TAGS.get(tagName);
        if (tagItems == null) {
            return false;
        }

        return tagItems.contains(itemLocation);
    }
}