package com.k1sak1.goetyawaken.common.events;

import com.k1sak1.goetyawaken.common.upgrades.ApostleUpgradeData;
import com.k1sak1.goetyawaken.common.upgrades.ApostleUpgradeManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ApostleUpgradeEvents {

    private static final java.util.Map<java.util.UUID, Integer> swiftDayTracker = new java.util.HashMap<>();
    private static final java.util.Map<java.util.UUID, Long> lastWorldTimeTracker = new java.util.HashMap<>();

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity target = event.getEntity();
        LivingEntity source = event.getSource().getEntity() instanceof LivingEntity
                ? (LivingEntity) event.getSource().getEntity()
                : null;

        if (source == null)
            return;

        if (ApostleUpgradeManager.isMarkedForUpgrade(source)) {
            ApostleUpgradeData data = ApostleUpgradeManager.getUpgradeData(source);

            if (target.hasEffect(net.minecraft.world.effect.MobEffects.POISON) ||
                    target.hasEffect(com.Polarice3.Goety.common.effects.GoetyEffects.ACID_VENOM.get())) {
                data.incrementBlightKills();
                ApostleUpgradeManager.saveUpgradeData(source);
            }

            if (target instanceof WitherBoss) {
                data.incrementWitherKills();
                ApostleUpgradeManager.saveUpgradeData(source);
            }

            if (target.getType() == EntityType.WARDEN) {
                data.incrementWardenKills();
                ApostleUpgradeManager.saveUpgradeData(source);
            }

            if (target.getType() == EntityType.BLAZE) {
                data.incrementBlazeKills();
                ApostleUpgradeManager.saveUpgradeData(source);
            }

            if (target.getType() == EntityType.VILLAGER) {
                data.incrementVillagerKills();
                ApostleUpgradeManager.saveUpgradeData(source);
            }
        }

        if (ApostleUpgradeManager.isMarkedForUpgrade(target)) {
            ApostleUpgradeManager.clearUpgradeData(target);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        LivingEntity source = event.getSource().getEntity() instanceof LivingEntity
                ? (LivingEntity) event.getSource().getEntity()
                : null;

        if (source == null)
            return;

        if (ApostleUpgradeManager.isMarkedForUpgrade(source)) {
            ApostleUpgradeData data = ApostleUpgradeManager.getUpgradeData(source);
            float damage = event.getAmount();

            if (target instanceof WitherBoss) {
                data.addWitherDamage((int) (damage));
                ApostleUpgradeManager.saveUpgradeData(source);
            }

            if (target.getType() == EntityType.WARDEN) {
                data.addWardenDamage((int) (damage));
                ApostleUpgradeManager.saveUpgradeData(source);
            }
            if (event.getSource().getMsgId().contains("freeze")) {
                data.addFrozenDamage((int) damage);
                ApostleUpgradeManager.saveUpgradeData(source);
            }
        }
        if (ApostleUpgradeManager.isMarkedForUpgrade(target)) {
            ApostleUpgradeData data = ApostleUpgradeManager.getUpgradeData(target);
            float maxHealth = (float) target.getAttributeValue(Attributes.MAX_HEALTH);
            if (event.getAmount() < 0) {
                data.addProgress("healAmount", -event.getAmount());
                ApostleUpgradeManager.saveUpgradeData(target);
            }
        }
    }

    public static void onSorcererTrade(LivingEntity sorcerer, int emeraldAmount) {
        if (ApostleUpgradeManager.isMarkedForUpgrade(sorcerer)) {
            ApostleUpgradeData data = ApostleUpgradeManager.getUpgradeData(sorcerer);
            data.addTradingProgress(emeraldAmount);
            ApostleUpgradeManager.saveUpgradeData(sorcerer);
        }
    }

    public static void onServantDealDamage(LivingEntity servant, float damage) {
        if (ApostleUpgradeManager.isMarkedForUpgrade(servant)) {
            ApostleUpgradeData data = ApostleUpgradeManager.getUpgradeData(servant);
            float maxHealth = (float) servant.getAttributeValue(Attributes.MAX_HEALTH);
            data.addProgress("damageDealt", damage);
            ApostleUpgradeManager.saveUpgradeData(servant);
        }
    }

    public static void onServantHeal(LivingEntity servant, float healAmount) {
        if (ApostleUpgradeManager.isMarkedForUpgrade(servant)) {
            ApostleUpgradeData data = ApostleUpgradeManager.getUpgradeData(servant);
            float maxHealth = (float) servant.getAttributeValue(Attributes.MAX_HEALTH);
            data.addProgress("healAmount", healAmount);
            ApostleUpgradeManager.saveUpgradeData(servant);
        }
    }

    public static void onServantFrozenDamage(LivingEntity servant, float damage) {
        if (ApostleUpgradeManager.isMarkedForUpgrade(servant)) {
            ApostleUpgradeData data = ApostleUpgradeManager.getUpgradeData(servant);
            float maxHealth = (float) servant.getAttributeValue(Attributes.MAX_HEALTH);
            data.addFrozenDamage((int) damage);
            ApostleUpgradeManager.saveUpgradeData(servant);
        }
    }

    public static void onCroneEffectUpdate(LivingEntity crone, int positiveEffects, int negativeEffects) {
        if (ApostleUpgradeManager.isMarkedForUpgrade(crone)) {
            ApostleUpgradeData data = ApostleUpgradeManager.getUpgradeData(crone);
            data.setEffectCounts(positiveEffects, negativeEffects);
            ApostleUpgradeManager.saveUpgradeData(crone);
        }
    }

    public static void onVizierFollowerUpdate(LivingEntity vizier, int followerCount) {
        if (ApostleUpgradeManager.isMarkedForUpgrade(vizier)) {
            ApostleUpgradeData data = ApostleUpgradeManager.getUpgradeData(vizier);
            data.setCultistFollowers(followerCount);
            ApostleUpgradeManager.saveUpgradeData(vizier);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            boolean hasSwift = player.hasEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED) &&
                    player.getEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED).getAmplifier() >= 1;
            for (Entity entity : player.level().getEntities(player, player.getBoundingBox().inflate(64))) {
                if (entity instanceof LivingEntity livingEntity) {
                    if (ApostleUpgradeManager.isMarkedForUpgrade(livingEntity) &&
                            livingEntity.distanceTo(player) < 64) {
                        boolean servantHasSwift = livingEntity
                                .hasEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED) &&
                                livingEntity.getEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED)
                                        .getAmplifier() >= 1;

                        if (servantHasSwift && event.player.tickCount % 20 == 0) {
                            ApostleUpgradeData data = ApostleUpgradeManager.getUpgradeData(livingEntity);
                            data.addSwiftTicks(20);
                            ApostleUpgradeManager.saveUpgradeData(livingEntity);
                        }
                        checkAndPerformUpgrade(livingEntity);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!event.getEntity().level().isClientSide) {
            LivingEntity livingEntity = event.getEntity();
            if (ApostleUpgradeManager.isMarkedForUpgrade(livingEntity)) {
                if (livingEntity instanceof com.k1sak1.goetyawaken.common.entities.ally.illager.VizierServant) {
                    updateVizierFollowerCount(livingEntity);
                }
                checkAndPerformUpgrade(livingEntity);
            }
        }
    }

    private static void updateVizierFollowerCount(LivingEntity vizier) {
        if (vizier.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            java.util.List<net.minecraft.world.entity.Entity> nearbyEntities = serverLevel.getEntities(vizier,
                    vizier.getBoundingBox().inflate(64.0),
                    entity -> entity instanceof com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant &&
                            entity != vizier &&
                            isOwnedBySamePlayer(
                                    (com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant) entity,
                                    vizier));

            int followerCount = nearbyEntities.size();
            ApostleUpgradeData data = ApostleUpgradeManager.getUpgradeData(vizier);
            data.setCultistFollowers(followerCount);
            ApostleUpgradeManager.saveUpgradeData(vizier);
        }
    }

    private static boolean isOwnedBySamePlayer(
            com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant servant1,
            LivingEntity servant2) {
        if (!(servant1 instanceof com.Polarice3.Goety.common.entities.neutral.Owned) ||
                !(servant2 instanceof com.Polarice3.Goety.common.entities.neutral.Owned)) {
            return false;
        }

        com.Polarice3.Goety.common.entities.neutral.Owned owned1 = (com.Polarice3.Goety.common.entities.neutral.Owned) servant1;
        com.Polarice3.Goety.common.entities.neutral.Owned owned2 = (com.Polarice3.Goety.common.entities.neutral.Owned) servant2;

        java.util.UUID owner1Id = owned1.getOwnerId();
        java.util.UUID owner2Id = owned2.getOwnerId();

        return owner1Id != null && owner2Id != null && owner1Id.equals(owner2Id);
    }

    public static boolean checkUpgradeConditions(LivingEntity entity) {
        if (!ApostleUpgradeManager.isMarkedForUpgrade(entity)) {
            return false;
        }

        ApostleUpgradeData data = ApostleUpgradeManager.getUpgradeData(entity);
        float maxHealth = (float) entity.getAttributeValue(Attributes.MAX_HEALTH);
        if (data.getProgress("healAmount") >= maxHealth * 1000) {
            return true;
        }
        if (data.getProgress("damageDealt") >= maxHealth * 1000) {
            return true;
        }
        if (data.getBlightKills() >= 100) {
            return true;
        }
        if (data.getWitherDamage() > 300 * 3 && data.getWitherKills() >= 3) {
            return true;
        }

        if (data.getWardenDamage() > 500 * 2 && data.getWardenKills() >= 2) {
            return true;
        }

        if (data.getPositiveEffects() >= 30 || data.getNegativeEffects() >= 20) {
            return true;
        }

        if (data.getBlazeKills() >= 100) {
            return true;
        }

        if (data.getTradingProgress() >= 4096) {
            return true;
        }

        if (data.getFrozenDamage() >= maxHealth * 10) {
            return true;
        }

        if (data.getSwiftTicks() >= 240000) {
            return true;
        }

        if (data.getCultistFollowers() >= 128) {
            return true;
        }

        if (data.getVillagerKills() >= 666) {
            return true;
        }

        return false;
    }

    public static int getUpgradeTitleNumber(LivingEntity entity) {
        if (!ApostleUpgradeManager.isMarkedForUpgrade(entity)) {
            return -1;
        }

        ApostleUpgradeData data = ApostleUpgradeManager.getUpgradeData(entity);
        float maxHealth = (float) entity.getAttributeValue(Attributes.MAX_HEALTH);

        if (data.getProgress("healAmount") >= maxHealth * 1000) {
            return 0;
        }

        if (data.getProgress("damageDealt") >= maxHealth * 1000) {
            return 1;
        }

        if (data.getBlightKills() >= 100) {
            return 2;
        }

        if (data.getWitherDamage() > 300 * 3 && data.getWitherKills() >= 3) {
            return 3;
        }

        if (data.getWardenDamage() > 500 * 2 && data.getWardenKills() >= 2) {
            return 4;
        }

        if (data.getPositiveEffects() >= 30 || data.getNegativeEffects() >= 20) {
            return 5;
        }

        if (data.getBlazeKills() >= 100) {
            return 6;
        }

        if (data.getTradingProgress() >= 4096) {
            return 7;
        }

        if (data.getFrozenDamage() >= maxHealth * 10) {
            return 8;
        }

        if (data.getSwiftTicks() >= 240000) {
            return 9;
        }

        if (data.getCultistFollowers() >= 256) {
            return 10;
        }

        if (data.getVillagerKills() >= 666) {
            return 11;
        }

        return -1;
    }

    public static void checkAndPerformUpgrade(LivingEntity entity) {
        if (checkUpgradeConditions(entity)) {
            int titleNumber = getUpgradeTitleNumber(entity);
            performUpgrade(entity, titleNumber);
            ApostleUpgradeManager.clearUpgradeData(entity);
        }
        if (ApostleUpgradeManager.isMarkedForUpgrade(entity)) {
            ApostleUpgradeManager.saveUpgradeData(entity);
        }
    }

    private static void performUpgrade(LivingEntity entity, int titleNumber) {
        if (entity.level().isClientSide)
            return;
        com.k1sak1.goetyawaken.common.upgrades.ApostleServantConverter.convertToApostle(entity, titleNumber);
    }
}