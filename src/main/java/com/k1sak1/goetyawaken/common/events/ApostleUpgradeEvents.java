package com.k1sak1.goetyawaken.common.events;

import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.common.items.ModItems;
import com.k1sak1.goetyawaken.common.upgrades.ApostleUpgradeData;
import com.k1sak1.goetyawaken.common.upgrades.ApostleUpgradeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class ApostleUpgradeEvents {

    private static final Map<UUID, RefundInfo> pendingRefunds = new HashMap<>();
    public static final Map<UUID, Integer> lastMoneyAmounts = new HashMap<>();

    private record RefundInfo(Vec3 pos, UUID ownerUuid, ResourceKey<Level> dimension, long gameTime) {
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }
        LivingEntity target = event.getEntity();
        LivingEntity source = event.getSource().getEntity() instanceof LivingEntity living ? living : null;

        if (source != null && ApostleUpgradeManager.isMarkedForUpgrade(source)) {
            ApostleUpgradeData data = ApostleUpgradeManager.getUpgradeData(source);
            boolean changed = false;
            if (target.hasEffect(net.minecraft.world.effect.MobEffects.POISON)
                    || target.hasEffect(com.Polarice3.Goety.common.effects.GoetyEffects.ACID_VENOM.get())) {
                data.incrementBlightKills();
                changed = true;
            }
            if (target instanceof WitherBoss) {
                data.incrementWitherKills();
                changed = true;
            }
            if (target.getType() == EntityType.WARDEN) {
                data.incrementWardenKills();
                changed = true;
            }
            if (target.getType() == EntityType.BLAZE) {
                data.incrementBlazeKills();
                changed = true;
            }
            if (target.getType() == EntityType.VILLAGER) {
                data.incrementVillagerKills();
                changed = true;
            }
            if (changed) {
                ApostleUpgradeManager.saveUpgradeData(source, data);
            }
        }

        if (ApostleUpgradeManager.isMarkedForUpgrade(target) && target.level() instanceof ServerLevel serverLevel) {
            UUID targetId = target.getUUID();
            if (!pendingRefunds.containsKey(targetId)) {
                UUID owner = ApostleUpgradeManager.getUpgradeData(target).getMarkedBy();
                pendingRefunds.put(targetId,
                        new RefundInfo(target.position(), owner, serverLevel.dimension(), serverLevel.getGameTime()));
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.getServer().getTickCount() % 20 != 0) {
            return;
        }
        int processed = 0;
        Iterator<Map.Entry<UUID, RefundInfo>> iterator = pendingRefunds.entrySet().iterator();
        while (iterator.hasNext() && processed < 128) {
            Map.Entry<UUID, RefundInfo> entry = iterator.next();
            RefundInfo info = entry.getValue();
            ServerLevel level = event.getServer().getLevel(info.dimension());
            if (level == null) {
                iterator.remove();
                continue;
            }
            if (level.getGameTime() - info.gameTime() < 100) {
                continue;
            }
            iterator.remove();
            processed++;
            Entity entity = level.getEntity(entry.getKey());
            if (entity != null && !entity.isRemoved()) {
                continue;
            }
            refundTear(level, info);
        }
    }

    private static void refundTear(ServerLevel level, RefundInfo info) {
        ItemStack tear = new ItemStack(ModItems.OBSIDIAN_TEAR.get());
        ServerPlayer owner = info.ownerUuid() == null ? null
                : level.getServer().getPlayerList().getPlayer(info.ownerUuid());
        if (owner != null && owner.getInventory().add(tear)) {
            owner.displayClientMessage(
                    Component.translatable("message.goetyawaken.apostle.tear_refunded"),
                    true);
            return;
        }
        BlockPos pos = BlockPos.containing(info.pos());
        level.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
        level.addFreshEntity(new ItemEntity(level, info.pos().x, info.pos().y, info.pos().z, tear));
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }
        LivingEntity target = event.getEntity();
        LivingEntity source = event.getSource().getEntity() instanceof LivingEntity living ? living : null;
        if (source == null) {
            return;
        }
        if (ApostleUpgradeManager.isMarkedForUpgrade(source)) {
            ApostleUpgradeData data = ApostleUpgradeManager.getUpgradeData(source);
            float damage = event.getAmount();
            boolean changed = false;
            if (target instanceof WitherBoss) {
                data.addWitherDamage((int) damage);
                changed = true;
            }
            if (target.getType() == EntityType.WARDEN) {
                data.addWardenDamage((int) damage);
                changed = true;
            }
            if (event.getSource().getMsgId().contains("freeze")) {
                data.addFrozenDamage((int) damage);
                changed = true;
            }
            if (changed) {
                ApostleUpgradeManager.saveUpgradeData(source, data);
            }
        }
    }

    public static void onSorcererTrade(LivingEntity sorcerer, int emeraldAmount) {
        if (ApostleUpgradeManager.isMarkedForUpgrade(sorcerer)) {
            ApostleUpgradeData data = ApostleUpgradeManager.getUpgradeData(sorcerer);
            data.addTradingProgress(emeraldAmount);
            ApostleUpgradeManager.saveUpgradeData(sorcerer, data);
        }
    }

    public static void onServantDealDamage(LivingEntity servant, float damage) {
        if (ApostleUpgradeManager.isMarkedForUpgrade(servant)) {
            ApostleUpgradeData data = ApostleUpgradeManager.getUpgradeData(servant);
            data.addDamageDealt(damage);
            ApostleUpgradeManager.saveUpgradeData(servant, data);
        }
    }

    public static void onServantHeal(LivingEntity servant, float healAmount) {
        if (ApostleUpgradeManager.isMarkedForUpgrade(servant)) {
            ApostleUpgradeData data = ApostleUpgradeManager.getUpgradeData(servant);
            data.addHealAmount(healAmount);
            ApostleUpgradeManager.saveUpgradeData(servant, data);
        }
    }

    public static void onServantFrozenDamage(LivingEntity servant, float damage) {
        if (ApostleUpgradeManager.isMarkedForUpgrade(servant)) {
            ApostleUpgradeData data = ApostleUpgradeManager.getUpgradeData(servant);
            data.addFrozenDamage((int) damage);
            ApostleUpgradeManager.saveUpgradeData(servant, data);
        }
    }

    public static void onCroneEffectUpdate(LivingEntity crone, int positiveEffects, int negativeEffects) {
        if (ApostleUpgradeManager.isMarkedForUpgrade(crone)) {
            ApostleUpgradeData data = ApostleUpgradeManager.getUpgradeData(crone);
            data.setEffectCounts(positiveEffects, negativeEffects);
            ApostleUpgradeManager.saveUpgradeData(crone, data);
        }
    }

    public static void onVizierFollowerUpdate(LivingEntity vizier, int followerCount) {
        if (ApostleUpgradeManager.isMarkedForUpgrade(vizier)) {
            ApostleUpgradeData data = ApostleUpgradeManager.getUpgradeData(vizier);
            data.setCultistFollowers(followerCount);
            ApostleUpgradeManager.saveUpgradeData(vizier, data);
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity.level().isClientSide || livingEntity.tickCount % 20 != 0) {
            return;
        }
        if (!ApostleUpgradeManager.isMarkedForUpgrade(livingEntity)) {
            return;
        }
        ApostleUpgradeData data = ApostleUpgradeManager.getUpgradeData(livingEntity);
        boolean changed = false;

        if (livingEntity.hasEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED)
                && livingEntity.getEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED).getAmplifier() >= 1) {
            data.addSwiftTicks(20);
            changed = true;
        }

        if (livingEntity instanceof com.k1sak1.goetyawaken.common.entities.ally.illager.VizierServant) {
            if (livingEntity.tickCount % 100 == 0) {
                updateVizierFollowerCount(livingEntity);
            }
        }

        if (livingEntity instanceof com.k1sak1.goetyawaken.common.entities.ally.illager.SorcererServant sorcerer) {
            UUID sorcererId = sorcerer.getUUID();
            int currentMoney = sorcerer.getMoneyAmount();
            Integer lastMoney = lastMoneyAmounts.get(sorcererId);
            if (lastMoney == null) {
                lastMoney = currentMoney;
            }
            if (lastMoney > currentMoney) {
                data.addTradingProgress(lastMoney - currentMoney);
                changed = true;
            }
            lastMoneyAmounts.put(sorcererId, currentMoney);
        }

        if (changed) {
            ApostleUpgradeManager.saveUpgradeData(livingEntity, data);
        }
        checkAndPerformUpgrade(livingEntity);
    }

    private static void updateVizierFollowerCount(LivingEntity vizier) {
        if (vizier.level() instanceof ServerLevel serverLevel) {
            java.util.List<Entity> nearbyEntities = serverLevel.getEntities(vizier,
                    vizier.getBoundingBox().inflate(64.0),
                    entity -> entity instanceof com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant
                            && entity != vizier
                            && isOwnedBySamePlayer(
                                    (com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant) entity,
                                    vizier));

            ApostleUpgradeData data = ApostleUpgradeManager.getUpgradeData(vizier);
            data.setCultistFollowers(nearbyEntities.size());
            ApostleUpgradeManager.saveUpgradeData(vizier, data);
        }
    }

    private static boolean isOwnedBySamePlayer(
            com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant servant1,
            LivingEntity servant2) {
        if (!(servant1 instanceof com.Polarice3.Goety.common.entities.neutral.Owned)
                || !(servant2 instanceof com.Polarice3.Goety.common.entities.neutral.Owned)) {
            return false;
        }
        com.Polarice3.Goety.common.entities.neutral.Owned owned1 = (com.Polarice3.Goety.common.entities.neutral.Owned) servant1;
        com.Polarice3.Goety.common.entities.neutral.Owned owned2 = (com.Polarice3.Goety.common.entities.neutral.Owned) servant2;
        UUID owner1Id = owned1.getOwnerId();
        UUID owner2Id = owned2.getOwnerId();
        return owner1Id != null && owner2Id != null && owner1Id.equals(owner2Id);
    }

    public static boolean checkUpgradeConditions(LivingEntity entity) {
        if (!ApostleUpgradeManager.isMarkedForUpgrade(entity)) {
            return false;
        }
        ApostleUpgradeData data = ApostleUpgradeManager.getUpgradeData(entity);
        double maxHealth = entity.getAttributeValue(Attributes.MAX_HEALTH);
        if (data.getProgress("healAmount") >= maxHealth * Config.UPGRADE_HEAL_MULTIPLIER.get()) {
            return true;
        }
        if (data.getProgress("damageDealt") >= maxHealth * Config.UPGRADE_DAMAGE_MULTIPLIER.get()) {
            return true;
        }
        if (data.getBlightKills() >= Config.UPGRADE_BLIGHT_KILLS.get()) {
            return true;
        }
        if (data.getWitherDamage() >= Config.UPGRADE_WITHER_DAMAGE.get()
                && data.getWitherKills() >= Config.UPGRADE_WITHER_KILLS.get()) {
            return true;
        }
        if (data.getWardenDamage() >= Config.UPGRADE_WARDEN_DAMAGE.get()
                && data.getWardenKills() >= Config.UPGRADE_WARDEN_KILLS.get()) {
            return true;
        }
        if (data.getPositiveEffects() >= Config.UPGRADE_POSITIVE_EFFECTS.get()
                || data.getNegativeEffects() >= Config.UPGRADE_NEGATIVE_EFFECTS.get()) {
            return true;
        }
        if (data.getBlazeKills() >= Config.UPGRADE_BLAZE_KILLS.get()) {
            return true;
        }
        if (data.getTradingProgress() >= Config.UPGRADE_TRADING_EMERALDS.get()) {
            return true;
        }
        if (data.getFrozenDamage() >= maxHealth * Config.UPGRADE_FROZEN_DAMAGE_MULTIPLIER.get()) {
            return true;
        }
        if (data.getSwiftTicks() >= Config.UPGRADE_SWIFT_TICKS.get()) {
            return true;
        }
        if (data.getCultistFollowers() >= Config.UPGRADE_FOLLOWER_COUNT.get()) {
            return true;
        }
        if (data.getVillagerKills() >= Config.UPGRADE_VILLAGER_KILLS.get()) {
            return true;
        }
        return false;
    }

    public static int getUpgradeTitleNumber(LivingEntity entity) {
        if (!ApostleUpgradeManager.isMarkedForUpgrade(entity)) {
            return -1;
        }
        ApostleUpgradeData data = ApostleUpgradeManager.getUpgradeData(entity);
        double maxHealth = entity.getAttributeValue(Attributes.MAX_HEALTH);

        if (data.getProgress("healAmount") >= maxHealth * Config.UPGRADE_HEAL_MULTIPLIER.get()) {
            return 0;
        }
        if (data.getProgress("damageDealt") >= maxHealth * Config.UPGRADE_DAMAGE_MULTIPLIER.get()) {
            return 1;
        }
        if (data.getBlightKills() >= Config.UPGRADE_BLIGHT_KILLS.get()) {
            return 2;
        }
        if (data.getWitherDamage() >= Config.UPGRADE_WITHER_DAMAGE.get()
                && data.getWitherKills() >= Config.UPGRADE_WITHER_KILLS.get()) {
            return 3;
        }
        if (data.getWardenDamage() >= Config.UPGRADE_WARDEN_DAMAGE.get()
                && data.getWardenKills() >= Config.UPGRADE_WARDEN_KILLS.get()) {
            return 4;
        }
        if (data.getPositiveEffects() >= Config.UPGRADE_POSITIVE_EFFECTS.get()
                || data.getNegativeEffects() >= Config.UPGRADE_NEGATIVE_EFFECTS.get()) {
            return 5;
        }
        if (data.getBlazeKills() >= Config.UPGRADE_BLAZE_KILLS.get()) {
            return 6;
        }
        if (data.getTradingProgress() >= Config.UPGRADE_TRADING_EMERALDS.get()) {
            return 7;
        }
        if (data.getFrozenDamage() >= maxHealth * Config.UPGRADE_FROZEN_DAMAGE_MULTIPLIER.get()) {
            return 8;
        }
        if (data.getSwiftTicks() >= Config.UPGRADE_SWIFT_TICKS.get()) {
            return 9;
        }
        if (data.getCultistFollowers() >= Config.UPGRADE_FOLLOWER_COUNT.get()) {
            return 10;
        }
        if (data.getVillagerKills() >= Config.UPGRADE_VILLAGER_KILLS.get()) {
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
    }

    private static void performUpgrade(LivingEntity entity, int titleNumber) {
        if (entity.level().isClientSide) {
            return;
        }
        com.k1sak1.goetyawaken.common.upgrades.ApostleServantConverter.convertToApostle(entity, titleNumber);
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            ApostleUpgradeManager.onEntityLeaveLevel(livingEntity);
            lastMoneyAmounts.remove(livingEntity.getUUID());
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        ApostleUpgradeManager.onServerStopping();
        pendingRefunds.clear();
        lastMoneyAmounts.clear();
    }
}
