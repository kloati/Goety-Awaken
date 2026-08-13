package com.k1sak1.goetyawaken.common.entities.hostile;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.k1sak1.goetyawaken.api.IAncientGlint;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import com.Polarice3.Goety.utils.ModLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.NonNullList;

public class HostileGiantGhast extends GiantGhast implements Enemy {
    private final ModServerBossInfo bossInfo;

    public HostileGiantGhast(EntityType<? extends GiantGhast> type, Level worldIn) {
        super(type, worldIn);
        this.setHostile(true);
        this.setPersistenceRequired();
        this.bossInfo = new ModServerBossInfo(this, BossEvent.BossBarColor.RED, false, false);
    }

    public HostileGiantGhast(Level worldIn) {
        this(com.k1sak1.goetyawaken.common.entities.ModEntityType.HOSTILE_GIANT_GHAST.get(), worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal<>(this,
                net.minecraft.world.entity.player.Player.class, true));
        this.targetSelector.addGoal(2, new net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        com.Polarice3.Goety.utils.MiscCapHelper.updateMobTarget(this);

        if (!this.level().isClientSide()) {
            this.setAggressive(this.getTarget() != null);
        }

        if (this.bossInfo != null && this.level() instanceof net.minecraft.server.level.ServerLevel) {
            this.bossInfo.update();
        }
    }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        if (this.bossInfo != null && this.level() instanceof net.minecraft.server.level.ServerLevel) {
            this.bossInfo.update();
        }
        return super.hurt(source, amount);
    }

    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (this instanceof IAncientGlint glint && glint.hasAncientGlint()
                && "ancient".equals(glint.getGlintTextureType())) {
            return;
        }
        if (this.bossInfo != null) {
            this.bossInfo.addPlayer(player);
        }
    }

    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        if (this instanceof IAncientGlint glint && glint.hasAncientGlint()
                && "ancient".equals(glint.getGlintTextureType())) {
            return;
        }
        if (this.bossInfo != null) {
            this.bossInfo.removePlayer(player);
        }
    }

    @Override
    public void remove(net.minecraft.world.entity.Entity.RemovalReason pReason) {
        if (pReason == net.minecraft.world.entity.Entity.RemovalReason.KILLED) {
            if (this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                BlockPos chestPos = this.blockPosition();
                this.createLootChest(chestPos, this.damageSources().generic());
            }
        }
        super.remove(pReason);
    }

    protected void createLootChest(BlockPos blockPos, DamageSource cause) {
        if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            this.level().setBlockAndUpdate(blockPos, ModBlocks.LOFTY_CHEST.get().defaultBlockState());
            LootParams.Builder lootParamsBuilder = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.THIS_ENTITY, this)
                    .withParameter(LootContextParams.ORIGIN, this.position())
                    .withParameter(LootContextParams.DAMAGE_SOURCE, cause)
                    .withOptionalParameter(LootContextParams.KILLER_ENTITY, cause.getEntity())
                    .withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, cause.getDirectEntity());

            if (this.getKillCredit() instanceof net.minecraft.world.entity.player.Player player) {
                lootParamsBuilder = lootParamsBuilder
                        .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
                        .withLuck(player.getLuck());
            }

            LootParams lootParams = lootParamsBuilder.create(LootContextParamSets.ENTITY);
            net.minecraft.resources.ResourceLocation lootTableId = new net.minecraft.resources.ResourceLocation(
                    "goetyawaken", "entities/miniboss_giant_ghast");
            LootTable table = serverLevel.getServer().getLootData().getLootTable(lootTableId);
            ObjectArrayList<ItemStack> lootItems = table.getRandomItems(lootParams);
            java.util.List<Integer> availableSlots = ModLootTables.getAvailableSlots(this.random);
            ModLootTables.shuffleAndSplitItems(lootItems, availableSlots.size(), this.random);

            NonNullList<ItemStack> finalLoot = NonNullList.withSize(27, ItemStack.EMPTY);
            for (ItemStack itemstack : lootItems) {
                if (!availableSlots.isEmpty()) {
                    if (itemstack.isEmpty()) {
                        finalLoot.set(availableSlots.remove(availableSlots.size() - 1), ItemStack.EMPTY);
                    } else {
                        finalLoot.set(availableSlots.remove(availableSlots.size() - 1), itemstack);
                    }
                }
            }
            if (this.level().getBlockEntity(blockPos) instanceof net.minecraft.world.Container container) {
                for (int i = 0; i < container.getContainerSize(); i++) {
                    container.setItem(i, finalLoot.get(i));
                }
            }
        }
    }
}
