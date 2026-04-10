package com.k1sak1.goetyawaken.common.entities.item;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EyeOfOverwatchItemEntity extends ItemEntity {
    private int floatTimer = 0;

    public EyeOfOverwatchItemEntity(EntityType<? extends ItemEntity> type, Level level) {
        super(type, level);
    }

    public EyeOfOverwatchItemEntity(Level level, double x, double y, double z, ItemStack stack) {
        super(level, x, y, z, stack);
        this.setPickUpDelay(30);
        this.setExtendedLifetime();
        this.setGlowingTag(true);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            this.setUnlimitedLifetime();
            this.setGlowingTag(true);
            this.setNoGravity(true);
        }
        this.hurtMarked = false;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isInLava() {
        return false;
    }
}