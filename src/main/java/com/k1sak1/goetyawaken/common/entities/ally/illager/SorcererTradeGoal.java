package com.k1sak1.goetyawaken.common.entities.ally.illager;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;

import java.util.EnumSet;

public class SorcererTradeGoal extends Goal {
    private final SorcererServant sorcerer;
    private int tradeDuration = 0;
    private int tradeDelay = 0;
    private static final int TRADE_DURATION = 60;
    private static final int TRADE_DELAY = 60;

    public SorcererTradeGoal(SorcererServant sorcerer) {
        this.sorcerer = sorcerer;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.sorcerer.isCurrentlyTrading() && this.sorcerer.getMoneyAmount() > 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.sorcerer.isCurrentlyTrading() && this.sorcerer.getMoneyAmount() > 0 && this.tradeDuration < TRADE_DURATION;
    }

    @Override
    public void start() {
        this.tradeDuration = 0;
        this.tradeDelay = 0;
        performTradeStep();
    }

    @Override
    public void stop() {
        if (!this.sorcerer.level().isClientSide) {
            net.minecraft.world.entity.LivingEntity owner = this.sorcerer.getTrueOwner();
            if (owner != null) {
                net.minecraft.world.phys.Vec3 ownerPos = owner.position();
                for (net.minecraft.world.item.ItemStack item : this.sorcerer.getTradeItems()) {
                    BehaviorUtils.throwItem(this.sorcerer, item, ownerPos);
                }
            }
            this.sorcerer.setMoneyAmount(0);
            this.sorcerer.clearTradeItems();
            this.sorcerer.setIsCurrentlyTrading(false);
        }
    }

    @Override
    public void tick() {
        this.tradeDuration++;
        net.minecraft.world.entity.LivingEntity owner = this.sorcerer.getTrueOwner();
        if (owner != null) {
            this.sorcerer.getLookControl().setLookAt(owner.getX(), owner.getY() + owner.getEyeHeight(), owner.getZ());
        }
        if (this.tradeDuration % 5 == 0) {
            performTradeStep();
        }
        this.sorcerer.getNavigation().stop();
    }

    private void performTradeStep() {
        if (this.sorcerer.getMoneyAmount() <= 0) {
            return;
        }
        int currentLevel = this.sorcerer.getSorcererLevel();
        int currentMoney = this.sorcerer.getMoneyAmount();
        java.util.List<SorcererTrade> availableTrades = SorcererTradeManager.getAvailableTrades(currentLevel, currentMoney);
        
        if (!availableTrades.isEmpty()) {
            SorcererTrade selectedTrade = SorcererTradeManager.getRandomTrade(availableTrades, this.sorcerer.getRandom());
            
            if (selectedTrade != null && selectedTrade.isAffordable(currentMoney)) {
                this.sorcerer.setMoneyAmount(currentMoney - selectedTrade.getPrice());
                net.minecraft.world.item.ItemStack tradeItem = selectedTrade.getItemStack(this.sorcerer.level());
                this.sorcerer.addTradeItem(tradeItem);
            }
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}