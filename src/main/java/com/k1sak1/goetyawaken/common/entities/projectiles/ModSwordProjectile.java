package com.k1sak1.goetyawaken.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.Util;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import com.k1sak1.goetyawaken.client.renderer.trail.TrailPosition;

import java.util.ArrayList;
import java.util.List;

public class ModSwordProjectile extends AbstractArrow implements ItemSupplier {
    private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData
            .defineId(ModSwordProjectile.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Boolean> DATA_HAS_TRAIL = SynchedEntityData.defineId(
            ModSwordProjectile.class,
            EntityDataSerializers.BOOLEAN);
    private static final int MAX_TRAILS = 48;
    private int groundTimer = 0;

    @OnlyIn(Dist.CLIENT)
    private List<TrailPosition> trailPositions;

    @OnlyIn(Dist.CLIENT)
    private List<TrailPosition> getTrailPositions() {
        if (trailPositions == null) {
            trailPositions = new ArrayList<>();
        }
        return trailPositions;
    }

    @OnlyIn(Dist.CLIENT)
    public List<TrailPosition> getPublicTrailPoints() {
        return getTrailPositions();
    }

    public ModSwordProjectile(EntityType<? extends AbstractArrow> p_i48546_1_, Level p_i48546_2_) {
        super(p_i48546_1_, p_i48546_2_);
        if (!p_i48546_2_.isClientSide) {
            this.setHasTrail(true);
        }
    }

    public ModSwordProjectile(double p_i48547_2_, double p_i48547_4_, double p_i48547_6_, Level p_i48547_8_) {
        super(com.k1sak1.goetyawaken.common.entities.ModEntityType.MOD_SWORD_PROJECTILE.get(), p_i48547_2_, p_i48547_4_,
                p_i48547_6_, p_i48547_8_);
        if (!p_i48547_8_.isClientSide) {
            this.setHasTrail(true);
        }
    }

    public ModSwordProjectile(LivingEntity p_i48548_2_, Level p_i48548_3_, ItemStack p_i48790_3_) {
        super(com.k1sak1.goetyawaken.common.entities.ModEntityType.MOD_SWORD_PROJECTILE.get(), p_i48548_2_.getX(),
                p_i48548_2_.getY(0.5F), p_i48548_2_.getZ(),
                p_i48548_3_);
        this.setOwner(p_i48548_2_);
        this.setItem(p_i48790_3_.copy());
        if (!p_i48548_3_.isClientSide) {
            this.setHasTrail(true);
        }
    }

    public void setItem(ItemStack pStack) {
        if (pStack.getItem() != this.getDefaultItem() || pStack.hasTag()) {
            this.getEntityData().set(DATA_ITEM_STACK, Util.make(pStack.copy(), (p_213883_0_) -> {
                p_213883_0_.setCount(1);
            }));
        }

    }

    protected Item getDefaultItem() {
        return Items.IRON_SWORD;
    }

    protected ItemStack getItemRaw() {
        return this.getEntityData().get(DATA_ITEM_STACK);
    }

    public ItemStack getItem() {
        ItemStack itemstack = this.getItemRaw();
        return itemstack.isEmpty() ? new ItemStack(this.getDefaultItem()) : itemstack;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_ITEM_STACK, ItemStack.EMPTY);
        this.entityData.define(DATA_HAS_TRAIL, false);
    }

    public boolean hasTrail() {
        return this.entityData.get(DATA_HAS_TRAIL);
    }

