package com.k1sak1.goetyawaken.common.upgrades;

import net.minecraft.nbt.CompoundTag;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ApostleUpgradeData {
    private boolean markedForUpgrade = false;
    private UUID markedBy = null;
    private Map<String, Double> progressData = new HashMap<>();
    private int titleNumber = -1;
    private boolean isUpgraded = false;

    private int blightKills = 0; // 条件3：击杀中毒生物
    private int witherKills = 0; // 条件4：击杀凋灵数量
    private int witherDamage = 0; // 条件4：对凋灵造成伤害
    private int wardenKills = 0; // 条件5：击杀监守者数量
    private int wardenDamage = 0; // 条件5：对监守者造成伤害
    private int blazeKills = 0; // 条件7：击杀烈焰人
    private int villagerKills = 0; // 条件12：击杀村民
    private int tradingProgress = 0; // 条件8：交易进度
    private int frozenDamage = 0; // 条件9：冰冻伤害
    private int swiftDays = 0; // 条件10：迅捷状态下渡过的游戏日
    private int swiftTicks = 0; // 条件10：迅捷状态下渡过的游戏刻
    private int cultistFollowers = 0; // 条件11：队员数量
    private int positiveEffects = 0; // 巫婆正面效果数量
    private int negativeEffects = 0; // 巫婆负面效果数量

    public ApostleUpgradeData() {
    }

    public void setMarkedForUpgrade(boolean marked) {
        this.markedForUpgrade = marked;
    }

    public boolean isMarkedForUpgrade() {
        return this.markedForUpgrade;
    }

    public void setMarkedBy(UUID playerUUID) {
        this.markedBy = playerUUID;
    }

    public UUID getMarkedBy() {
        return this.markedBy;
    }

    public void setTitleNumber(int titleNumber) {
        this.titleNumber = titleNumber;
    }

    public int getTitleNumber() {
        return this.titleNumber;
    }

    public void setUpgraded(boolean upgraded) {
        this.isUpgraded = upgraded;
    }

    public boolean isUpgraded() {
        return this.isUpgraded;
    }

    public double getProgress(String key) {
        return this.progressData.getOrDefault(key, 0.0);
    }

    public void setProgress(String key, double value) {
        this.progressData.put(key, value);
    }

    public void addProgress(String key, double value) {
        double current = getProgress(key);
        setProgress(key, current + value);
    }

    public void incrementBlightKills() {
        this.blightKills++;
    }

    public int getBlightKills() {
        return this.blightKills;
    }

    public void incrementWitherKills() {
        this.witherKills++;
    }

    public void addWitherDamage(int damage) {
        this.witherDamage += damage;
    }

    public int getWitherKills() {
        return this.witherKills;
    }

    public int getWitherDamage() {
        return this.witherDamage;
    }

    public void incrementWardenKills() {
        this.wardenKills++;
    }

    public void addWardenDamage(int damage) {
        this.wardenDamage += damage;
    }

    public int getWardenKills() {
        return this.wardenKills;
    }

    public int getWardenDamage() {
        return this.wardenDamage;
    }

    public void incrementBlazeKills() {
        this.blazeKills++;
    }

    public int getBlazeKills() {
        return this.blazeKills;
    }

    public void addTradingProgress(int emeralds) {
        this.tradingProgress += emeralds;
    }

    public int getTradingProgress() {
        return this.tradingProgress;
    }

    public void addFrozenDamage(int damage) {
        this.frozenDamage += damage;
    }

    public int getFrozenDamage() {
        return this.frozenDamage;
    }

    public void incrementSwiftDays() {
        this.swiftDays++;
    }

    public int getSwiftDays() {
        return this.swiftDays;
    }

    public int getSwiftTicks() {
        return this.swiftTicks;
    }

    public void addSwiftTicks(int ticks) {
        this.swiftTicks += ticks;
    }

    public void setCultistFollowers(int count) {
        this.cultistFollowers = count;
    }

    public int getCultistFollowers() {
        return this.cultistFollowers;
    }

    public void setEffectCounts(int positive, int negative) {
        this.positiveEffects = positive;
        this.negativeEffects = negative;
    }

    public int getPositiveEffects() {
        return this.positiveEffects;
    }

    public int getNegativeEffects() {
        return this.negativeEffects;
    }

    public void incrementVillagerKills() {
        this.villagerKills++;
    }

    public int getVillagerKills() {
        return this.villagerKills;
    }

    public CompoundTag saveNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("MarkedForUpgrade", this.markedForUpgrade);
        if (this.markedBy != null) {
            tag.putUUID("MarkedBy", this.markedBy);
        }
        tag.putInt("TitleNumber", this.titleNumber);
        tag.putBoolean("IsUpgraded", this.isUpgraded);
        tag.putInt("BlightKills", this.blightKills);
        tag.putInt("WitherKills", this.witherKills);
        tag.putInt("WitherDamage", this.witherDamage);
        tag.putInt("WardenKills", this.wardenKills);
        tag.putInt("WardenDamage", this.wardenDamage);
        tag.putInt("BlazeKills", this.blazeKills);
        tag.putInt("VillagerKills", this.villagerKills);
        tag.putInt("TradingProgress", this.tradingProgress);
        tag.putInt("FrozenDamage", this.frozenDamage);
        tag.putInt("SwiftDays", this.swiftDays);
        tag.putInt("SwiftTicks", this.swiftTicks);
        tag.putInt("CultistFollowers", this.cultistFollowers);
        tag.putInt("PositiveEffects", this.positiveEffects);
        tag.putInt("NegativeEffects", this.negativeEffects);
        CompoundTag progressTag = new CompoundTag();
        for (Map.Entry<String, Double> entry : this.progressData.entrySet()) {
            progressTag.putDouble(entry.getKey(), entry.getValue());
        }
        tag.put("ProgressData", progressTag);
        return tag;
    }

    public static ApostleUpgradeData loadNBT(CompoundTag tag) {
        ApostleUpgradeData data = new ApostleUpgradeData();
        data.markedForUpgrade = tag.getBoolean("MarkedForUpgrade");
        if (tag.hasUUID("MarkedBy")) {
            data.markedBy = tag.getUUID("MarkedBy");
        }
        data.titleNumber = tag.getInt("TitleNumber");
        data.isUpgraded = tag.getBoolean("IsUpgraded");
        data.blightKills = tag.getInt("BlightKills");
        data.witherKills = tag.getInt("WitherKills");
        data.witherDamage = tag.getInt("WitherDamage");
        data.wardenKills = tag.getInt("WardenKills");
        data.wardenDamage = tag.getInt("WardenDamage");
        data.blazeKills = tag.getInt("BlazeKills");
        data.villagerKills = tag.getInt("VillagerKills");
        data.tradingProgress = tag.getInt("TradingProgress");
        data.frozenDamage = tag.getInt("FrozenDamage");
        data.swiftDays = tag.getInt("SwiftDays");
        data.swiftTicks = tag.getInt("SwiftTicks");
        data.cultistFollowers = tag.getInt("CultistFollowers");
        data.positiveEffects = tag.getInt("PositiveEffects");
        data.negativeEffects = tag.getInt("NegativeEffects");
        if (tag.contains("ProgressData")) {
            CompoundTag progressTag = tag.getCompound("ProgressData");
            for (String key : progressTag.getAllKeys()) {
                data.progressData.put(key, progressTag.getDouble(key));
            }
        }

        return data;
    }
}