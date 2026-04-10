package com.k1sak1.goetyawaken.common.events;

import com.k1sak1.goetyawaken.common.upgrades.SpecialServantHandlers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SpecialServantEvents {

    private static final java.util.Map<net.minecraft.world.entity.Entity, Integer> lastMoneyAmounts = new java.util.HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide) {
            for (Entity entity : event.player.level().getEntities(event.player,
                    event.player.getBoundingBox().inflate(64.0))) {
                if (entity instanceof com.k1sak1.goetyawaken.common.entities.ally.illager.SorcererServant sorcerer) {
                    if (com.k1sak1.goetyawaken.common.upgrades.ApostleUpgradeManager.isMarkedForUpgrade(sorcerer)) {
                        int currentMoney = sorcerer.getMoneyAmount();
                        Integer lastMoney = lastMoneyAmounts.get(sorcerer);
                        if (lastMoney == null) {
                            lastMoney = currentMoney;
                        }
                        if (lastMoney > currentMoney) {
                            int tradedAmount = lastMoney - currentMoney;
                            SpecialServantHandlers.handleSorcererTrade(sorcerer, tradedAmount);
                        }
                        lastMoneyAmounts.put(sorcerer, currentMoney);
                        ApostleUpgradeEvents.checkAndPerformUpgrade(sorcerer);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingTick(net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent event) {
        if (!event.getEntity().level().isClientSide) {
            LivingEntity entity = event.getEntity();
            if (entity instanceof com.k1sak1.goetyawaken.common.entities.ally.illager.CroneServant crone) {
                if (com.k1sak1.goetyawaken.common.upgrades.ApostleUpgradeManager.isMarkedForUpgrade(crone)) {
                    int positiveEffects = 0;
                    int negativeEffects = 0;

                    for (net.minecraft.world.effect.MobEffectInstance effect : crone.getActiveEffects()) {
                        if (effect.getEffect()
                                .getCategory() == net.minecraft.world.effect.MobEffectCategory.BENEFICIAL) {
                            positiveEffects++;
                        } else {
                            negativeEffects++;
                        }
                    }
                    SpecialServantHandlers.handleCroneEffectUpdate(crone, positiveEffects, negativeEffects);
                    ApostleUpgradeEvents.checkAndPerformUpgrade(crone);
                }
            } else if (entity instanceof com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant raider) {
                if (com.k1sak1.goetyawaken.common.upgrades.ApostleUpgradeManager.isMarkedForUpgrade(raider)) {
                    ApostleUpgradeEvents.checkAndPerformUpgrade(raider);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerInteractWithSorcerer(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity() == null || event.getTarget() == null) {
            return;
        }

        if (event.getTarget() instanceof com.k1sak1.goetyawaken.common.entities.ally.illager.SorcererServant sorcerer) {
            ItemStack itemStack = event.getItemStack();
            if (itemStack != null && itemStack.getItem() == net.minecraft.world.item.Items.EMERALD) {
                lastMoneyAmounts.put(sorcerer, sorcerer.getMoneyAmount());
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant raider) {
            if (com.k1sak1.goetyawaken.common.upgrades.ApostleUpgradeManager.isMarkedForUpgrade(raider)) {
                float healAmount = event.getAmount();
                SpecialServantHandlers.handleRaiderServantHeal(raider, healAmount);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        LivingEntity source = event.getSource().getEntity() instanceof LivingEntity
                ? (LivingEntity) event.getSource().getEntity()
                : null;
        if (target instanceof com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant raider) {
            if (com.k1sak1.goetyawaken.common.upgrades.ApostleUpgradeManager.isMarkedForUpgrade(raider)) {
                float damage = event.getAmount();
                if (event.getSource().getMsgId().contains("freeze")) {
                    SpecialServantHandlers.handleRaiderServantFrozenDamage(raider, damage);
                }
            }
        }
        if (source instanceof com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant raider) {
            if (com.k1sak1.goetyawaken.common.upgrades.ApostleUpgradeManager.isMarkedForUpgrade(raider)) {
                float damage = event.getAmount();
                SpecialServantHandlers.handleRaiderServantDamageDealt(raider, damage);
            }
        }
    }
}