    public void setHasTrail(boolean hasTrail) {
        this.entityData.set(DATA_HAS_TRAIL, hasTrail);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        ItemStack itemstack = this.getItemRaw();
        if (!itemstack.isEmpty()) {
            pCompound.put("Item", itemstack.save(new CompoundTag()));
        }
        pCompound.putInt("GroundTimer", this.groundTimer);
        pCompound.putBoolean("HasTrail", this.hasTrail());

    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        ItemStack itemstack = ItemStack.of(pCompound.getCompound("Item"));
        this.setItem(itemstack);
        if (pCompound.contains("GroundTimer")) {
            this.groundTimer = pCompound.getInt("GroundTimer");
        }
        if (pCompound.contains("HasTrail")) {
            this.setHasTrail(pCompound.getBoolean("HasTrail"));
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (!this.hasTrail()) {
                this.setHasTrail(true);
            }
        } else {
            this.handleClientTick();
        }
        if (!this.inGround && !this.level().isClientSide) {
            Vec3 vector3d = this.getDeltaMovement();
            double d3 = vector3d.x;
            double d4 = vector3d.y;
            double d0 = vector3d.z;
            double d5 = this.getX() + d3;
            double d1 = this.getY() + d4;
            double d2 = this.getZ() + d0;
            this.level().addParticle(ParticleTypes.ENCHANT, d5 - d3 * 0.25D, d1 - d4 * 0.25D, d2 - d0 * 0.25D, d3, d4,
                    d0);
        }
        if (this.inGround) {
            groundTimer++;
            if (groundTimer >= 20) {
                this.discard();
            }
        } else {
            groundTimer = 0;
        }

    }

    @OnlyIn(Dist.CLIENT)
    private void handleClientTick() {
        if (this.hasTrail()) {
            this.initializeTrail();
            this.updateTrail();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void initializeTrail() {
        // 只在第一帧初始化一个点，避免所有点都在同一位置
        if (this.hasTrail() && this.getTrailPositions().isEmpty()) {
            this.getTrailPositions().add(new TrailPosition(this.position(), 0));
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void updateTrail() {
        // 逐步填充轨迹点直到达到最大数量
        if (this.getTrailPositions().size() < MAX_TRAILS) {
            this.getTrailPositions().add(new TrailPosition(this.position(), 0));
        }
    }

    protected void onHitEntity(EntityHitResult pResult) {
        Entity target = pResult.getEntity();
        float f = 6.0F;
        float f1 = 0.0F;
        int i = 0;
        Entity owner = this.getOwner();
        if (this.getItem().getItem() instanceof SwordItem swordItem) {
            f = swordItem.getDamage();
        }
        if (!this.getItem().isEmpty()) {
            f1 += this.getItem().getEnchantmentLevel(Enchantments.KNOCKBACK);
            i = this.getItem().getEnchantmentLevel(Enchantments.FIRE_ASPECT);
        }
        if (target instanceof LivingEntity livingentity) {
            f += EnchantmentHelper.getDamageBonus(this.getItem(), livingentity.getMobType());
        }
        DamageSource damagesource = ModDamageSource.sword(this, owner == null ? this : owner);
        if (owner instanceof Player player) {
            damagesource = this.damageSources().playerAttack(player);
        } else if (owner instanceof Mob mob) {
            damagesource = this.damageSources().mobAttack(mob);
        }
        SoundEvent soundevent = SoundEvents.TRIDENT_HIT;
        if (target.hurt(damagesource, f)) {
            if (target instanceof LivingEntity livingTarget) {
                if (owner instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(livingTarget, owner);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity) owner, livingTarget);
                    ItemHelper.setItemEffect(this.getItem(), livingTarget);
                }
                if (f1 > 0) {
                    double d0 = this.getX() - livingTarget.getX();
                    double d1 = this.getZ() - livingTarget.getZ();
                    livingTarget.knockback(f1 * 0.5F, d0, d1);
                }
                if (i > 0) {
                    livingTarget.setSecondsOnFire(i * 4);
                }

                this.doPostHurtEffects(livingTarget);
            }
        }

        float f2 = 1.0F;
        this.playSound(soundevent, f2, 1.0F);
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null) {
            if (this.getOwner() instanceof Mob mob && mob.getTarget() == pEntity) {
                return super.canHitEntity(pEntity);
            } else {
                if (MobUtil.areAllies(this.getOwner(), pEntity)) {
                    return false;
                }
                if (this.getOwner() instanceof Enemy && pEntity instanceof Enemy) {
                    return false;
                }
                if (pEntity instanceof Projectile projectile && projectile.getOwner() == this.getOwner()) {
                    return false;
                }
                if (pEntity instanceof IOwned owned0 && this.getOwner() instanceof IOwned owned1) {
                    return !MobUtil.ownerStack(owned0, owned1);
                }
            }
        }
        return super.canHitEntity(pEntity);
    }

    protected boolean tryPickup(Player p_150196_) {
        return p_150196_.getAbilities().instabuild;
    }

    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}