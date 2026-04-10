package com.k1sak1.goetyawaken.common.items;

import com.k1sak1.goetyawaken.common.entities.item.EyeOfOverwatchItemEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Rarity;

public class EyeOfOverwatchItem extends Item {
    public EyeOfOverwatchItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant());
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(Level level, Entity entity, ItemStack stack) {
        if (entity instanceof ItemEntity itemEntity) {
            EyeOfOverwatchItemEntity customEntity = new EyeOfOverwatchItemEntity(level, entity.getX(), entity.getY(),
                    entity.getZ(), stack);
            customEntity.setDeltaMovement(entity.getDeltaMovement());
            return customEntity;
        }
        return null;
    }
}