package com.k1sak1.goetyawaken.common.entities.projectiles;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.items.ModItems;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import com.google.common.collect.Maps;
import com.Polarice3.Goety.utils.BlockFinder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FrostScytheSlash extends AbstractHurtingProjectile {
   private static final EntityDataAccessor<Integer> DATA_TYPE_ID;
   public static final Map<Integer, ResourceLocation> TEXTURE_BY_TYPE;
   private ItemStack weapon;
   private float damage;
   private int lifespan;
   private int totalLife;

   public FrostScytheSlash(EntityType<? extends AbstractHurtingProjectile> entityType, Level world) {
      super(entityType, world);
      this.weapon = new ItemStack((ItemLike) ModItems.FROST_SCYTHE.get());
      this.damage = 8.5F;
      this.lifespan = 0;
      this.totalLife = 60;
   }

   public FrostScytheSlash(ItemStack itemStack, Level world, double x, double y, double z, double xSpeed,
         double ySpeed, double zSpeed) {
      super(ModEntityType.FROST_SCYTHE_SLASH.get(), x, y, z, xSpeed, ySpeed, zSpeed, world);
      this.weapon = new ItemStack((ItemLike) ModItems.FROST_SCYTHE.get());
      this.weapon = itemStack;
   }

   public FrostScytheSlash(Level world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
      super(ModEntityType.FROST_SCYTHE_SLASH.get(), x, y, z, xSpeed, ySpeed, zSpeed, world);
      this.weapon = new ItemStack((ItemLike) ModItems.FROST_SCYTHE.get());
   }

   public ResourceLocation getResourceLocation() {
      return (ResourceLocation) TEXTURE_BY_TYPE.getOrDefault(this.getAnimation(),
            (ResourceLocation) TEXTURE_BY_TYPE.get(0));
   }

   public int getAnimation() {
      return (Integer) this.entityData.get(DATA_TYPE_ID);
   }

   public void setAnimation(int pType) {
      this.entityData.set(DATA_TYPE_ID, pType);
   }

   public float getDamage() {
      return this.damage;
   }

   public void setDamage(float damage) {
      this.damage = damage;
   }

   public int getTotalLife() {
      return this.totalLife;
   }

   public void setTotalLife(int totalLife) {
      this.totalLife = totalLife;
   }

   public int getLifespan() {
      return this.lifespan;
   }

   public void setLifespan(int lifespan) {
      this.lifespan = lifespan;
   }

   protected void defineSynchedData() {
      this.entityData.define(DATA_TYPE_ID, 0);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setAnimation(compound.getInt("Animation"));
      if (compound.contains("Damage")) {
         this.setLifespan(compound.getInt("Damage"));
      }

      if (compound.contains("Lifespan")) {
         this.setLifespan(compound.getInt("Lifespan"));
      }

      if (compound.contains("TotalLife")) {
         this.setTotalLife(compound.getInt("TotalLife"));
      }

   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("Animation", this.getAnimation());
      compound.putFloat("Damage", this.getDamage());
      compound.putInt("Lifespan", this.getLifespan());
      compound.putInt("TotalLife", this.getTotalLife());
   }

   public void tick() {
      super.tick();
      if (this.lifespan < this.getTotalLife()) {
         ++this.lifespan;
      } else {
         this.discard();
      }

      if (this.getAnimation() < 7) {
         this.setAnimation(this.getAnimation() + 1);
      } else {
         this.setAnimation(0);
      }

      Iterator var2;
      if ((Boolean) ItemConfig.ScytheSlashBreaks.get()) {
         AABB aabb = this.getBoundingBox().inflate(0.2);
         var2 = BlockPos.betweenClosed(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ),
               Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ)).iterator();

         label97: while (true) {
            BlockPos blockpos;
            BlockState blockstate;
            do {
               if (!var2.hasNext()) {
                  break label97;
               }

               blockpos = (BlockPos) var2.next();
               blockstate = this.level().getBlockState(blockpos);
            } while (!blockstate.is(BlockTags.MINEABLE_WITH_HOE) && !BlockFinder.isScytheBreak(blockstate));

            ItemStack itemStack = this.weapon;
            if (this.weapon == null || this.weapon.isEmpty()) {
               itemStack = new ItemStack((ItemLike) ModItems.FROST_SCYTHE.get());
            }

            BlockFinder.breakBlock(this.level(), blockpos, itemStack, this);
         }
      }

      if (!this.level().isClientSide) {
         List<Entity> targets = new ArrayList();
         var2 = this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(0.5)).iterator();

         Entity entity;
         while (var2.hasNext()) {
            entity = (Entity) var2.next();
            if (this.getOwner() != null) {
               if (entity != this.getOwner() && !MobUtil.areAllies(entity, this.getOwner())
                     && entity != this.getOwner().getVehicle()) {
                  targets.add(entity);
               }
            } else {
               targets.add(entity);
            }
         }

         if (!targets.isEmpty()) {
            var2 = targets.iterator();

            while (var2.hasNext()) {
               entity = (Entity) var2.next();
               if (MobUtil.validEntity(entity)) {
                  float f = this.getDamage();
                  if (this.getOwner() != null) {
                     if (entity instanceof LivingEntity) {
                        f += EnchantmentHelper.getDamageBonus(this.weapon, ((LivingEntity) entity).getMobType());
                     }

                     Entity var6 = this.getOwner();
                     if (var6 instanceof Player) {
                        Player player = (Player) var6;
                        boolean attack = entity.hurt(entity.damageSources().playerAttack(player), f);
                        if (entity instanceof EnderDragon) {
                           EnderDragon enderDragonEntity = (EnderDragon) entity;
                           attack = enderDragonEntity.hurt(entity.damageSources().playerAttack(player), f);
                        }

                        if (attack && entity instanceof LivingEntity) {
                           int enchantment = this.weapon
                                 .getEnchantmentLevel((Enchantment) ModEnchantments.SOUL_EATER.get());
                           int soulEater = Mth.clamp(enchantment + 1, 1, 10);
                           SEHelper.increaseSouls(player, (Integer) ItemConfig.DarkScytheSouls.get() * soulEater);
                        }
                     } else {
                        Entity var17 = this.getOwner();
                        DamageSource var10000;
                        if (var17 instanceof LivingEntity) {
                           LivingEntity livingEntity = (LivingEntity) var17;
                           var10000 = entity.damageSources().mobAttack(livingEntity);
                        } else {
                           var10000 = entity.damageSources().thrown(this, this);
                        }

                        DamageSource damageSource = var10000;
                        entity.hurt(damageSource, f);
                     }
                  } else {
                     entity.hurt(entity.damageSources().thrown(this, this), f);
                  }
               }
            }
         }
      }

   }

   @Override
   protected void onHitBlock(BlockHitResult p_230299_1_) {
      super.onHitBlock(p_230299_1_);
      this.discard();
   }

   @Override
   public boolean isOnFire() {
      return false;
   }

   @Override
   public boolean isPickable() {
      return false;
   }

   @Override
   public boolean hurt(DamageSource pSource, float pAmount) {
      return false;
   }

   @Override
   protected ParticleOptions getTrailParticle() {
      return ParticleTypes.SNOWFLAKE;
   }

   @Override
   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   static {
      DATA_TYPE_ID = SynchedEntityData.defineId(FrostScytheSlash.class, EntityDataSerializers.INT);
      TEXTURE_BY_TYPE = (Map) Util.make(Maps.newHashMap(), (map) -> {
         map.put(0, new ResourceLocation(GoetyAwaken.MODID,
               "textures/entity/projectiles/frostscytheslash/scythe_0.png"));
         map.put(1, new ResourceLocation(GoetyAwaken.MODID,
               "textures/entity/projectiles/frostscytheslash/scythe_1.png"));
         map.put(2, new ResourceLocation(GoetyAwaken.MODID,
               "textures/entity/projectiles/frostscytheslash/scythe_2.png"));
         map.put(3, new ResourceLocation(GoetyAwaken.MODID,
               "textures/entity/projectiles/frostscytheslash/scythe_3.png"));
         map.put(4, new ResourceLocation(GoetyAwaken.MODID,
               "textures/entity/projectiles/frostscytheslash/scythe_4.png"));
         map.put(5, new ResourceLocation(GoetyAwaken.MODID,
               "textures/entity/projectiles/frostscytheslash/scythe_5.png"));
         map.put(6, new ResourceLocation(GoetyAwaken.MODID,
               "textures/entity/projectiles/frostscytheslash/scythe_6.png"));
         map.put(7, new ResourceLocation(GoetyAwaken.MODID,
               "textures/entity/projectiles/frostscytheslash/scythe_7.png"));
      });
   }
}