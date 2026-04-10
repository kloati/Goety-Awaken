package com.k1sak1.goetyawaken.utils;

import com.k1sak1.goetyawaken.GoetyAwaken;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class ModDamageSource extends DamageSource {
    public static ResourceKey<DamageType> MUSHROOM_MISSILE = create("mushroom_missile");
    public static ResourceKey<DamageType> EXPLOSIVE_ARROW = create("explosive_arrow");
    public static ResourceKey<DamageType> DEATH_FIRE = create("death_fire");
    public static ResourceKey<DamageType> PURE_LIGHT = create("pure_light");

    public ModDamageSource(Holder<DamageType> p_270906_, @Nullable Entity p_270796_, @Nullable Entity p_270459_,
            @Nullable Vec3 p_270623_) {
        super(p_270906_, p_270796_, p_270459_, p_270623_);
    }

    public static ResourceKey<DamageType> create(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, GoetyAwaken.location(name));
    }

    public static DamageSource getDamageSource(Level level, ResourceKey<DamageType> type, EntityType<?>... toIgnore) {
        return getEntityDamageSource(level, type, null, toIgnore);
    }

    public static DamageSource entityDamageSource(Level level, ResourceKey<DamageType> type, @Nullable Entity attacker,
            EntityType<?>... toIgnore) {
        return getEntityDamageSource(level, type, attacker);
    }

    public static DamageSource getEntityDamageSource(Level level, ResourceKey<DamageType> type,
            @Nullable Entity attacker, EntityType<?>... toIgnore) {
        return getIndirectEntityDamageSource(level, type, attacker, attacker, toIgnore);
    }

    public static DamageSource indirectEntityDamageSource(Level level, ResourceKey<DamageType> type,
            @Nullable Entity attacker, @Nullable Entity indirectAttacker) {
        return getIndirectEntityDamageSource(level, type, attacker, indirectAttacker);
    }

    public static DamageSource getIndirectEntityDamageSource(Level level, ResourceKey<DamageType> type,
            @Nullable Entity attacker, @Nullable Entity indirectAttacker, EntityType<?>... toIgnore) {
        return toIgnore.length > 0
                ? new EntityExcludedDamageSource(
                        level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type), toIgnore)
                : source(level, type, attacker, indirectAttacker);
    }

    public static DamageSource source(Level level, ResourceKey<DamageType> type, @Nullable Entity attacker,
            @Nullable Entity indirectAttacker) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type),
                attacker, indirectAttacker);
    }

    public static DamageSource mushroomMissile(Entity pSource, @Nullable Entity pIndirectEntity) {
        return indirectEntityDamageSource(pSource.level(), MUSHROOM_MISSILE, pSource, pIndirectEntity);
    }

    public static DamageSource explosiveArrow(Entity pSource, @Nullable Entity pIndirectEntity) {
        return indirectEntityDamageSource(pSource.level(), EXPLOSIVE_ARROW, pSource, pIndirectEntity);
    }

    public static DamageSource deathFire(Entity pSource, @Nullable Entity pIndirectEntity) {
        return indirectEntityDamageSource(pSource.level(), DEATH_FIRE, pSource, pIndirectEntity);
    }

    public static DamageSource pureLight(Entity pSource, @Nullable Entity pIndirectEntity) {
        return indirectEntityDamageSource(pSource.level(), PURE_LIGHT, pSource, pIndirectEntity);
    }

    public static DamageSource lifeLeech(Level level) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(com.Polarice3.Goety.utils.ModDamageSource.LIFE_LEECH));
    }

    public static boolean isDeathFire(DamageSource source) {
        return source != null && source.is(DEATH_FIRE);
    }

    public static boolean isPureLight(DamageSource source) {
        return source != null && source.is(PURE_LIGHT);
    }

    public static class EntityExcludedDamageSource extends DamageSource {

        protected final List<EntityType<?>> entities;

        public EntityExcludedDamageSource(Holder<DamageType> type, EntityType<?>... entities) {
            super(type);
            this.entities = Arrays.stream(entities).toList();
        }

        @Override
        public Component getLocalizedDeathMessage(LivingEntity living) {
            LivingEntity livingentity = living.getKillCredit();
            String s = "death.attack." + this.type().msgId();
            String s1 = s + ".player";
            if (livingentity != null) {
                for (EntityType<?> entity : entities) {
                    if (livingentity.getType() == entity) {
                        return Component.translatable(s, living.getDisplayName());
                    }
                }
            }
            return livingentity != null
                    ? Component.translatable(s1, living.getDisplayName(), livingentity.getDisplayName())
                    : Component.translatable(s, living.getDisplayName());
        }
    }

    public static void bootstrap(BootstapContext<DamageType> context) {
        context.register(MUSHROOM_MISSILE, new DamageType("goetyawaken.mushroom_missile", 0.0F));
        context.register(EXPLOSIVE_ARROW, new DamageType("goetyawaken.explosive_arrow", 0.0F));
        context.register(DEATH_FIRE, new DamageType("goetyawaken.death_fire", 0.0F));
        context.register(PURE_LIGHT, new DamageType("goetyawaken.pure_light", 0.0F));
    }
}