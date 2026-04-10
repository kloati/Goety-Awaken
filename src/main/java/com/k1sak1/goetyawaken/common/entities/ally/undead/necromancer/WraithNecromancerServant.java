package com.k1sak1.goetyawaken.common.entities.ally.undead.necromancer;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AnimationState;

public class WraithNecromancerServant extends AbstractWraithNecromancer {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState flyAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState summonAnimationState = new AnimationState();
    public final AnimationState spellAnimationState = new AnimationState();
    public final AnimationState alertAnimationState = new AnimationState();
    public final AnimationState shockwaveAnimationState = new AnimationState();

    public WraithNecromancerServant(EntityType<? extends AbstractWraithNecromancer> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(8, new net.minecraft.world.entity.ai.goal.RandomStrollGoal(this, 1.0D));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn,
            MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        spawnDataIn = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        if (dataTag != null && dataTag.contains("NecroLevel")) {
            int level = dataTag.getInt("NecroLevel");
            this.setNecroLevel(level);
        }
        this.setConfigurableAttributes();
        this.setHealth(this.getMaxHealth());
        this.addSummon(com.Polarice3.Goety.common.entities.ModEntityType.WRAITH_SERVANT.get());
        this.addSummon(com.Polarice3.Goety.common.entities.ModEntityType.MUCK_WRAITH_SERVANT.get());
        this.addSummon(com.Polarice3.Goety.common.entities.ModEntityType.BORDER_WRAITH_SERVANT.get());
        this.addSummon(com.Polarice3.Goety.common.entities.ModEntityType.REAPER_SERVANT.get());
        this.setPersistenceRequired();

        return spawnDataIn;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            this.idleAnimationState.animateWhen(this.getAnimationState() == IDLE_ANIM, this.tickCount);
            this.flyAnimationState.animateWhen(this.getAnimationState() == FLY_ANIM, this.tickCount);
            this.attackAnimationState.animateWhen(this.getAnimationState() == ATTACK_ANIM, this.tickCount);
            this.summonAnimationState.animateWhen(this.getAnimationState() == SUMMON_ANIM, this.tickCount);
            this.spellAnimationState.animateWhen(this.getAnimationState() == SPELL_ANIM, this.tickCount);
            this.alertAnimationState.animateWhen(this.getAnimationState() == ALERT_ANIM, this.tickCount);
            this.shockwaveAnimationState.animateWhen(this.getAnimationState() == SHOCKWAVE_ANIM, this.tickCount);
        } else {
            if (!this.isShooting() && !this.isSpellCasting() &&
                    this.getAnimationState() != SUMMON_ANIM &&
                    this.getAnimationState() != SPELL_ANIM &&
                    this.getAnimationState() != ATTACK_ANIM &&
                    this.getAnimationState() != ALERT_ANIM &&
                    this.getAnimationState() != SHOCKWAVE_ANIM) {
                double speed = this.getDeltaMovement().horizontalDistance();
                if (speed > 0.3D) {
                    if (this.getAnimationState() != FLY_ANIM) {
                        this.setAnimationState(FLY_ANIM);
                    }
                } else {
                    if (this.getAnimationState() != IDLE_ANIM) {
                        this.setAnimationState(IDLE_ANIM);
                    }
                }
            }
        }
    }

    @Override
    public void soulJar() {
        if (this.getTrueOwner() instanceof Player player
                && com.Polarice3.Goety.config.MobsConfig.NecromancerSoulJar.get()) {
            Optional<ItemStack> optional = player.getInventory().items.stream()
                    .filter(itemStack1 -> itemStack1.is(com.Polarice3.Goety.common.items.ModItems.EMPTY_SOUL_JAR.get()))
                    .findFirst();
            if (optional.isPresent()) {
                ItemStack original = optional.get();
                if (original.is(com.Polarice3.Goety.common.items.ModItems.EMPTY_SOUL_JAR.get())) {
                    if (!player.isCreative()) {
                        original.shrink(1);
                    }
                    ItemStack itemStack = new ItemStack(
                            com.k1sak1.goetyawaken.common.items.ModItems.WRAITH_NECROMANCER_SOUL_JAR.get());
                    com.Polarice3.Goety.common.items.revive.SoulJar.setOwnerName(this.getTrueOwner(), itemStack);
                    com.Polarice3.Goety.common.items.revive.SoulJar.setSummon(this, itemStack);
                    com.k1sak1.goetyawaken.common.items.WraithNecromancerSoulJar.setWraith(itemStack);
                    CompoundTag tag = itemStack.getOrCreateTag();
                    if (this.getSummonList()
                            .contains(com.Polarice3.Goety.common.entities.ModEntityType.ZOMBIE_SERVANT.get())) {
                        tag.putBoolean("HasZombieServants", true);
                    }
                    if (this.getSummonList()
                            .contains(com.Polarice3.Goety.common.entities.ModEntityType.SKELETON_SERVANT.get())) {
                        tag.putBoolean("HasSkeletonServants", true);
                    }
                    if (this.getSummonList()
                            .contains(com.Polarice3.Goety.common.entities.ModEntityType.VANGUARD_SERVANT.get())) {
                        tag.putBoolean("HasVanguardServants", true);
                    }
                    if (this.getSummonList()
                            .contains(com.Polarice3.Goety.common.entities.ModEntityType.BLACKGUARD_SERVANT.get())) {
                        tag.putBoolean("HasBlackguardServants", true);
                    }

                    com.Polarice3.Goety.utils.SEHelper.addCooldown(player, itemStack.getItem(),
                            com.Polarice3.Goety.utils.MathHelper.secondsToTicks(30));
                    if (!player.getInventory().add(itemStack)) {
                        player.drop(itemStack, false, true);
                    }
                }
            }
        }
    }
}