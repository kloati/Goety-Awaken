package com.k1sak1.goetyawaken.common.entities.ally.Integration;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import javax.annotation.Nullable;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import java.util.EnumSet;
import java.util.function.Predicate;

//Based on https://github.com/izofar/takes-a-pillage, Original by izofar
public class SkirmisherServant extends AbstractIllagerServant {

    private static final Predicate<Difficulty> DOOR_BREAKING_PREDICATE = (difficulty -> (difficulty == Difficulty.NORMAL
            || difficulty == Difficulty.HARD));

    public SkirmisherServant(EntityType<? extends AbstractIllagerServant> entityType, Level world) {
        super(entityType, world);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.SkirmisherServantMovementSpeed.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.SkirmisherServantFollowRange.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.SkirmisherServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.SkirmisherServantDamage.get())
                .add(Attributes.ARMOR, AttributesConfig.SkirmisherServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.SkirmisherServantArmorToughness.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.SkirmisherServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.SkirmisherServantDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED),
                AttributesConfig.SkirmisherServantMovementSpeed.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE),
                AttributesConfig.SkirmisherServantFollowRange.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR),
                AttributesConfig.SkirmisherServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.SkirmisherServantArmorToughness.get());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SkirmisherServantBreakDoorGoal(this));
        this.goalSelector.addGoal(4, new SkirmisherServantMeleeAttackGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, RaiderServant.class)).setAlertOthers());
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    @Override
    public AbstractIllagerServant.IllagerServantArmPose getArmPose() {
        if (this.isAggressive())
            return AbstractIllagerServant.IllagerServantArmPose.ATTACKING;
        return this.isCelebrating() ? AbstractIllagerServant.IllagerServantArmPose.CELEBRATING
                : AbstractIllagerServant.IllagerServantArmPose.CROSSED;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource randomsource, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.VINDICATOR_CELEBRATE;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelaccessor, DifficultyInstance difficulty,
            MobSpawnType spawntype, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        SpawnGroupData spawngroupdata = super.finalizeSpawn(levelaccessor, difficulty, spawntype, data, tag);
        ((GroundPathNavigation) this.getNavigation()).setCanOpenDoors(true);
        RandomSource randomsource = levelaccessor.getRandom();
        this.populateDefaultEquipmentSlots(randomsource, difficulty);
        this.populateDefaultEquipmentEnchantments(randomsource, difficulty);
        return spawngroupdata;
    }

    private static class SkirmisherServantMeleeAttackGoal extends MeleeAttackGoal {
        public SkirmisherServantMeleeAttackGoal(SkirmisherServant entity) {
            super(entity, 1.1D, false);
        }

        @Override
        protected double getAttackReachSqr(LivingEntity livingentity) {
            if (this.mob.getVehicle() instanceof Ravager) {
                float f = this.mob.getVehicle().getBbWidth() - 0.1F;
                return (f * 2.0F * f * 2.0F + livingentity.getBbWidth());
            }
            return super.getAttackReachSqr(livingentity);
        }
    }

    @Override
    public boolean canOpenDoors() {
        return true;
    }

    @Override
    public boolean canBeLeader() {
        return true;
    }

    @Override
    public boolean canWearArmor() {
        return true;
    }

    @Override
    public boolean canHaveWeapon() {
        return true;
    }

    @Override
    public boolean isMainWeapon(ItemStack itemStack) {
        return itemStack.getItem() instanceof AxeItem || itemStack.is(ItemTags.AXES)
                || itemStack.is(ModTags.Items.VINDICATOR_WEAPONS);
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Item item = itemstack.getItem();
        ItemStack itemstack2 = this.getMainHandItem();
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (!(pPlayer.getOffhandItem().getItem() instanceof IWand)) {
                if (this.isMainWeapon(itemstack)) {
                    this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                    this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.copy());
                    this.dropEquipment(EquipmentSlot.MAINHAND, itemstack2);
                    this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D),
                                this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
                    }
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VINDICATOR_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VINDICATOR_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_34103_) {
        return SoundEvents.VINDICATOR_HURT;
    }

    private static class SkirmisherServantBreakDoorGoal extends BreakDoorGoal {
        public SkirmisherServantBreakDoorGoal(Mob mob) {
            super(mob, 6, SkirmisherServant.DOOR_BREAKING_PREDICATE);
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return this.getMob().random.nextInt(reducedTickDelay(10)) == 0
                    && super.canUse();
        }

        public void start() {
            super.start();
            this.mob.setNoActionTime(0);
        }

        private SkirmisherServant getMob() {
            return (SkirmisherServant) this.mob;
        }
    }
}
