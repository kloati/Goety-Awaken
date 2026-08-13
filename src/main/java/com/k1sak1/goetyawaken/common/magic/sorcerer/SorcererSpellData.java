package com.k1sak1.goetyawaken.common.magic.sorcerer;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SorcererSpellData {

    private static final int MAX_WEIGHT = 1000;
    private static final int WEIGHT_RECOVERY = 20;

    public int castingTime;
    public List<SorcererSpellEntry> spellEntries = List.of();
    public Map<String, Integer> focusNameToIndex = Map.of();
    public int[] spellCoolDown = new int[0];
    public int[] spellWeights = new int[0];
    public SorcererSpellEntry currentSpell;
    public boolean needsSpellReload = true;
    public int coolDown = 0;
    public int castTimeCounter;
    public boolean hasSpawned;
    public ItemStack virtualWand = ItemStack.EMPTY;
    public int maxCastingTime;
    public int spellUseTimeRemaining;

    public void reloadSpellData() {
        List<SorcererSpellEntry> entries = SorcererSpellConfig.getSpellEntries();
        this.spellEntries = entries;
        Map<String, Integer> indexMap = new HashMap<>();
        int[] cooldowns = new int[entries.size()];
        int[] weights = new int[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            indexMap.put(entries.get(i).getFocusRegistryName(), i);
            weights[i] = entries.get(i).getWeight();
        }
        this.focusNameToIndex = indexMap;
        this.spellCoolDown = cooldowns;
        this.spellWeights = weights;
    }

    public void decrementCastingTime() {
        if (castingTime > 0) {
            --castingTime;
        }
    }

    public void serverTick(Mob owner) {
        if (needsSpellReload || this.spellEntries != SorcererSpellConfig.getSpellEntries()) {
            reloadSpellData();
            needsSpellReload = false;
        }
        for (int i = 0; i < spellCoolDown.length; i++) {
            if (spellCoolDown[i] > 0) {
                --spellCoolDown[i];
            }
        }
        if (coolDown > 0) {
            --coolDown;
        }
        if (owner.tickCount % 20 == 0) {
            for (int i = 0; i < spellWeights.length; i++) {
                spellWeights[i] = Math.min(spellWeights[i] + WEIGHT_RECOVERY, MAX_WEIGHT);
            }
        }
    }
}
