package com.k1sak1.goetyawaken.client.events;

import com.k1sak1.goetyawaken.GoetyAwaken;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = GoetyAwaken.MODID, value = Dist.CLIENT)
public class AncientBossBarEvent {

    public static final ResourceLocation ANCIENT_BOSS_BAR = GoetyAwaken.location(
            "textures/gui/ancient_hunt.png");

    public static Map<UUID, Mob> ANCIENT_BOSS_BARS = new HashMap<>();

    private static void cleanInvalidEntries() {
        ANCIENT_BOSS_BARS.entrySet().removeIf(entry -> {
            Mob mob = entry.getValue();
            return mob == null || mob.isRemoved();
        });
    }

    @SubscribeEvent
    public static void renderBossBar(CustomizeGuiOverlayEvent.BossEventProgress event) {
        cleanInvalidEntries();
    }

    public static void addAncientBossBar(UUID id, Mob mob) {
        ANCIENT_BOSS_BARS.put(id, mob);
    }

    public static void removeAncientBossBar(UUID id, Mob mob) {
        ANCIENT_BOSS_BARS.remove(id, mob);
    }

    public static void clearAll() {
        ANCIENT_BOSS_BARS.clear();
    }
}
