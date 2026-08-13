package com.k1sak1.goetyawaken.common.entities.ally.Integration;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.utils.MobUtil;
import com.izofar.takesapillage.entity.IShieldedMob;
import com.izofar.takesapillage.entity.ai.ShieldGoal;
import com.izofar.takesapillage.init.ModSoundEvents;
import com.k1sak1.goetyawaken.config.AttributesConfig;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;
import java.util.UUID;

//Based on https://github.com/izofar/takes-a-pillage, Original by izofar
public class LegionerServant extends AbstractIllagerServant implements IShieldedMob {

    private static final EntityDataAccessor<Boolean> DATA_IS_SHIELDED = SynchedEntityData.defineId(
            LegionerServant.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SHIELD_HAND = SynchedEntityData.defineId(
            LegionerServant.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_SHIELD_COOLDOWN = SynchedEntityData.defineId(
            LegionerServant.class,
            EntityDataSerializers.INT);

    private static final UUID SPEED_MODIFIER_ATTACKING_UUID = UUID.fromString("3520BCE0-D755-458F-944B-A528DB8EF9DC");
    private static final AttributeModifier SPEED_MODIFIER_BLOCKING = new AttributeModifier(
            SPEED_MODIFIER_ATTACKING_UUID, "Shielded speed penalty", -0.10D, AttributeModifier.Operation.ADDITION);

    public LegionerServant(EntityType<? extends AbstractIllagerServant> entitytype, Level world) {
        super(entitytype, world);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.LegionerServantMovementSpeed.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.LegionerServantFollowRange.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.LegionerServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.LegionerServantDamage.get())
                .add(Attributes.ARMOR, AttributesConfig.LegionerServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.LegionerServantArmorToughness.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.LegionerServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.LegionerServantDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED),
                AttributesConfig.LegionerServantMovementSpeed.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE),
                AttributesConfig.LegionerServantFollowRange.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR),
                AttributesConfig.LegionerServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.LegionerServantArmorToughness.get());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ShieldGoal<>(this, Player.class));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.25D, true));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, RaiderServant.class)).setAlertOthers());
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));

    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_SHIELDED, false);
        this.entityData.define(DATA_SHIELD_HAND, false);
        this.entityData.define(DATA_SHIELD_COOLDOWN, 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            this.decrementShieldCooldown();
        }
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

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource randomsource, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
    }

    @Override
    public AbstractIllagerServant.IllagerServantArmPose getArmPose() {
        if (this.isAggressive())
            return AbstractIllagerServant.IllagerServantArmPose.ATTACKING;
        return this.isCelebrating() ? AbstractIllagerServant.IllagerServantArmPose.CELEBRATING : null;
    }

    @Override
    public void knockback(double x, double y, double z) {
        if (!this.isUsingShield()) {
            super.knockback(x, y, z);
        } else {
            this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 0.8F + this.level().random.nextFloat() * 0.4F);
        }
    }

    @Override
    protected void blockUsingShield(LivingEntity attacker) {
        super.blockUsingShield(attacker);
        if (attacker.getMainHandItem().canDisableShield(this.useItem, this, attacker)) {
            this.disableShield();
        }
    }

    private void disableShield() {
        this.setShieldCooldown(60);
        this.stopUsingShield();
        this.level().broadcastEntityEvent(this, (byte) 30);
        this.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F);
    }

    @Override
    public boolean isShieldDisabled() {
        return this.getShieldCooldown() > 0;
    }

    @Override
    public void startUsingShield() {
        if (this.isUsingShield() || this.isShieldDisabled())
            return;
        for (InteractionHand interactionhand : InteractionHand.values()) {
            if (this.getItemInHand(interactionhand).is(Items.SHIELD)) {
                this.startUsingItem(interactionhand);
                this.setUsingShield(true);
                this.setShieldMainhand(interactionhand == InteractionHand.MAIN_HAND);
                AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
                if (attributeinstance != null && !attributeinstance.hasModifier(SPEED_MODIFIER_BLOCKING)) {
                    attributeinstance.addTransientModifier(SPEED_MODIFIER_BLOCKING);
                }
            }
        }
    }

    @Override
    public void stopUsingShield() {
        if (!this.isUsingShield())
            return;
        for (InteractionHand interactionhand : InteractionHand.values()) {
            if (this.getItemInHand(interactionhand).is(Items.SHIELD)) {
                this.stopUsingItem();
                this.setUsingShield(false);
                AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
                if (attributeinstance != null)
                    attributeinstance.removeModifier(SPEED_MODIFIER_BLOCKING);
            }
        }
    }

    public boolean isUsingShield() {
        return this.entityData.get(DATA_IS_SHIELDED);
    }

    public void setUsingShield(boolean isShielded) {
        this.entityData.set(DATA_IS_SHIELDED, isShielded);
    }

    private boolean isShieldMainhand() {
        return this.entityData.get(DATA_SHIELD_HAND);
    }

    private void setShieldMainhand(boolean isShieldedMainHand) {
        this.entityData.set(DATA_SHIELD_HAND, isShieldedMainHand);
    }

    private int getShieldCooldown() {
        return this.entityData.get(DATA_SHIELD_COOLDOWN);
    }

    private void setShieldCooldown(int newShieldCooldown) {
        this.entityData.set(DATA_SHIELD_COOLDOWN, newShieldCooldown);
    }

    private void decrementShieldCooldown() {
        this.setShieldCooldown(Math.max(this.getShieldCooldown() - 1, 0));
    }

    public InteractionHand getShieldHand() {
        return this.isUsingShield() ? (this.isShieldMainhand() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND)
                : null;
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return ModSoundEvents.LEGIONER_CELEBRATE.get();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.LEGIONER_AMBIENT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.LEGIONER_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33306_) {
        return ModSoundEvents.LEGIONER_HURT.get();
    }

    @Override
    public boolean canBeLeader() {
        return true;
    }

    @Override
    public boolean canOpenDoors() {
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
        return itemStack.getItem() instanceof SwordItem || itemStack.is(ItemTags.SWORDS);
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
                if (itemstack.getItem() instanceof ShieldItem) {
                    ItemStack offhandStack = this.getOffhandItem();
                    this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                    this.setItemSlot(EquipmentSlot.OFFHAND, itemstack.copy());
                    if (!offhandStack.isEmpty()) {
                        this.dropEquipment(EquipmentSlot.OFFHAND, offhandStack);
                        this.setGuaranteedDrop(EquipmentSlot.OFFHAND);
                    }
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
}
