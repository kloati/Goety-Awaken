package com.k1sak1.goetyawaken.common.compat.touhoulittlemaid;

import net.minecraft.world.entity.LivingEntity;

public interface IHasPowerPointProxy {

    int getPowerPoint();

    default void dropPowerPoint(LivingEntity entity) {
    }
}