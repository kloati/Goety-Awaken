package com.k1sak1.goetyawaken.common.entities.hostile.undead.skeleton;

import com.k1sak1.goetyawaken.common.entities.ally.undead.skeleton.VanguardChampion;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class HostileVanguardChampion extends VanguardChampion implements Enemy {

    public HostileVanguardChampion(EntityType<? extends Summoned> type, Level worldIn) {
        super(type, worldIn);
        this.setHostile(true);
    }

    @Override
    public int xpReward() {
        return 40;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(3,
                new NearestAttackableTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
    }

    // @Override
    // public void die(DamageSource source) {
    // if (!this.level().isClientSide && source.getDirectEntity() != null &&
    // (source.getDirectEntity().getType().toString().contains("necro_bolt"))) {
    // if (source.getEntity() instanceof Player player && this.random.nextInt(4) ==
    // 0) {
    // this.convertToServant(player);
    // }
    // }
    // super.die(source);
    // }

    // private boolean convertToServant(Player player) {
    // if (this.level() instanceof ServerLevel serverLevel) {
    // VanguardChampion servant = (VanguardChampion) this
    // .convertTo(ModEntityType.VANGUARD_CHAMPION.get(), true);
    // if (servant != null) {
    // servant.setTrueOwner(player);
    // if (this.getTarget() != null) {
    // servant.setTarget(this.getTarget());
    // }
    // for (EquipmentSlot slot : EquipmentSlot.values()) {
    // ItemStack stack = this.getItemBySlot(slot);
    // if (!stack.isEmpty()) {
    // servant.setItemSlot(slot, stack.copy());
    // }
    // }
    // servant.finalizeSpawn(serverLevel,
    // this.level().getCurrentDifficultyAt(servant.blockPosition()),
    // MobSpawnType.CONVERSION, null, null);
    // if (!servant.isSilent()) {
    // servant.level().levelEvent(null, 1026, servant.blockPosition(), 0);
    // }
    // net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, servant);
    // return true;
    // }
    // }
    // return false;
    // }
}
