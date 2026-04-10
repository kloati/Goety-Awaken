package com.k1sak1.goetyawaken.mixin.touhoulittlemaid;

import com.k1sak1.goetyawaken.common.entities.ally.Integration.MaidFairyServant;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MaidFairyServant.class)
public abstract class MaidFairyServantMixin extends Mob {

        protected MaidFairyServantMixin(EntityType<? extends Mob> p_21368_, Level p_21369_) {
                super(p_21368_, p_21369_);
        }

        @Inject(method = "registerGoals", at = @At("TAIL"))
        private void addTouhouLittleMaidGoals(CallbackInfo ci) {
                try {
                        Class<?> entityMaidClass = Class
                                        .forName("com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid");
                        Class<?> fairyAttackGoalClass = Class
                                        .forName("com.github.tartaricacid.touhoulittlemaid.entity.ai.goal.FairyAttackGoal");
                        Class<?> fairyNearestAttackableTargetGoalClass = Class.forName(
                                        "com.github.tartaricacid.touhoulittlemaid.entity.ai.goal.FairyNearestAttackableTargetGoal");

                        MaidFairyServant self = (MaidFairyServant) (Object) this;
                        this.goalSelector.addGoal(5, new LookAtPlayerGoal(self,
                                        entityMaidClass.asSubclass(net.minecraft.world.entity.LivingEntity.class),
                                        8.0F));
                        this.goalSelector.getAvailableGoals().removeIf(goalWrapper -> goalWrapper
                                        .getGoal() instanceof net.minecraft.world.entity.ai.goal.RangedAttackGoal);

                        Object fairyAttackGoal = fairyAttackGoalClass.getConstructor(
                                        fairyAttackGoalClass.getEnclosingClass(), double.class, double.class)
                                        .newInstance(self, 6.0, 1.0);
                        this.goalSelector.addGoal(1, (Goal) fairyAttackGoal);
                        this.targetSelector.getAvailableGoals()
                                        .removeIf(goalWrapper -> goalWrapper
                                                        .getGoal() instanceof NearestAttackableTargetGoal);

                        Object fairyNearestAttackableTargetGoal = fairyNearestAttackableTargetGoalClass
                                        .getConstructor(fairyNearestAttackableTargetGoalClass.getEnclosingClass())
                                        .newInstance(self);
                        this.targetSelector.addGoal(3, (Goal) fairyNearestAttackableTargetGoal);

                } catch (Exception e) {
                }
        }
}