package com.k1sak1.goetyawaken.common.entities.projectiles;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class PrisonEyeEntity extends EyeBaseEntity {

    public PrisonEyeEntity(EntityType<? extends PrisonEyeEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public PrisonEyeEntity(EntityType<? extends PrisonEyeEntity> pEntityType, Level pLevel, double x, double y,
            double z) {
        super(pEntityType, pLevel);
        this.setPos(x, y, z);
    }

    public static PrisonEyeEntity create(Level level, double x, double y, double z) {
        PrisonEyeEntity entity = new PrisonEyeEntity(
                com.k1sak1.goetyawaken.common.entities.ModEntityType.PRISON_EYE.get(),
                level);
        entity.setPos(x, y, z);
        return entity;
    }

    public void setOwner(net.minecraft.world.entity.player.Player player) {
        super.setOwner(player);
    }

    @Override
    public net.minecraft.network.chat.Component getName() {
        return net.minecraft.network.chat.Component.translatable("entity.goetyawaken.prison_eye");
    }
}