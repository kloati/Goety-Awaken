package com.k1sak1.goetyawaken.common.entities.ai;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.api.magic.IChargingSpell;
import com.Polarice3.Goety.api.magic.IBreathingSpell;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.common.magic.sorcerer.SorcererSpellCaster;
import com.k1sak1.goetyawaken.common.magic.sorcerer.SorcererSpellData;
import com.k1sak1.goetyawaken.common.magic.sorcerer.SorcererSpellEntry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class SorcererSpellAttackGoal extends Goal {

    private final Mob mob;
    private final SorcererSpellCaster caster;
    private static final int MAX_CAST_DURATION = 150;

    private SorcererSpellData data() {
        return caster.getSpellData();
    }

    public SorcererSpellEntry spellEntry;
    private ItemStack wandStack = ItemStack.EMPTY;
    private int elapsedTicks;
    private int shotTimer;
    private int shotsFired;
    private boolean spellResultCalled;
    private int lastBroadcastEvent = 0;

    public SorcererSpellAttackGoal(Mob mob, SorcererSpellCaster caster) {
        this.mob = mob;
        this.caster = caster;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive())
            return false;
        if (caster.isCastingSpell2() || !mob.hasLineOfSight(target) || data().coolDown > 0)
            return false;

        List<SorcererSpellEntry> entries = data().spellEntries;
        if (entries == null || entries.isEmpty())
            return false;

        List<SorcererSpellEntry> spells = new ArrayList<>();
        List<Integer> weights = new ArrayList<>();
        int totalWeight = 0;
        int level = caster.getSorcererLevel();

        for (int i = 0; i < entries.size(); i++) {
            SorcererSpellEntry entry = entries.get(i);
            if (level < entry.getMinLevel() || level > entry.getMaxLevel())
                continue;
            Spell spell = entry.getSpell();
            if (spell == null)
                continue;
            if (!spell.conditionsMet(mob.level(), mob))
                continue;
            if (data().spellCoolDown[i] > 0)
                continue;
            if (spell instanceof SummonSpell && mob.hasEffect(GoetyEffects.SUMMON_DOWN.get()))
                continue;
            spells.add(entry);
            weights.add(data().spellWeights[i]);
            totalWeight += data().spellWeights[i];
        }

        if (!spells.isEmpty() && totalWeight > 0) {
            int randomValue = mob.getRandom().nextInt(totalWeight);
            int currentWeight = 0;
            for (int i = 0; i < spells.size(); i++) {
                currentWeight += weights.get(i);
                if (randomValue < currentWeight) {
                    this.spellEntry = spells.get(i);
                    break;
                }
            }
        } else {
            this.spellEntry = null;
        }

        if (this.spellEntry != null) {
            this.wandStack = buildWandStack();
        }
        return this.spellEntry != null;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive())
            return false;
        SorcererSpellData sd = data();
        if (sd == null || sd.spellUseTimeRemaining <= 0)
            return false;
        if (this.spellEntry != null && !SorcererSpellCaster.isSpellStillValid(this.spellEntry))
            return false;
        Spell spell = this.spellEntry.getSpell();
        if (spell != null && !spell.conditionsMet(mob.level(), mob))
            return false;
        return true;
    }

    private ItemStack buildWandStack() {
        ItemStack staff = spellEntry.resolveUpgradeStaff(caster.getSorcererLevel());
        if (staff != null && !staff.isEmpty())
            return staff.copy();
        ItemStack wand = new ItemStack(ModItems.DARK_WAND.get());
        ItemStack focus = spellEntry.getFocusStack();
        if (focus != null && !focus.isEmpty()) {
            IItemHandler handler = IWand.getItemHandler(wand);
            if (handler != null && handler.getSlots() > 0) {
                handler.insertItem(0, focus.copy(), false);
            }
        }
        return wand;
    }

    private int calcDuration(Spell spell) {
        int base;
        if (spell instanceof IChargingSpell chargingSpell) {
            if (chargingSpell.everCharge()) {
                base = MAX_CAST_DURATION;
            } else {
                int shots = chargingSpell.shotsNumber(mob, this.wandStack);
                int cd = Math.max(1, chargingSpell.Cooldown(mob, this.wandStack, 0));
                int castUp = chargingSpell.castUp(mob, this.wandStack);
                if (shots <= 0) {
                    base = MAX_CAST_DURATION;
                } else {
                    base = Math.min(shots * cd + castUp, MAX_CAST_DURATION);
                }
            }
        } else {
            base = Math.max(1, spell.castDuration(mob, this.wandStack));
        }
        if (caster.shouldReduceCastTime()) {
            base = Math.max(1, base / 2);
        }
        return base;
    }

    @Override
    public void start() {
        Spell spell = this.spellEntry.getSpell();
        int duration = calcDuration(spell);
        Integer idx = data().focusNameToIndex.get(this.spellEntry.getFocusRegistryName());
        if (idx != null)
            data().spellCoolDown[idx] = this.getCastingInterval();
        SoundEvent soundevent = this.getSpellPrepareSound();
        if (soundevent != null)
            mob.playSound(soundevent, 1.0F, 1.0F);
        caster.setIsCastingSpell(1);
        data().currentSpell = this.spellEntry;
        caster.setCurrentSpellName(this.spellEntry.getFocusRegistryName());
        SorcererSpellData sd = data();
        sd.virtualWand = this.wandStack.copy();
        sd.maxCastingTime = duration;
        sd.spellUseTimeRemaining = duration;
        sd.castingTime = duration;
        sd.castTimeCounter = 0;
        this.elapsedTicks = 0;
        this.shotTimer = 0;
        this.shotsFired = 0;
        this.spellResultCalled = false;
        this.lastBroadcastEvent = 0;
    }

    @Override
    public void stop() {
        super.stop();
        cleanup();
    }

    @Override
    public void tick() {
        SorcererSpellData sd = data();
        if (sd == null)
            return;
        if (mob.getTarget() != null) {
            MobUtil.instaLook(mob, mob.getTarget());
        }
        Spell spell = this.spellEntry.getSpell();
        if (spell == null)
            return;
        SpellStat spellStat = WandUtil.getStats(mob, spell);
        if (this.spellEntry.isLevelIncrease()) {
            spellStat.setPotency(caster.getSorcererLevel() - this.spellEntry.getMinLevel());
        }

        if (sd.castTimeCounter == 0) {
            if (mob.level() instanceof ServerLevel serverLevel) {
                spell.startSpell(serverLevel, mob, this.wandStack, spellStat);
            }
        }
        sd.castTimeCounter++;
        this.elapsedTicks++;

        if (mob.level() instanceof ServerLevel serverLevel) {
            spell.useSpell(serverLevel, mob, this.wandStack, sd.castTimeCounter, spellStat);
        }

        boolean isCharging = spell instanceof IChargingSpell;
        boolean isInstant = !isCharging && spell.castDuration(mob, this.wandStack) <= 0;

        if (isCharging) {
            IChargingSpell chargingSpell = (IChargingSpell) spell;
            int castUp = chargingSpell.castUp(mob, this.wandStack);
            if (sd.castTimeCounter >= castUp || castUp <= 0) {
                this.shotTimer++;
                int cooldown = chargingSpell.Cooldown(mob, this.wandStack, this.shotsFired);
                if (cooldown < 0)
                    cooldown = 0;
                if (cooldown <= 0 || this.shotTimer >= cooldown) {
                    this.shotTimer = 0;
                    if (mob.level() instanceof ServerLevel serverLevel) {
                        spell.SpellResult(serverLevel, mob, this.wandStack, spellStat);
                    }
                    this.shotsFired++;
                    if (spell instanceof IBreathingSpell breathingSpell) {
                        breathingSpell.showWandBreath(mob, this.wandStack, spellStat);
                    }
                    Integer idx = data().focusNameToIndex.get(this.spellEntry.getFocusRegistryName());
                    if (idx != null)
                        data().spellWeights[idx] = this.spellEntry.getWeight();
                }
            }
            if (this.lastBroadcastEvent != 4) {
                mob.level().broadcastEntityEvent(mob, (byte) 4);
                this.lastBroadcastEvent = 4;
            }
        } else if (!isInstant && !this.spellResultCalled) {
            if (this.elapsedTicks >= sd.maxCastingTime - 1) {
                this.spellResultCalled = true;
                if (mob.level() instanceof ServerLevel serverLevel) {
                    spell.SpellResult(serverLevel, mob, this.wandStack, spellStat);
                }
                Integer idx = data().focusNameToIndex.get(this.spellEntry.getFocusRegistryName());
                if (idx != null)
                    data().spellWeights[idx] = this.spellEntry.getWeight();
            }
            if (this.lastBroadcastEvent != 5) {
                mob.level().broadcastEntityEvent(mob, (byte) 5);
                this.lastBroadcastEvent = 5;
            }
        } else if (isInstant && !this.spellResultCalled) {
            this.spellResultCalled = true;
            if (mob.level() instanceof ServerLevel serverLevel) {
                spell.SpellResult(serverLevel, mob, this.wandStack, spellStat);
            }
            Integer idx = data().focusNameToIndex.get(this.spellEntry.getFocusRegistryName());
            if (idx != null)
                data().spellWeights[idx] = this.spellEntry.getWeight();
            if (this.lastBroadcastEvent != 5) {
                mob.level().broadcastEntityEvent(mob, (byte) 5);
                this.lastBroadcastEvent = 5;
            }
        } else if (isInstant) {
            if (this.lastBroadcastEvent != 5) {
                mob.level().broadcastEntityEvent(mob, (byte) 5);
                this.lastBroadcastEvent = 5;
            }
        }

        spell.useParticle(mob.level(), mob, this.wandStack);
        if (sd.spellUseTimeRemaining > 0) {
            sd.spellUseTimeRemaining--;
        }
        sd.castingTime = sd.spellUseTimeRemaining;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private void cleanup() {
        Spell spell = this.spellEntry != null ? this.spellEntry.getSpell() : null;
        SorcererSpellData sd = data();
        if (spell != null && mob.level() instanceof ServerLevel serverLevel && sd != null) {
            spell.stopSpell(serverLevel, mob, this.wandStack,
                    this.spellEntry.getFocusStack(), sd.castTimeCounter,
                    WandUtil.getStats(mob, spell));
        }
        if (sd != null) {
            sd.castingTime = 0;
            sd.spellUseTimeRemaining = 0;
            sd.virtualWand = ItemStack.EMPTY;
            sd.coolDown = caster.shouldReduceCastTime() ? 10 : 20;
        }
        caster.setIsCastingSpell(0);
        caster.setCurrentSpellName("");
        mob.level().broadcastEntityEvent(mob, (byte) 5);
        mob.level().broadcastEntityEvent(mob, (byte) 7);
    }

    protected int getCastingInterval() {
        Spell spell = spellEntry.getSpell();
        int interval;
        if (spell instanceof IChargingSpell chargingSpell) {
            interval = chargingSpell.defaultSpellCooldown() * 2;
        } else {
            interval = spell.defaultSpellCooldown();
        }
        if (caster.shouldReduceCastTime()) {
            interval = Math.max(1, interval / 2);
        }
        return interval;
    }

    @Nullable
    protected SoundEvent getSpellPrepareSound() {
        return spellEntry.getSpell().CastingSound(mob);
    }
}
