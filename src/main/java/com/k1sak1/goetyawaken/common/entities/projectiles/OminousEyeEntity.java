package com.k1sak1.goetyawaken.common.entities.projectiles;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class OminousEyeEntity extends EyeBaseEntity {

    public OminousEyeEntity(EntityType<? extends OminousEyeEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static OminousEyeEntity create(Level level, double x, double y, double z) {
        OminousEyeEntity entity = new OminousEyeEntity(
                com.k1sak1.goetyawaken.common.entities.ModEntityType.OMINOUS_EYE.get(),
                level);
        entity.setPos(x, y, z);
        return entity;
    }

    @Override
    public net.minecraft.network.chat.Component getName() {
        return net.minecraft.network.chat.Component.translatable("entity.goetyawaken.ominous_eye");
    }
}
