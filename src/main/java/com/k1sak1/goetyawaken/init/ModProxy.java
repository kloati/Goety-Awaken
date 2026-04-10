package com.k1sak1.goetyawaken.init;

import net.minecraft.world.entity.Mob;
import java.util.UUID;

public interface ModProxy {
    void addBossBar(UUID id, Mob mob);

    void removeBossBar(UUID id, Mob mob);
}
