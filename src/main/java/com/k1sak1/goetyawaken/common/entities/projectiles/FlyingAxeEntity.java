package com.k1sak1.goetyawaken.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import net.minecraft.Util;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class FlyingAxeEntity extends AbstractArrow implements ItemSupplier {
    private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData
            .defineId(FlyingAxeEntity.class, EntityDataSerializers.ITEM_STACK);

    public FlyingAxeEntity(EntityType<? extends FlyingAxeEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public FlyingAxeEntity(LivingEntity owner, Level level, ItemStack itemStack) {
        super(ModEntityType.FLYING_AXE.get(), owner.getX(), owner.getEyeY() - 0.1F, owner.getZ(), level);
        this.setOwner(owner);
        this.setItem(itemStack);
        this.playSound(SoundEvents.TRIDENT_THROW, 1.0F, 1.0F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ITEM_STACK, ItemStack.EMPTY);
    }

    public void setItem(ItemStack stack) {
        if (!stack.isEmpty()) {
            this.entityData.set(DATA_ITEM_STACK, Util.make(stack.copy(), (pStack) -> {
                pStack.setCount(1);
            }));
        }
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemstack = this.entityData.get(DATA_ITEM_STACK);
        return itemstack.isEmpty() ? new ItemStack(this.getDefaultItem()) : itemstack;
    }

    protected Item getDefaultItem() {
        return Items.IRON_AXE;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        ItemStack itemstack = this.getItem();
        if (!itemstack.isEmpty()) {
            pCompound.put("Item", itemstack.save(new CompoundTag()));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        ItemStack itemstack = ItemStack.of(pCompound.getCompound("Item"));
        this.setItem(itemstack);
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            if (!this.inGround && this.tickCount % 3 == 0) {
                ServerLevel serverLevel = (ServerLevel) this.level();
                serverLevel.sendParticles(
                        ParticleTypes.CRIT,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        1,
                        0.0D,
                        0.0D,
                        0.0D,
                        0.0D);
            }

            if (this.inGroundTime >= 40) {
                this.discard();
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        LivingEntity livingTarget = target instanceof LivingEntity ? (LivingEntity) target : null;

        Entity owner = this.getOwner();
        if (livingTarget != null && owner instanceof LivingEntity livingOwner) {
            if (MobUtil.areAllies(livingOwner, livingTarget)) {
                return;
            }
        }

        float damage = 8.0F;
        float knockback = 0.0F;
        int fireAspect = 0;
        ItemStack axeItem = this.getItem();
        if (!axeItem.isEmpty()) {
            Multimap<Attribute, AttributeModifier> attributes = axeItem.getAttributeModifiers(EquipmentSlot.MAINHAND);
            if (attributes.containsKey(Attributes.ATTACK_DAMAGE)) {
                for (AttributeModifier modifier : attributes.get(Attributes.ATTACK_DAMAGE)) {
                    if (modifier.getOperation() == AttributeModifier.Operation.ADDITION) {
                        damage = (float) modifier.getAmount();
                        break;
                    }
                }
            }

            knockback += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.KNOCKBACK, axeItem);
            fireAspect = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, axeItem);

            if (livingTarget != null) {
                damage += EnchantmentHelper.getDamageBonus(axeItem, livingTarget.getMobType());
            }
        }

        DamageSource damagesource = ModDamageSource.sword(this, owner == null ? this : owner);
        if (owner instanceof Player player) {
            damagesource = this.damageSources().playerAttack(player);
        } else if (owner instanceof Mob mob) {
            damagesource = this.damageSources().mobAttack(mob);
        }

        if (livingTarget != null) {
            MobUtil.disableShield(livingTarget, 100);
        }

        SoundEvent soundevent = SoundEvents.TRIDENT_HIT;
        if (target.hurt(damagesource, damage)) {
            if (livingTarget != null) {
                if (owner instanceof LivingEntity livingOwner) {
                    EnchantmentHelper.doPostHurtEffects(livingTarget, livingOwner);
                    EnchantmentHelper.doPostDamageEffects(livingOwner, livingTarget);
                    ItemHelper.setItemEffect(this.getItem(), livingTarget);
                }

                if (knockback > 0) {
                    double d0 = this.getX() - livingTarget.getX();
                    double d1 = this.getZ() - livingTarget.getZ();
                    livingTarget.knockback(knockback * 0.5F, d0, d1);
                }

                if (fireAspect > 0) {
                    livingTarget.setSecondsOnFire(fireAspect * 4);
                }
            }
        }

        this.playSound(soundevent, 1.0F, 1.0F);
        this.discard();
    }

    @Override
    protected void onHitBlock(net.minecraft.world.phys.BlockHitResult result) {
        super.onHitBlock(result);
        this.playSound(SoundEvents.TRIDENT_HIT_GROUND, 1.0F, 1.0F);
    }

    @Override
    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null) {
            if (pEntity == this.getOwner()) {
                return false;
            }
            if (this.getOwner() instanceof Mob mob && mob.getTarget() == pEntity) {
                return super.canHitEntity(pEntity);
            }
            if (MobUtil.areAllies(this.getOwner(), pEntity)) {
                return false;
            }
            if (pEntity instanceof IOwned owned0 && this.getOwner() instanceof IOwned owned1) {
                return !MobUtil.ownerStack(owned0, owned1);
            }
        }

        return super.canHitEntity(pEntity);
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    public boolean isInGround() {
        return this.inGround;
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
