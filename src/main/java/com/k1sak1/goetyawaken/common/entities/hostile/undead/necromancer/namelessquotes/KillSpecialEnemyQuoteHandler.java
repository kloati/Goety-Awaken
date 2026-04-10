package com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.namelessquotes;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import java.util.List;

public class KillSpecialEnemyQuoteHandler {
    private static KillSpecialEnemyQuoteLoader loader;

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        loader = new KillSpecialEnemyQuoteLoader();
        event.addListener(loader);
    }

    public static List<KillSpecialEnemyQuoteLoader.SpecialEnemyQuoteConfig> getQuotesForEntity(
            ResourceLocation entityId) {
        if (loader == null) {
            return List.of();
        }
        return loader.getQuotesForEntity(entityId);
    }

    public static boolean hasQuotesForEntity(ResourceLocation entityId) {
        if (loader == null) {
            return false;
        }
        return loader.hasQuotesForEntity(entityId);
    }
}
