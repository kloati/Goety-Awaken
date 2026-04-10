package com.k1sak1.goetyawaken.client.events;

import com.k1sak1.goetyawaken.api.IAncientGlint;
import com.k1sak1.goetyawaken.common.entities.hostile.MushroomMonstrosityHostile;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.NamelessOne;
import com.k1sak1.goetyawaken.common.network.server.SBossBarPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomBossBarHandler {

    public static Map<UUID, Integer> BOSS_BAR_RENDER_TYPES = new HashMap<>();

    public static void handleBossBarPacket(UUID barId, int bossId, boolean remove, int renderType) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }

        Entity entity = minecraft.level.getEntity(bossId);
        if (!(entity instanceof Mob mob)) {
            return;
        }

        if (remove) {
            AncientBossBarEvent.removeAncientBossBar(barId, mob);
            NamelessOneBossBarEvent.removeNamelessOneBossBar(barId, mob);
            MushroomBossBarEvent.removeMushroomBossBar(barId, mob);
            BOSS_BAR_RENDER_TYPES.remove(barId);
        } else {
            switch (renderType) {
                case SBossBarPacket.RENDER_TYPE_ANCIENT:
                    if (mob instanceof IAncientGlint glint && glint.hasAncientGlint()
                            && "ancient".equals(glint.getGlintTextureType())) {
                        AncientBossBarEvent.addAncientBossBar(barId, mob);
                        BOSS_BAR_RENDER_TYPES.put(barId, renderType);
                    }
                    break;
                case SBossBarPacket.RENDER_TYPE_NAMELESS_ONE:
                    if (mob instanceof NamelessOne) {
                        NamelessOneBossBarEvent.addNamelessOneBossBar(barId, mob);
                        BOSS_BAR_RENDER_TYPES.put(barId, renderType);
                    }
                    break;
                case SBossBarPacket.RENDER_TYPE_MUSHROOM:
                    if (mob instanceof MushroomMonstrosityHostile) {
                        MushroomBossBarEvent.addMushroomBossBar(barId, mob);
                        BOSS_BAR_RENDER_TYPES.put(barId, renderType);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
