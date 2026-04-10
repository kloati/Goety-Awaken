package com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.namelessquotes;

import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.AbstractNamelessOne;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class NamelessOneQuoteEvents {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null || target.level().isClientSide()) {
            return;
        }

        Entity sourceEntity = event.getSource().getEntity();

        if (target instanceof Player targetPlayer) {
            triggerPlayerDeathQuoteForNearbyNamelessOnes(target, targetPlayer);
        }

        if (sourceEntity instanceof AbstractNamelessOne namelessOne) {
            if (target.equals(namelessOne.getTarget())) {
                triggerKillQuoteWithPriority(namelessOne, target);
            }
        } else if (sourceEntity instanceof Player sourcePlayer) {

            checkAndTriggerForOwnedNamelessOnes(sourcePlayer, target, sourcePlayer);
        } else if (sourceEntity instanceof LivingEntity sourceLiving) {

            if (sourceLiving instanceof com.Polarice3.Goety.api.entities.IOwned owned) {
                Entity owner = owned.getTrueOwner();
                if (owner instanceof AbstractNamelessOne namelessOne) {

                    if (target.equals(namelessOne.getTarget())) {
                        triggerKillQuoteWithPriority(namelessOne, target);
                    }
                } else if (owner instanceof Player player) {

                    checkAndTriggerForOwnedNamelessOnes(player, target, sourceLiving);
                }
            }
        }
    }

    private static void triggerPlayerDeathQuoteForNearbyNamelessOnes(LivingEntity target, Player targetPlayer) {
        if (!(target.level() instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            return;
        }

        java.util.List<AbstractNamelessOne> nearbyNamelessOnes = serverLevel.getEntitiesOfClass(
                AbstractNamelessOne.class,
                target.getBoundingBox().inflate(64.0D));

        for (AbstractNamelessOne namelessOne : nearbyNamelessOnes) {
            namelessOne.triggerPlayerDeathQuote(targetPlayer);
        }
    }

    private static void triggerKillQuoteWithPriority(AbstractNamelessOne namelessOne, LivingEntity target) {

        if (tryTriggerKillSpecialEnemyQuote(namelessOne, target)) {
            return;
        }

        if (isBoss(target)) {
            namelessOne.triggerKillBossQuote(target);
            return;
        }

        if (target instanceof Player targetPlayer) {
            namelessOne.triggerKillPlayerQuote(targetPlayer);
            return;
        }

        if (namelessOne.level().random.nextDouble() < 0.05) {
            namelessOne.triggerKillEnemyQuote();
        }
    }

    private static boolean tryTriggerKillSpecialEnemyQuote(AbstractNamelessOne namelessOne, LivingEntity target) {
        Entity killer = target.getLastHurtByMob();
        if (killer == null) {
            return false;
        }
        boolean areAllies = com.Polarice3.Goety.utils.MobUtil.areAllies(namelessOne, killer);
        if (areAllies || killer == namelessOne) {
            boolean result = namelessOne.triggerKillSpecialEnemyQuote(target);
            return result;
        }
        return false;
    }

    private static void checkAndTriggerForOwnedNamelessOnes(Player owner, LivingEntity target, Entity killer) {
        if (!(owner.level() instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            return;
        }
        java.util.List<AbstractNamelessOne> ownedNamelessOnes = serverLevel.getEntitiesOfClass(
                AbstractNamelessOne.class,
                owner.getBoundingBox().inflate(64.0D),
                entity -> owner.equals(entity.getTrueOwner()));

        for (AbstractNamelessOne namelessOne : ownedNamelessOnes) {

            if (target.equals(namelessOne.getTarget())) {
                triggerKillQuoteWithPriority(namelessOne, target);
            }
        }
    }

    private static boolean isBoss(LivingEntity entity) {
        net.minecraft.world.entity.EntityType<?> type = entity.getType();
        return type == net.minecraft.world.entity.EntityType.ENDER_DRAGON ||
                type == net.minecraft.world.entity.EntityType.WITHER ||
                type == net.minecraft.world.entity.EntityType.WARDEN ||
                type == net.minecraft.world.entity.EntityType.ELDER_GUARDIAN ||
                type.is(net.minecraftforge.common.Tags.EntityTypes.BOSSES);
    }

    @SubscribeEvent
    public static void onLivingSetAttackTarget(LivingChangeTargetEvent event) {
        if (event.getEntity() instanceof AbstractNamelessOne namelessOne) {
            if (event.getNewTarget() != null && event.getNewTarget() instanceof LivingEntity) {
                namelessOne.triggerDiscoverEnemyQuote();
            }
        }
    }
}
