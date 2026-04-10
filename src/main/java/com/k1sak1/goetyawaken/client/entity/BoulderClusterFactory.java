package com.k1sak1.goetyawaken.client.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;

public class BoulderClusterFactory {

    @SuppressWarnings("unchecked")
    public static ClientBlockClusterEntity make(PlayMessages.SpawnEntity packet, Level level) {
        EntityType<ClientBlockClusterEntity> type = (EntityType<ClientBlockClusterEntity>) (EntityType<?>) ModEntityType.BOULDER_CLUSTER
                .get();
        return new ClientBlockClusterEntity(type, level);
    }
}
