package com.k1sak1.goetyawaken.common.entities.ally.Integration;

import net.minecraft.world.entity.AnimationState;

public interface ICanBeAnimated {
    AnimationState getAnimationState(String name);

    default float getAnimationSpeed() {
        return 1.0F;
    }
}
