package com.k1sak1.goetyawaken.common.entities.ally.undead.necromancer;

import com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.AbstractParchedNecromancer;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.world.DifficultyInstance;

public class ParchedNecromancerServant extends AbstractParchedNecromancer {

    public ParchedNecromancerServant(EntityType<? extends AbstractNecromancer> type, Level level) {
        super(type, level);
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
        this.setPersistenceRequired();
        return spawnDataIn;
    }

    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(5, new WanderGoal<>(this, 1.0D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
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
                            com.k1sak1.goetyawaken.common.items.ModItems.PARCHED_NECROMANCER_SOUL_JAR.get());
                    com.Polarice3.Goety.common.items.revive.SoulJar.setOwnerName(this.getTrueOwner(), itemStack);
                    com.Polarice3.Goety.common.items.revive.SoulJar.setSummon(this, itemStack);
                    com.k1sak1.goetyawaken.common.items.ParchedNecromancerSoulJar.setParched(itemStack);
                    CompoundTag tag = itemStack.getOrCreateTag();
                    if (this.getSummonList()
                            .contains(com.Polarice3.Goety.common.entities.ModEntityType.HUSK_SERVANT.get())) {
                        tag.putBoolean("HasHuskServants", true);
                    }
                    if (this.getSummonList()
                            .contains(com.k1sak1.goetyawaken.common.entities.ModEntityType.PARCHED_SERVANT.get())) {
                        tag.putBoolean("HasParchedServants", true);
                    }
                    if (this.getSummonList()
                            .contains(com.Polarice3.Goety.common.entities.ModEntityType.VANGUARD_SERVANT.get())) {
                        tag.putBoolean("HasVanguardServants", true);
                    }
                    if (this.getSummonList()
                            .contains(com.Polarice3.Goety.common.entities.ModEntityType.BLACKGUARD_SERVANT.get())) {
                        tag.putBoolean("HasBlackguardServants", true);
                    }
                    if (this.getSummonList()
                            .contains(com.Polarice3.Goety.common.entities.ModEntityType.WRAITH_SERVANT.get())) {
                        tag.putBoolean("HasWraithServants", true);
                    }
                    if (this.getSummonList()
                            .contains(com.Polarice3.Goety.common.entities.ModEntityType.REAPER_SERVANT.get())) {
                        tag.putBoolean("HasReaperServants", true);
                    }
                    if (this.getSummonList()
                            .contains(com.Polarice3.Goety.common.entities.ModEntityType.PHANTOM_SERVANT.get())) {
                        tag.putBoolean("HasPhantomServants", true);
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