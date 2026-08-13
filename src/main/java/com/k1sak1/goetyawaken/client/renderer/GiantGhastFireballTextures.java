package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class GiantGhastFireballTextures {
    public static final Map<Integer, ResourceLocation> TEXTURES = Util.make(Maps.newHashMap(), (map) -> {
        for (int i = 0; i < 32; i++) {
            map.put(i, location("hell_blast" + (i + 1) + ".png"));
        }
    });

    public static ResourceLocation location(String path) {
        return GoetyAwaken.location("textures/entity/projectiles/giant_ghast_fireball/" + path);
    }
}
