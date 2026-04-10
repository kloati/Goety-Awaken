package com.k1sak1.goetyawaken.common.entities.projectiles;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class MirageEyeEntity extends EyeBaseEntity {

    public MirageEyeEntity(EntityType<? extends MirageEyeEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static MirageEyeEntity create(Level level, double x, double y, double z) {
        MirageEyeEntity entity = new MirageEyeEntity(
                com.k1sak1.goetyawaken.common.entities.ModEntityType.MIRAGE_EYE.get(),
                level);
        entity.setPos(x, y, z);
        return entity;
    }

    @Override
    public net.minecraft.network.chat.Component getName() {
        return net.minecraft.network.chat.Component.translatable("entity.goetyawaken.mirage_eye");
    }
}