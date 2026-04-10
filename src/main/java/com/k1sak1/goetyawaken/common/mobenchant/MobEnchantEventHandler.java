package com.k1sak1.goetyawaken.common.mobenchant;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.network.server.SMobEnchantSyncPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = GoetyAwaken.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MobEnchantEventHandler {
    private static final Map<Integer, MobEnchantCapability> CAPABILITY_CACHE = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity living) {
            if (entity instanceof IMobEnchantable enchantable) {
                MobEnchantCapability existingCap = null;
                existingCap = CAPABILITY_CACHE.get(entity.getId());
                if (existingCap == null) {
                    existingCap = new MobEnchantCapability(living);
                    CAPABILITY_CACHE.put(entity.getId(), existingCap);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        Entity tracked = event.getTarget();
        if (!(tracked instanceof LivingEntity living))
            return;
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer))
            return;

        MobEnchantCapability cap = CAPABILITY_CACHE.get(living.getId());
        if (cap != null && cap.getMobEnchantLevel(MobEnchantType.HUGE) > 0) {
            int hugeLevel = cap.getMobEnchantLevel(MobEnchantType.HUGE);
            SMobEnchantSyncPacket packet = new SMobEnchantSyncPacket(living.getId(), MobEnchantType.HUGE, hugeLevel);
            GoetyAwaken.network.channel.send(PacketDistributor.PLAYER.with(() -> serverPlayer), packet);
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide && entity.level() instanceof ServerLevel serverLevel) {
            MobEnchantResurrectionManager.processResurrection(serverLevel);
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide) {
            return;
        }

        MobEnchantCapability capability = getCapability(entity);
        if (capability == null) {
            return;
        }

        float protectionPercent = capability.getProtectionPercentage();
        if (protectionPercent > 0) {
            float originalDamage = event.getAmount();
            float reducedDamage = originalDamage * (1.0f - protectionPercent);
            event.setAmount(reducedDamage);
        }

        float thornPercent = capability.getThornPercentage();
        if (thornPercent > 0 && event.getSource().getEntity() instanceof LivingEntity attacker) {
            float thornDamage = event.getAmount() * thornPercent;
            DamageSource thornSource = createThornDamageSource(entity, event.getSource());
            attacker.hurt(thornSource, thornDamage);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();

        if (target.level().isClientSide) {
            return;
        }

        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) {
            return;
        }

        MobEnchantCapability capability = getCapability(attacker);
        if (capability == null || !capability.hasMobEnchantment()) {
            return;
        }

        float damage = event.getAmount();
        int strongLevel = capability.getMobEnchantLevel(MobEnchantType.STRONG);
        int enchantCount = capability.getMobEnchantCount();

        if (strongLevel > 0 && damage > 0) {
            float enchantBonus = enchantCount * 0.1f;
            float totalBonus;
            totalBonus = strongLevel + enchantBonus;
            event.setAmount(damage + totalBonus);
        }
    }

    private static DamageSource createThornDamageSource(LivingEntity thornOwner, DamageSource originalSource) {
        Entity directEntity = originalSource.getDirectEntity();
        Entity trueEntity = originalSource.getEntity();
        if (trueEntity != null) {
            return thornOwner.damageSources().thorns(thornOwner);
        }
        return thornOwner.damageSources().thorns(thornOwner);
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        Entity entity = event.getEntity();

        if (entity.level().isClientSide) {
            return;
        }

        CAPABILITY_CACHE.remove(entity.getId());
    }

    public static MobEnchantCapability getCapabilityFromCache(LivingEntity entity) {
        return CAPABILITY_CACHE.get(entity.getId());
    }

    public static MobEnchantCapability getCapability(LivingEntity entity) {
        MobEnchantCapability cached = CAPABILITY_CACHE.get(entity.getId());
        if (cached != null) {
            return cached;
        }

        MobEnchantCapability capability = new MobEnchantCapability(entity);
        CAPABILITY_CACHE.put(entity.getId(), capability);
        return capability;
    }

    public static void syncCapabilityToCache(LivingEntity entity, MobEnchantCapability capability) {
        CAPABILITY_CACHE.put(entity.getId(), capability);
    }

    public static void applyEnchantment(LivingEntity entity, MobEnchantType type, int level) {
        MobEnchantCapability capability = getCapability(entity);
        capability.setMobEnchantLevel(type, level);
    }

    public static void clearEnchantments(LivingEntity entity) {
        MobEnchantCapability capability = getCapability(entity);
        capability.clearMobEnchantments();
    }

    public static int getEnchantmentLevel(LivingEntity entity, MobEnchantType type) {
        MobEnchantCapability capability = getCapability(entity);
        return capability.getMobEnchantLevel(type);
    }
}
