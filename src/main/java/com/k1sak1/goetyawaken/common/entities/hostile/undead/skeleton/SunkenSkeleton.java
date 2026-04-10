package com.k1sak1.goetyawaken.common.entities.hostile.undead.skeleton;

import com.Polarice3.Goety.common.entities.ai.path.ModWaterPathNavigation;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SunkenSkeletonServant;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.entities.ally.undead.skeleton.ParchedServant;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class SunkenSkeleton extends SunkenSkeletonServant implements Enemy {
    protected final ModWaterPathNavigation waterNavigation;
    protected final GroundPathNavigation groundNavigation;
    public double arrowPower = 0.0;

    public SunkenSkeleton(EntityType<? extends SunkenSkeletonServant> type, Level worldIn) {
        super(type, worldIn);
        this.setHostile(true);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        this.waterNavigation = new ModWaterPathNavigation(this, worldIn);
        this.groundNavigation = new GroundPathNavigation(this, worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new RestrictSunGoal(this));
        this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Wolf.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(3,
                new NearestAttackableTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.SunkenSkeletonServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.SunkenSkeletonServantArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.SunkenSkeletonServantDamage.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.SunkenSkeletonServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR),
                AttributesConfig.SunkenSkeletonServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.SunkenSkeletonServantDamage.get());
    }

    public double getBaseRangeDamage() {
        return AttributesConfig.SunkenSkeletonServantRangeDamage.get();
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide && source.getDirectEntity() != null &&
                (source.getDirectEntity().getType().toString().contains("necro_bolt"))) {
            if (source.getEntity() instanceof Player player) {
                this.convertToServant(player);
            }
        }
        super.die(source);
    }

    private boolean convertToServant(Player player) {
        if (this.level() instanceof ServerLevel serverLevel) {
            SunkenSkeletonServant servant = (SunkenSkeletonServant) this
                    .convertTo(com.Polarice3.Goety.common.entities.ModEntityType.SUNKEN_SKELETON_SERVANT.get(), true);
            if (servant != null) {
                servant.setTrueOwner(player);
                if (this.getTarget() != null) {
                    servant.setTarget(this.getTarget());
                }
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    ItemStack stack = this.getItemBySlot(slot);
                    if (!stack.isEmpty()) {
                        servant.setItemSlot(slot, stack.copy());
                    }
                }
                servant.finalizeSpawn(serverLevel, this.level().getCurrentDifficultyAt(servant.blockPosition()),
                        MobSpawnType.CONVERSION, null, null);
                if (!servant.isSilent()) {
                    servant.level().levelEvent(null, 1026, servant.blockPosition(), 0);
                }
                net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, servant);
                return true;
            }
        }
        return false;
    }
}