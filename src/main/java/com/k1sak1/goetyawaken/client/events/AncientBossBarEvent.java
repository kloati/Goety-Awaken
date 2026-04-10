package com.k1sak1.goetyawaken.client.events;

import com.k1sak1.goetyawaken.GoetyAwaken;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AncientBossBarEvent {

    public static final ResourceLocation ANCIENT_BOSS_BAR = GoetyAwaken.location(
            "textures/gui/ancient_hunt.png");

    public static Map<UUID, Mob> ANCIENT_BOSS_BARS = new HashMap<>();

    public static void addAncientBossBar(UUID id, Mob mob) {
        ANCIENT_BOSS_BARS.put(id, mob);
    }

    public static void removeAncientBossBar(UUID id, Mob mob) {
        ANCIENT_BOSS_BARS.remove(id, mob);
    }
}
