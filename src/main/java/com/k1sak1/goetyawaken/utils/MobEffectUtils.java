package com.k1sak1.goetyawaken.utils;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.MobEffectEvent;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

public class MobEffectUtils {
    private static final MethodHandle ON_EFFECT_ADDED;
    private static final MethodHandle ON_EFFECT_UPDATED;
    private static final Field ACTIVE_EFFECTS_FIELD;

    static {
        MethodHandle onEffectAdded = null;
        MethodHandle onEffectUpdated = null;
        Field activeEffectsField = null;

        try {
            for (Field field : LivingEntity.class.getDeclaredFields()) {
                if (Map.class.isAssignableFrom(field.getType()) &&
                        field.getGenericType().toString().contains("MobEffect")) {
                    activeEffectsField = field;
                    break;
                }
            }
            if (activeEffectsField != null) {
                try {
                    ModuleAccess.addExports(LivingEntity.class.getModule(), LivingEntity.class.getPackageName(),
                            ModuleAccess.module(MobEffectUtils.class));
                    ModuleAccess.addOpens(LivingEntity.class.getModule(), LivingEntity.class.getPackageName(),
                            ModuleAccess.module(MobEffectUtils.class));
                } catch (Exception e) {
                }

                activeEffectsField.setAccessible(true);
            } else {
            }
            onEffectAdded = ModuleAccess.LOOKUP.findVirtual(LivingEntity.class, "onEffectAdded",
                    MethodType.methodType(void.class, MobEffectInstance.class, Entity.class));

            onEffectUpdated = ModuleAccess.LOOKUP.findVirtual(LivingEntity.class, "onEffectUpdated",
                    MethodType.methodType(void.class, MobEffectInstance.class, boolean.class, Entity.class));

        } catch (NoSuchMethodException | IllegalAccessException e) {

        }

        ON_EFFECT_ADDED = onEffectAdded;
        ON_EFFECT_UPDATED = onEffectUpdated;
        ACTIVE_EFFECTS_FIELD = activeEffectsField;
    }

    public static void forceAdd(LivingEntity target, MobEffectInstance effectInstance, @Nullable Entity sourceEntity) {
        try {
            MobEffectInstance existingEffect = target.getEffect(effectInstance.getEffect());
            MinecraftForge.EVENT_BUS
                    .post(new MobEffectEvent.Added(target, existingEffect, effectInstance, sourceEntity));

            if (ACTIVE_EFFECTS_FIELD != null) {
                @SuppressWarnings("unchecked")
                Map<MobEffect, MobEffectInstance> activeEffects = (Map<MobEffect, MobEffectInstance>) ACTIVE_EFFECTS_FIELD
                        .get(target);
                if (existingEffect == null) {
                    activeEffects.put(effectInstance.getEffect(), effectInstance);
                    if (ON_EFFECT_ADDED != null) {
                        ON_EFFECT_ADDED.invoke(target, effectInstance, sourceEntity);
                    }
                } else {
                    existingEffect.update(effectInstance);
                    activeEffects.put(effectInstance.getEffect(), effectInstance);
                    if (ON_EFFECT_UPDATED != null) {
                        ON_EFFECT_UPDATED.invoke(target, effectInstance, true, sourceEntity);
                    }
                    existingEffect.update(effectInstance);
                }
            } else {
                target.addEffect(effectInstance, sourceEntity);
            }
        } catch (Throwable throwable) {
            target.addEffect(effectInstance, sourceEntity);
            throwable.printStackTrace();
        }
    }
}