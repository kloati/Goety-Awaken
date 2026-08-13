package com.k1sak1.goetyawaken.mixin;

import com.Polarice3.Goety.common.entities.ally.illager.CrusherServant;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.common.entities.ally.PaleGolemServant;
import com.k1sak1.goetyawaken.common.entities.ally.illager.RoyalguardServant;
import com.k1sak1.goetyawaken.common.entities.ally.illager.TowerGuardServant;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;
import java.util.Optional;

@Mixin(CrusherServant.class)
public class CrusherServantMixin {

    @Inject(method = "registerGoals", at = @At("TAIL"), remap = true)
    private void injectRepairShieldGoal(CallbackInfo ci) {
        CrusherServant self = (CrusherServant) (Object) this;
        self.goalSelector.addGoal(4, new RepairShieldGoal(self));
    }

    private static class RepairShieldGoal extends Goal {
        private static final int SEARCH_RADIUS = 16;
        private final CrusherServant crusher;

        public RepairShieldGoal(CrusherServant crusher) {
            this.crusher = crusher;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!(this.crusher.level() instanceof ServerLevel)) {
                return false;
            }
            if (this.crusher.getTarget() != null && this.crusher.getTarget().isAlive()) {
                return false;
            }
            return !this.crusher.itemsInInv(this::isPaleSteelIngot).isEmpty()
                    && this.findShieldlessAlly() != null;
        }

        @Override
        public boolean canContinueToUse() {
            return this.findShieldlessAlly() != null
                    && !this.crusher.itemsInInv(this::isPaleSteelIngot).isEmpty();
        }

        @Override
        public void tick() {
            LivingEntity ally = this.findShieldlessAlly();
            if (ally == null) {
                return;
            }

            if (!this.crusher.isWithinDistance(ally, 2.5D)) {
                this.crusher.getNavigation().moveTo(ally.getX(), ally.getY(), ally.getZ(), 0.75F);
            } else {
                this.crusher.getNavigation().stop();
                Optional<ItemStack> ingotOpt = this.crusher.itemsInInv(this::isPaleSteelIngot).stream().findFirst();
                if (ingotOpt.isPresent()) {
                    ItemStack ingot = ingotOpt.get();
                    ingot.shrink(1);
                    this.crusher.getInventory().setChanged();

                    if (ally instanceof TowerGuardServant towerGuard) {
                        towerGuard.regenerateShield();
                    } else if (ally instanceof RoyalguardServant royalGuard) {
                        royalGuard.setShield(true);
                        royalGuard.setShieldHealth(0);
                        royalGuard.setShieldHidden(false);
                    } else if (ally instanceof PaleGolemServant paleGolem) {
                        paleGolem.heal(paleGolem.getMaxHealth() * 0.20F);
                    }

                    this.crusher.level().playSound(null, ally.blockPosition(),
                            SoundEvents.ARMOR_EQUIP_GENERIC,
                            this.crusher.getSoundSource(), 1.0F, 1.0F);
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        private boolean isPaleSteelIngot(ItemStack stack) {
            return stack.is(ModItems.PALE_STEEL_INGOT.get());
        }

        private LivingEntity findShieldlessAlly() {
            return this.crusher.level().getEntitiesOfClass(
                    LivingEntity.class,
                    this.crusher.getBoundingBox().inflate(SEARCH_RADIUS),
                    entity -> {
                        if (entity == this.crusher) {
                            return false;
                        }
                        if (!MobUtil.areAllies(this.crusher, entity)) {
                            return false;
                        }
                        if (entity instanceof Mob mob && mob.getTarget() != null && mob.getTarget().isAlive()) {
                            return false;
                        }
                        if (entity instanceof TowerGuardServant towerGuard) {
                            return !towerGuard.hasShield() || towerGuard.isShieldHidden();
                        }
                        if (entity instanceof RoyalguardServant royalGuard) {
                            return !royalGuard.hasShield() || royalGuard.isShieldHidden();
                        }
                        if (entity instanceof PaleGolemServant paleGolem) {
                            return paleGolem.getHealth() < paleGolem.getMaxHealth();
                        }
                        return false;
                    }).stream().findFirst().orElse(null);
        }
    }
}
