package com.k1sak1.goetyawaken.common.magic.sorcerer;

import com.Polarice3.Goety.common.magic.Spell;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class SorcererSpellEntry {

    private String focusRegistryName;
    private int minLevel;
    private int maxLevel;
    private boolean levelIncrease;
    private String upgradeStaffRegistryName;
    private int upgradeStaffLevel;
    private int weight;

    private transient Spell spell;
    private transient ItemStack focusStack;
    private transient ItemStack upgradeStaff;
    private transient int index;

    public static final int DEFAULT_WEIGHT = 10;

    public SorcererSpellEntry(String focusRegistryName, int minLevel, int maxLevel,
                              boolean levelIncrease, String upgradeStaffRegistryName,
                              int upgradeStaffLevel, int weight) {
        this.focusRegistryName = focusRegistryName;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.levelIncrease = levelIncrease;
        this.upgradeStaffRegistryName = upgradeStaffRegistryName == null ? "none" : upgradeStaffRegistryName;
        this.upgradeStaffLevel = upgradeStaffLevel;
        this.weight = weight;
    }

    public String getFocusRegistryName() {
        return focusRegistryName;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public boolean isLevelIncrease() {
        return levelIncrease;
    }

    public void setLevelIncrease(boolean levelIncrease) {
        this.levelIncrease = levelIncrease;
    }

    public String getUpgradeStaffRegistryName() {
        return upgradeStaffRegistryName;
    }

    public void setUpgradeStaffRegistryName(String name) {
        this.upgradeStaffRegistryName = name;
    }

    public int getUpgradeStaffLevel() {
        return upgradeStaffLevel;
    }

    public void setUpgradeStaffLevel(int level) {
        this.upgradeStaffLevel = level;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Spell getSpell() {
        return spell;
    }

    public void setSpell(Spell spell) {
        this.spell = spell;
    }

    public ItemStack getFocusStack() {
        return focusStack;
    }

    public void setFocusStack(ItemStack stack) {
        this.focusStack = stack;
    }

    public ItemStack getUpgradeStaff() {
        return upgradeStaff;
    }

    public void setUpgradeStaff(ItemStack stack) {
        this.upgradeStaff = stack;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ItemStack resolveUpgradeStaff(int sorcererLevel) {
        if (upgradeStaff == null || upgradeStaff.isEmpty()) return ItemStack.EMPTY;
        if ("none".equals(upgradeStaffRegistryName) || upgradeStaffRegistryName.isEmpty()) return ItemStack.EMPTY;
        if (sorcererLevel < upgradeStaffLevel) return ItemStack.EMPTY;
        return upgradeStaff.copy();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SorcererSpellEntry that)) return false;
        return focusRegistryName.equals(that.focusRegistryName);
    }

    @Override
    public int hashCode() {
        return focusRegistryName.hashCode();
    }

    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putString("focus", focusRegistryName);
        tag.putInt("minLevel", minLevel);
        tag.putInt("maxLevel", maxLevel);
        tag.putBoolean("levelIncrease", levelIncrease);
        tag.putString("upgradeStaff", upgradeStaffRegistryName);
        tag.putInt("upgradeStaffLevel", upgradeStaffLevel);
        tag.putInt("weight", weight);
        return tag;
    }

    public static SorcererSpellEntry fromNbt(CompoundTag tag) {
        return new SorcererSpellEntry(
                tag.getString("focus"),
                tag.getInt("minLevel"),
                tag.getInt("maxLevel"),
                tag.getBoolean("levelIncrease"),
                tag.getString("upgradeStaff"),
                tag.getInt("upgradeStaffLevel"),
                tag.getInt("weight")
        );
    }

    public static SorcererSpellEntry createDefault(String focusRegistryName) {
        return new SorcererSpellEntry(focusRegistryName, 1, 6, false, "none", 0, DEFAULT_WEIGHT);
    }
}
