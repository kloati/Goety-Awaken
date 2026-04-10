package com.k1sak1.goetyawaken.common.entities.ally.illager;

import com.Polarice3.Goety.common.entities.ally.illager.SpellcasterIllagerServant;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.common.research.ResearchList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnviokerServant extends SpellcasterIllagerServant {
    private static final EntityDataAccessor<Boolean> HAS_FAKE_APPOINTMENT = SynchedEntityData
            .defineId(EnviokerServant.class, EntityDataSerializers.BOOLEAN);
    @Nullable
    private Mob tramplerTarget;
    private int summonCool;
    private int tramplerCool;

    public EnviokerServant(EntityType<? extends EnviokerServant> p_32627_, Level p_32628_) {
        super(p_32627_, p_32628_);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new EnviokerCastingSpellGoal());
        this.goalSelector.addGoal(2, new AvoidTargetGoal<>(this, LivingEntity.class, 8.0F, 0.6D, 1.0D));
        this.goalSelector.addGoal(4, new EnviokerSummonSpellGoal());
        this.goalSelector.addGoal(5, new EnviokerAttackSpellGoal());
        this.goalSelector.addGoal(6, new EnviokerProjectileSpellGoal());
        this.goalSelector.addGoal(7, new EnviokerTramplerSpellGoal());
        this.miscGoal();
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.EnviokerHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.EnviokerArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.EnviokerDamage.get());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.summonCool = compound.getInt("SummonCool");
        this.tramplerCool = compound.getInt("TramplerCool");
        this.setHasFakeAppointment(compound.getBoolean("HasFakeAppointment"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("SummonCool", this.summonCool);
        compound.putInt("TramplerCool", this.tramplerCool);
        compound.putBoolean("HasFakeAppointment", this.hasFakeAppointment());
    }

    public boolean hasFakeAppointment() {
        return this.entityData.get(HAS_FAKE_APPOINTMENT);
    }

    public void setHasFakeAppointment(boolean hasFakeAppointment) {
        this.entityData.set(HAS_FAKE_APPOINTMENT, hasFakeAppointment);
    }

    @Override
    public int xpReward() {
        return 20;
    }

    public SoundEvent getCelebrateSound() {
        return SoundEvents.EVOKER_CELEBRATE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.EVOKER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.EVOKER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_32654_) {
        return SoundEvents.EVOKER_HURT;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.equipWeapons();
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    private void equipWeapons() {
        if (!this.level().isClientSide) {
            this.setItemSlot(EquipmentSlot.MAINHAND, this.createMainHandWeapon());
            this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.TOTEM_OF_UNDYING));
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_FAKE_APPOINTMENT, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.summonCool > 0) {
            --this.summonCool;
        }
        if (this.tramplerCool > 0) {
            --this.tramplerCool;
        }
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    @Override
    protected void pickUpItem(ItemEntity pItemEntity) {
        ItemStack itemstack = pItemEntity.getItem();
        if (itemstack.getItem() == Items.TOTEM_OF_UNDYING) {
            if (this.getInventory().canAddItem(itemstack)) {
                this.onItemPickup(pItemEntity);
                this.getInventory().addItem(itemstack);
                this.take(pItemEntity, itemstack.getCount());
                pItemEntity.discard();
            } else {
                super.pickUpItem(pItemEntity);
            }
        } else {
            super.pickUpItem(pItemEntity);
        }
    }

    private ItemStack createMainHandWeapon() {
        LivingEntity owner = this.getTrueOwner();
        boolean hasHighSouls = false;

        if (owner instanceof Player player) {
            hasHighSouls = SEHelper.getSoulAmountInt(player) >= 12500;
        }
        ItemStack weapon;
        if (hasHighSouls) {
            weapon = new ItemStack(Items.DIAMOND_SWORD);
        } else {
            weapon = new ItemStack(Items.IRON_SWORD);
        }
        this.enchantWeapon(weapon);

        return weapon;
    }

    private void enchantWeapon(ItemStack weapon) {
        Random random = new Random(this.getRandom().nextLong());
        if (random.nextFloat() < 0.3F) {
            int enchantLevel = random.nextInt(3) + 1;
            if (weapon.getItem() instanceof SwordItem) {
                switch (random.nextInt(4)) {
                    case 0:
                        weapon.enchant(Enchantments.SHARPNESS, enchantLevel);
                        break;
                    case 1:
                        weapon.enchant(Enchantments.SMITE, enchantLevel);
                        break;
                    case 2:
                        weapon.enchant(Enchantments.BANE_OF_ARTHROPODS, enchantLevel);
                        break;
                    case 3:
                        weapon.enchant(Enchantments.KNOCKBACK, enchantLevel);
                        break;
                }
            }
            if (random.nextFloat() < 0.1F) {
                weapon.enchant(Enchantments.UNBREAKING, random.nextInt(3) + 1);
            }
        }
    }

    class EnviokerCastingSpellGoal extends SpellcasterCastingSpellGoal {
        public void tick() {
            if (EnviokerServant.this.getTarget() != null) {
                EnviokerServant.this.getLookControl().setLookAt(EnviokerServant.this.getTarget(),
                        (float) EnviokerServant.this.getMaxHeadYRot(), (float) EnviokerServant.this.getMaxHeadXRot());
            }
        }
    }

    class EnviokerAttackSpellGoal extends SpellcasterUseSpellGoal {
        private final TargetingConditions vexCountTargeting = TargetingConditions.forNonCombat().range(16.0D)
                .ignoreLineOfSight().ignoreInvisibilityTesting();

        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            } else {
                return EnviokerServant.this.getTarget() != null
                        && EnviokerServant.this.hasLineOfSight(EnviokerServant.this.getTarget());
            }
        }

        protected int getCastingTime() {
            return 40;
        }

        protected int getCastingInterval() {
            return 100;
        }

        protected void performSpellCasting() {
            LivingEntity livingentity = EnviokerServant.this.getTarget();
            double d0 = Math.min(livingentity.getY(), EnviokerServant.this.getY());
            double d1 = Math.max(livingentity.getY(), EnviokerServant.this.getY()) + 1.0D;
            float f = (float) Mth.atan2(livingentity.getZ() - EnviokerServant.this.getZ(),
                    livingentity.getX() - EnviokerServant.this.getX());
            if (EnviokerServant.this.distanceToSqr(livingentity) < 9.0D) {
                for (int i = 0; i < 5; ++i) {
                    float f1 = f + (float) i * (float) Math.PI * 0.4F;
                    this.createSpellEntity(EnviokerServant.this.getX() + (double) Mth.cos(f1) * 1.5D,
                            EnviokerServant.this.getZ() + (double) Mth.sin(f1) * 1.5D, d0, d1, f1, 0);
                }

                for (int k = 0; k < 8; ++k) {
                    float f2 = f + (float) k * (float) Math.PI * 2.0F / 8.0F + 1.2566371F;
                    this.createSpellEntity(EnviokerServant.this.getX() + (double) Mth.cos(f2) * 2.5D,
                            EnviokerServant.this.getZ() + (double) Mth.sin(f2) * 2.5D, d0, d1, f2, 3);
                }

                for (int k = 0; k < 11; ++k) {
                    float f2 = f + (float) k * (float) Math.PI * 4.0F / 16.0F + 2.5133462F;
                    this.createSpellEntity(EnviokerServant.this.getX() + (double) Mth.cos(f2) * 3.5D,
                            EnviokerServant.this.getZ() + (double) Mth.sin(f2) * 3.5D, d0, d1, f2, 6);
                }
            } else {
                float radius = 0.2F;
                for (int l = 0; l < 32; ++l) {
                    double d2 = 1.25D * (double) (l + 1);
                    float fleft = f + radius;
                    float fright = f - radius;
                    this.createSpellEntity(EnviokerServant.this.getX() + (double) Mth.cos(f) * d2,
                            EnviokerServant.this.getZ() + (double) Mth.sin(f) * d2, d0, d1, f, l);
                    this.createSpellEntity(EnviokerServant.this.getX() + (double) Mth.cos(fleft) * d2,
                            EnviokerServant.this.getZ() + (double) Mth.sin(fleft) * d2, d0, d1, fleft, l);
                    this.createSpellEntity(EnviokerServant.this.getX() + (double) Mth.cos(fright) * d2,
                            EnviokerServant.this.getZ() + (double) Mth.sin(fright) * d2, d0, d1, fright, l);
                }
            }

        }

        private void createSpellEntity(double p_190876_1_, double p_190876_3_, double p_190876_5_, double p_190876_7_,
                float p_190876_9_, int p_190876_10_) {
            BlockPos blockpos = BlockPos.containing(p_190876_1_, p_190876_7_, p_190876_3_);
            boolean flag = false;
            double d0 = 0.0D;

            do {
                BlockPos blockpos1 = blockpos.below();
                net.minecraft.world.level.block.state.BlockState blockstate = EnviokerServant.this.level()
                        .getBlockState(blockpos1);
                if (blockstate.isFaceSturdy(EnviokerServant.this.level(), blockpos1, net.minecraft.core.Direction.UP)) {
                    if (!EnviokerServant.this.level().isEmptyBlock(blockpos)) {
                        net.minecraft.world.level.block.state.BlockState blockstate1 = EnviokerServant.this.level()
                                .getBlockState(blockpos);
                        VoxelShape voxelshape = blockstate1.getCollisionShape(EnviokerServant.this.level(), blockpos);
                        if (!voxelshape.isEmpty()) {
                            d0 = voxelshape.max(net.minecraft.core.Direction.Axis.Y);
                        }
                    }

                    flag = true;
                    break;
                }

                blockpos = blockpos.below();
            } while (blockpos.getY() >= Mth.floor(p_190876_5_) - 1);

            if (flag) {
                EnviokerServant.this.level()
                        .addFreshEntity(new com.Polarice3.Goety.common.entities.projectiles.Fangs(
                                EnviokerServant.this.level(), p_190876_1_, (double) blockpos.getY() + d0, p_190876_3_,
                                p_190876_9_, p_190876_10_, EnviokerServant.this));
            }

        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_ATTACK;
        }

        protected IllagerServantSpell getSpell() {
            return IllagerServantSpell.FANGS;
        }
    }

    class EnviokerSummonSpellGoal extends SpellcasterUseSpellGoal {
        private final TargetingConditions vexCountTargeting = TargetingConditions.forNonCombat().range(16.0D)
                .ignoreLineOfSight().ignoreInvisibilityTesting();

        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            } else {
                int i = EnviokerServant.this.level().getNearbyEntities(TormentorServant.class, this.vexCountTargeting,
                        EnviokerServant.this, EnviokerServant.this.getBoundingBox().inflate(16.0D)).size();
                return EnviokerServant.this.getTarget() != null
                        && i < 1 && EnviokerServant.this.summonCool <= 0
                        && EnviokerServant.this.hasLineOfSight(EnviokerServant.this.getTarget());
            }
        }

        protected int getCastingTime() {
            return 100;
        }

        protected int getCastingInterval() {
            return 340;
        }

        protected void performSpellCasting() {
            ServerLevel serverlevel = (ServerLevel) EnviokerServant.this.level();

            BlockPos blockpos = EnviokerServant.this.blockPosition().offset(-2 + EnviokerServant.this.random.nextInt(5),
                    1, -2 + EnviokerServant.this.random.nextInt(5));
            TormentorServant tormentorServant = ModEntityType.TORMENTOR_SERVANT.get()
                    .create(EnviokerServant.this.level());
            if (tormentorServant != null) {
                tormentorServant.moveTo(blockpos, 0.0F, 0.0F);
                tormentorServant.finalizeSpawn(serverlevel,
                        EnviokerServant.this.level().getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED,
                        (SpawnGroupData) null, (CompoundTag) null);
                tormentorServant.setTrueOwner(EnviokerServant.this);
                tormentorServant.setBoundOrigin(blockpos);
                tormentorServant.setLimitedLife(20 * (30 + EnviokerServant.this.random.nextInt(90)));
                serverlevel.addFreshEntityWithPassengers(tormentorServant);
            }
            EnviokerServant.this.summonCool = MathHelper.secondsToTicks(30);
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        protected IllagerServantSpell getSpell() {
            return IllagerServantSpell.SUMMON_VEX;
        }
    }

    class EnviokerProjectileSpellGoal extends SpellcasterUseSpellGoal {
        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            } else {
                return EnviokerServant.this.getTarget() != null
                        && EnviokerServant.this.level().getDifficulty() != net.minecraft.world.Difficulty.PEACEFUL
                        && EnviokerServant.this.getMainHandItem()
                                .getItem() instanceof net.minecraft.world.item.SwordItem
                        && EnviokerServant.this.hasLineOfSight(EnviokerServant.this.getTarget());
            }
        }

        protected int getCastingTime() {
            return 20;
        }

        protected int getCastingInterval() {
            return 20;
        }

        protected void performSpellCasting() {
            LivingEntity livingentity = EnviokerServant.this.getTarget();
            if (livingentity != null) {
                if (EnviokerServant.this.getSensing().hasLineOfSight(livingentity)) {
                    com.Polarice3.Goety.common.entities.projectiles.SwordProjectile swordProjectile = new com.Polarice3.Goety.common.entities.projectiles.SwordProjectile(
                            EnviokerServant.this, EnviokerServant.this.level(), EnviokerServant.this.getMainHandItem());
                    double d0 = livingentity.getX() - EnviokerServant.this.getX();
                    double d1 = livingentity.getY(0.3333333333333333D) - swordProjectile.getY();
                    double d2 = livingentity.getZ() - EnviokerServant.this.getZ();
                    double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
                    swordProjectile.pickup = net.minecraft.world.entity.projectile.AbstractArrow.Pickup.CREATIVE_ONLY;
                    swordProjectile.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, 1.0F);
                    EnviokerServant.this.level().addFreshEntity(swordProjectile);
                    if (!EnviokerServant.this.isSilent()) {
                        EnviokerServant.this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F);
                    }
                }
            }

        }

        protected SoundEvent getSpellPrepareSound() {
            return null;
        }

        protected IllagerServantSpell getSpell() {
            return IllagerServantSpell.WOLOLO;
        }
    }

    class EnviokerTramplerSpellGoal extends SpellcasterUseSpellGoal {
        private final TargetingConditions tramplerTargeting = TargetingConditions.forNonCombat().range(16.0D)
                .selector((p_32710_) -> {
                    return p_32710_ instanceof net.minecraft.world.entity.monster.Pillager
                            || p_32710_ instanceof com.Polarice3.Goety.common.entities.ally.illager.PillagerServant;
                });

        public boolean canUse() {
            if (!EnviokerServant.this.isLeader()) {
                return false;
            } else if (EnviokerServant.this.getTarget() != null) {
                return false;
            } else if (EnviokerServant.this.isCastingSpell()) {
                return false;
            } else if (EnviokerServant.this.tickCount < this.nextAttackTickCount) {
                return false;
            } else if (EnviokerServant.this.tramplerCool > 0) {
                return false;
            } else if (EnviokerServant.this.getNearbyCompanions().isEmpty()) {
                return false;
            } else if (this.otherEnviokers().size() < 2) {
                return false;
            } else {
                if (EnviokerServant.this.getTrueOwner() instanceof Player player) {
                    if (!SEHelper.hasResearch(player, ResearchList.RAVAGING)) {
                        return false;
                    } else if (SEHelper.getGrudgeEntityTypes(player).contains(EntityType.PILLAGER)) {
                        List<Mob> list = EnviokerServant.this.level().getNearbyEntities(Mob.class,
                                this.tramplerTargeting, EnviokerServant.this,
                                EnviokerServant.this.getBoundingBox().inflate(16.0D, 4.0D, 16.0D));
                        if (list.isEmpty()) {
                            return false;
                        } else {
                            EnviokerServant.this
                                    .setTramplerTarget(list.get(EnviokerServant.this.random.nextInt(list.size())));
                            return true;
                        }
                    } else if (EnviokerServant.this.getCommandPosEntity() != null
                            && (EnviokerServant.this
                                    .getCommandPosEntity() instanceof net.minecraft.world.entity.monster.Pillager
                                    || EnviokerServant.this
                                            .getCommandPosEntity() instanceof com.Polarice3.Goety.common.entities.ally.illager.PillagerServant)
                            && EnviokerServant.this.getCommandPosEntity().distanceTo(EnviokerServant.this) <= 16.0D) {
                        EnviokerServant.this.setTramplerTarget((Mob) EnviokerServant.this.getCommandPosEntity());
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }

        public boolean canContinueToUse() {
            return EnviokerServant.this.getTramplerTarget() != null
                    && EnviokerServant.this.getTramplerTarget().isAlive()
                    && this.otherEnviokers().size() >= 2
                    && EnviokerServant.this.getTarget() == null
                    && this.attackWarmupDelay > 0;
        }

        @Override
        public void tick() {
            super.tick();
            Mob victim = EnviokerServant.this.getTramplerTarget();
            if (victim != null && victim.isAlive()) {
                MobUtil.instaLook(EnviokerServant.this, victim);
                com.Polarice3.Goety.utils.MiscCapHelper.setShakeTime(victim, 20);
                victim.setLastHurtByMob(EnviokerServant.this);
                victim.getNavigation().stop();
                victim.getMoveControl().strafe(0.0F, 0.0F);
                if (victim.tickCount % 20 == 0) {
                    if (victim.level() instanceof ServerLevel serverLevel) {
                        com.Polarice3.Goety.utils.ServerParticleUtil.addParticlesAroundSelf(serverLevel,
                                net.minecraft.core.particles.ParticleTypes.ENCHANT, victim);
                    }
                }
                Vec3 offset = new Vec3(2, 0, 0);
                Vec3 at = this.groundOf(victim.position().add(offset));
                EnviokerServant.this.getNavigation().moveTo(at.x, at.y, at.z, 0.75F);
                for (int i = 0; i < this.otherEnviokers().size(); ++i) {
                    EnviokerServant evokerServant = this.otherEnviokers().get(i);
                    float f = (float) (i + 1) / (this.otherEnviokers().size() + 1);
                    Vec3 offset2 = new Vec3(2, 0, 0).yRot(f * ((float) Math.PI * 2F));
                    Vec3 at2 = this.groundOf(victim.position().add(offset2));
                    evokerServant.getNavigation().moveTo(at2.x, at2.y, at2.z, 0.75F);
                    MobUtil.instaLook(evokerServant, victim);
                    evokerServant.setIsCastingSpell(IllagerServantSpell.RAVAGING);
                    evokerServant.spellCastingTickCount = 20;
                }
            }
        }

        public void stop() {
            super.stop();
            this.attackWarmupDelay = 0;
            EnviokerServant.this.spellCastingTickCount = 0;
            EnviokerServant.this.setIsCastingSpell(IllagerServantSpell.NONE);
            EnviokerServant.this.setTramplerTarget(null);
            EnviokerServant.this.setCommandPosEntity(null);
        }

        private Vec3 groundOf(Vec3 in) {
            BlockPos origin = BlockPos.containing(in);
            BlockPos.MutableBlockPos blockPos = origin.mutable();
            while (!EnviokerServant.this.level().isEmptyBlock(blockPos)
                    && blockPos.getY() < EnviokerServant.this.level().getMaxBuildHeight()) {
                blockPos.move(0, 1, 0);
            }
            while (EnviokerServant.this.level().isEmptyBlock(blockPos.below())
                    && blockPos.getY() > EnviokerServant.this.level().getMinBuildHeight()) {
                blockPos.move(0, -1, 0);
            }
            return new Vec3(in.x, blockPos.getY(), in.z);
        }

        protected void performSpellCasting() {
            Mob victim = EnviokerServant.this.getTramplerTarget();
            if (victim != null && victim.isAlive()) {
                Player player = null;
                if (EnviokerServant.this.getTrueOwner() instanceof Player player1) {
                    player = player1;
                }
                Entity entity = MobUtil.convertTo(victim,
                        com.Polarice3.Goety.common.entities.ModEntityType.TRAMPLER_SERVANT.get(), true, player);
                if (entity instanceof Mob mob) {
                    mob.setYHeadRot(victim.getYHeadRot());
                    mob.setYRot(victim.getYRot());
                    mob.spawnAnim();
                }
                EnviokerServant.this.tramplerCool = MathHelper
                        .secondsToTicks(com.Polarice3.Goety.config.MobsConfig.EvokerServantRavagedCooldown.get());
                for (EnviokerServant evokerServant : this.otherEnviokers()) {
                    evokerServant.tramplerCool = MathHelper
                            .secondsToTicks(com.Polarice3.Goety.config.MobsConfig.EvokerServantRavagedCooldown.get());
                }
            }
        }

        public List<EnviokerServant> otherEnviokers() {
            List<EnviokerServant> servants = new ArrayList<>();
            for (com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant raider : EnviokerServant.this
                    .getNearbyCompanions()) {
                if (raider instanceof EnviokerServant evokerServant) {
                    if (evokerServant.tramplerCool <= 0
                            && evokerServant.getTarget() == null
                            && (!evokerServant.isCastingSpell()
                                    || evokerServant.getCurrentSpell() == IllagerServantSpell.RAVAGING)) {
                        servants.add(evokerServant);
                    }
                }
            }
            return servants;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        protected int getCastWarmupTime() {
            return MathHelper.secondsToTicks(30);
        }

        protected int getCastingTime() {
            return MathHelper.secondsToTicks(31);
        }

        protected int getCastingInterval() {
            return 140;
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_WOLOLO;
        }

        protected IllagerServantSpell getSpell() {
            return IllagerServantSpell.RAVAGING;
        }
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Item item = itemstack.getItem();
        ItemStack itemstack2 = this.getMainHandItem();
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (!(pPlayer.getOffhandItem().getItem() instanceof com.Polarice3.Goety.api.items.magic.IWand)) {
                if (item instanceof SwordItem || itemstack.is(ItemTags.SWORDS)) {
                    this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                    this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.copy());
                    this.dropEquipment(EquipmentSlot.MAINHAND, itemstack2);
                    this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        this.level().addParticle(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER,
                                this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
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

    void setTramplerTarget(@Nullable Mob p_32635_) {
        this.tramplerTarget = p_32635_;
    }

    @Nullable
    Mob getTramplerTarget() {
        return this.tramplerTarget;
    }
}