package com.k1sak1.goetyawaken.common.entities.ally;

import com.Polarice3.Goety.common.entities.ally.AnimalSummon;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.ClimbOnTopOfPowderSnowGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import com.k1sak1.goetyawaken.config.AttributesConfig;

public class CaerbannogRabbitServant extends AnimalSummon {
    private int jumpTicks;
    private int jumpDuration;
    private boolean wasOnGround;
    private int jumpDelayTicks;
    private int moreCarrotTicks;

    public CaerbannogRabbitServant(EntityType<? extends AnimalSummon> type, Level worldIn) {
        super(type, worldIn);
        this.jumpControl = new RabbitJumpControl(this);
        this.moveControl = new RabbitMoveControl(this);
        this.setSpeedModifier(0.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ClimbOnTopOfPowderSnowGoal(this, this.level()));
        this.goalSelector.addGoal(1, new BreedGoal(this, 0.8D));
        this.goalSelector.addGoal(2,
                new TemptGoal(this, 1.0D, Ingredient.of(Items.CARROT, Items.GOLDEN_CARROT, Blocks.DANDELION), false));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.4D, true));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        super.registerGoals();
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.CaerbannogRabbitServantHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.CaerbannogRabbitServantDamage.get())
                .add(Attributes.ARMOR, AttributesConfig.CaerbannogRabbitServantArmor.get());
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return com.k1sak1.goetyawaken.Config.caerbannogRabbitServantLimit;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.RABBIT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.RABBIT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.RABBIT_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pState) {
        this.playSound(SoundEvents.RABBIT_JUMP, 0.15F, 1.0F);
    }

    @Override
    public MobType getMobType() {
        return ModMobType.NATURAL;
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        this.playSound(SoundEvents.RABBIT_ATTACK, 1.0F,
                (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2F + 1.0F);
        float f = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        boolean flag = pEntity.hurt(this.damageSources().mobAttack(this), f);

        if (flag) {
            if (this.isUpgraded() && pEntity instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(
                        new MobEffectInstance(GoetyEffects.CURSED.get(), MathHelper.secondsToTicks(4), 0), this);
            }
        }

        return flag;
    }

    public float getJumpCompletion(float pPartialTick) {
        return this.jumpDuration == 0 ? 0.0F : ((float) this.jumpTicks + pPartialTick) / (float) this.jumpDuration;
    }

    public void aiStep() {
        super.aiStep();
        if (this.jumpTicks != this.jumpDuration) {
            ++this.jumpTicks;
        } else if (this.jumpDuration != 0) {
            this.jumpTicks = 0;
            this.jumpDuration = 0;
        }
    }

    @Override
    public void customServerAiStep() {
        if (this.jumpDelayTicks > 0) {
            --this.jumpDelayTicks;
        }

        if (this.moreCarrotTicks > 0) {
            this.moreCarrotTicks -= this.random.nextInt(3);
            if (this.moreCarrotTicks < 0) {
                this.moreCarrotTicks = 0;
            }
        }

        if (this.onGround()) {
            if (!this.wasOnGround) {
                this.setJumping(false);
                this.checkLandingDelay();
            }

            if (this.jumpDelayTicks == 0) {
                LivingEntity livingentity = this.getTarget();
                if (livingentity != null && this.distanceToSqr(livingentity) < 16.0D) {
                    this.facePoint(livingentity.getX(), livingentity.getZ());
                    this.moveControl.setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(),
                            2.2D);
                    this.startJumping();
                    this.wasOnGround = true;
                }
            }

            RabbitJumpControl rabbitJumpControl = (RabbitJumpControl) this.jumpControl;
            if (!rabbitJumpControl.wantJump()) {
                if (this.moveControl.hasWanted() && this.jumpDelayTicks == 0) {
                    Path path = this.navigation.getPath();
                    Vec3 vec3 = new Vec3(this.moveControl.getWantedX(), this.moveControl.getWantedY(),
                            this.moveControl.getWantedZ());
                    if (path != null && !path.isDone()) {
                        vec3 = path.getNextEntityPos(this);
                    }

                    this.facePoint(vec3.x, vec3.z);
                    this.startJumping();
                }
            } else if (!rabbitJumpControl.canJump()) {
                this.enableJumpControl();
            }
        }

        this.wasOnGround = this.onGround();
    }

    private void facePoint(double pX, double pZ) {
        this.setYRot(
                (float) (Mth.atan2(pZ - this.getZ(), pX - this.getX()) * (double) (180F / (float) Math.PI)) - 90.0F);
    }

    private void enableJumpControl() {
        ((RabbitJumpControl) this.jumpControl).setCanJump(true);
    }

    private void disableJumpControl() {
        ((RabbitJumpControl) this.jumpControl).setCanJump(false);
    }

    private void setLandingDelay() {
        if (this.moveControl.getSpeedModifier() < 2.2D) {
            this.jumpDelayTicks = 10;
        } else {
            this.jumpDelayTicks = 1;
        }
    }

    private void checkLandingDelay() {
        this.setLandingDelay();
        this.disableJumpControl();
    }

    protected void jumpFromGround() {
        super.jumpFromGround();
        double d0 = this.moveControl.getSpeedModifier();
        if (d0 > 0.0D) {
            double d1 = this.getDeltaMovement().horizontalDistanceSqr();
            if (d1 < 0.01D) {
                this.moveRelative(0.1F, new Vec3(0.0D, 0.0D, 1.0D));
            }
        }

        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 1);
        }
    }

    public void startJumping() {
        this.setJumping(true);
        this.jumpDuration = 10;
        this.jumpTicks = 0;
    }

    protected float getJumpPower() {
        float f = 0.3F;
        if (this.horizontalCollision
                || this.moveControl.hasWanted() && this.moveControl.getWantedY() > this.getY() + 0.5D) {
            f = 0.5F;
        }

        Path path = this.navigation.getPath();
        if (path != null && !path.isDone()) {
            Vec3 vec3 = path.getNextEntityPos(this);
            if (vec3.y > this.getY() + 0.5D) {
                f = 0.5F;
            }
        }

        if (this.moveControl.getSpeedModifier() <= 0.6D) {
            f = 0.2F;
        }

        return f + this.getJumpBoostPower();
    }

    public void setSpeedModifier(double pSpeedModifier) {
        this.getNavigation().setSpeedModifier(pSpeedModifier);
        this.moveControl.setWantedPosition(this.moveControl.getWantedX(), this.moveControl.getWantedY(),
                this.moveControl.getWantedZ(), pSpeedModifier);
    }

    public void handleEntityEvent(byte pId) {
        if (pId == 1) {
            this.spawnSprintParticle();
            this.jumpDuration = 10;
            this.jumpTicks = 0;
        } else {
            super.handleEntityEvent(pId);
        }
    }

    static class RabbitJumpControl extends JumpControl {
        private final CaerbannogRabbitServant rabbit;
        private boolean canJump;

        public RabbitJumpControl(CaerbannogRabbitServant pRabbit) {
            super(pRabbit);
            this.rabbit = pRabbit;
        }

        public boolean wantJump() {
            return this.jump;
        }

        public boolean canJump() {
            return this.canJump;
        }

        public void setCanJump(boolean pCanJump) {
            this.canJump = pCanJump;
        }

        public void tick() {
            if (this.jump) {
                this.rabbit.startJumping();
                this.jump = false;
            }
        }
    }

    static class RabbitMoveControl extends MoveControl {
        private final CaerbannogRabbitServant rabbit;
        private double nextJumpSpeed;

        public RabbitMoveControl(CaerbannogRabbitServant pRabbit) {
            super(pRabbit);
            this.rabbit = pRabbit;
        }

        public void tick() {
            if (this.rabbit.onGround() && !this.rabbit.jumping
                    && !((RabbitJumpControl) this.rabbit.jumpControl).wantJump()) {
                this.rabbit.setSpeedModifier(0.0D);
            } else if (this.hasWanted()) {
                this.rabbit.setSpeedModifier(this.nextJumpSpeed);
            }

            super.tick();
        }

        public void setWantedPosition(double pX, double pY, double pZ, double pSpeed) {
            if (this.rabbit.isInWater()) {
                pSpeed = 1.5D;
            }

            super.setWantedPosition(pX, pY, pZ, pSpeed);
            if (pSpeed > 0.0D) {
                this.nextJumpSpeed = pSpeed;
            }
        }
    }

    public boolean isFood(ItemStack pStack) {
        return pStack.is(Items.CARROT) || pStack.is(Items.GOLDEN_CARROT) || pStack.is(Blocks.DANDELION.asItem());
    }

    @Override
    public AnimalSummon getBreedOffspring(ServerLevel pLevel, AnimalSummon pOtherParent) {
        CaerbannogRabbitServant rabbit = new CaerbannogRabbitServant(
                (EntityType<? extends AnimalSummon>) this.getType(), pLevel);
        if (this.getTrueOwner() != null) {
            rabbit.setTrueOwner(this.getTrueOwner());
        }
        return rabbit;
    }

    public boolean canMate(AnimalSummon pOtherAnimal) {
        if (pOtherAnimal instanceof CaerbannogRabbitServant otherRabbit) {
            if (this.getTrueOwner() == null && otherRabbit.getTrueOwner() == null) {
                return true;
            }
            if (this.getTrueOwner() != null && otherRabbit.getTrueOwner() != null) {
                return this.getTrueOwner().equals(otherRabbit.getTrueOwner());
            }
        }
        return false;
    }

    static class RabbitPanicGoal extends PanicGoal {
        private final CaerbannogRabbitServant rabbit;

        public RabbitPanicGoal(CaerbannogRabbitServant pRabbit, double pSpeedModifier) {
            super(pRabbit, pSpeedModifier);
            this.rabbit = pRabbit;
        }

        public void tick() {
            super.tick();
            this.rabbit.setSpeedModifier(this.speedModifier);
        }
    }

    @Override
    public void setUpgraded(boolean upgraded) {
        super.setUpgraded(upgraded);
        AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance armor = this.getAttribute(Attributes.ARMOR);
        AttributeInstance attack = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (health != null && armor != null && attack != null) {
            if (upgraded) {
                health.setBaseValue(AttributesConfig.CaerbannogRabbitServantHealth.get() + 3.0D);
                armor.setBaseValue(AttributesConfig.CaerbannogRabbitServantArmor.get() + 2.0D);
                attack.setBaseValue(AttributesConfig.CaerbannogRabbitServantDamage.get() + 2.0D);
            } else {
                health.setBaseValue(AttributesConfig.CaerbannogRabbitServantHealth.get());
                armor.setBaseValue(AttributesConfig.CaerbannogRabbitServantArmor.get());
                attack.setBaseValue(AttributesConfig.CaerbannogRabbitServantDamage.get());
            }
        }
        this.setHealth(this.getMaxHealth());
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
        if (this.isHostile()
                && this.level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_DOMOBLOOT)) {
            if (this.random.nextFloat() < 0.5F) {
                this.spawnAtLocation(Items.RABBIT);
            }
            if (this.random.nextFloat() < 0.1F) {
                this.spawnAtLocation(Items.RABBIT_FOOT);
            }
            if (this.random.nextFloat() < 0.1F) {
                this.spawnAtLocation(ModItems.RAGING_MATTER.get());
            }
        }
    }
}