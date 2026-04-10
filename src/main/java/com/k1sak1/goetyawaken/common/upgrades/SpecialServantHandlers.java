package com.k1sak1.goetyawaken.common.upgrades;

import com.k1sak1.goetyawaken.common.events.ApostleUpgradeEvents;
import net.minecraft.world.entity.LivingEntity;

public class SpecialServantHandlers {

    public static void handleCroneEffectUpdate(LivingEntity crone, int positiveEffects, int negativeEffects) {
        ApostleUpgradeEvents.onCroneEffectUpdate(crone, positiveEffects, negativeEffects);
    }

    public static void handleSorcererTrade(LivingEntity sorcerer, int emeraldAmount) {
        ApostleUpgradeEvents.onSorcererTrade(sorcerer, emeraldAmount);
    }

    public static void handleVizierFollowerUpdate(LivingEntity vizier, int followerCount) {
        ApostleUpgradeEvents.onVizierFollowerUpdate(vizier, followerCount);
    }

    public static void handleServantDamageDealt(LivingEntity servant, float damage) {
        ApostleUpgradeEvents.onServantDealDamage(servant, damage);
    }

    public static boolean checkServantUpgradeConditions(LivingEntity servant) {
        return ApostleUpgradeEvents.checkUpgradeConditions(servant);
    }

    public static void performUpgradeCheck(LivingEntity servant) {
        ApostleUpgradeEvents.checkAndPerformUpgrade(servant);
    }

    public static void handleRaiderServantHeal(LivingEntity raider, float healAmount) {
        ApostleUpgradeEvents.onServantHeal(raider, healAmount);
    }

    public static void handleRaiderServantDamageDealt(LivingEntity raider, float damage) {
        ApostleUpgradeEvents.onServantDealDamage(raider, damage);
    }

    public static void handleRaiderServantFrozenDamage(LivingEntity raider, float damage) {
        ApostleUpgradeEvents.onServantFrozenDamage(raider, damage);
    }

    public static void handleServantHeal(LivingEntity servant, float healAmount) {
        ApostleUpgradeEvents.onServantHeal(servant, healAmount);
    }
}