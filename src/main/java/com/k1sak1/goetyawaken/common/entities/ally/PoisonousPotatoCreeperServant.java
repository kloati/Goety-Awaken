package com.k1sak1.goetyawaken.common.entities.ally;

import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.k1sak1.goetyawaken.init.ModEffects;
import com.k1sak1.goetyawaken.init.ModSounds;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.VerticalCircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.SphereExplodeParticleOption;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

import com.k1sak1.goetyawaken.common.entities.ModEntityType;

public class PoisonousPotatoCreeperServant extends AbstractCreeperServant {
    private static final EntityDataAccessor<Integer> DATA_SWELL_DIR = SynchedEntityData.defineId(
            PoisonousPotatoCreeperServant.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_POWERED = SynchedEntityData.defineId(
            PoisonousPotatoCreeperServant.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_IGNITED = SynchedEntityData.defineId(
            PoisonousPotatoCreeperServant.class,
            EntityDataSerializers.BOOLEAN);

    public PoisonousPotatoCreeperServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.explosionRadius = 3;
    }

    @Override
    protected EntityDataAccessor<Integer> getSwellDirAccessor() {
        return DATA_SWELL_DIR;
    }

    @Override
    protected EntityDataAccessor<Boolean> getPoweredAccessor() {
        return DATA_IS_POWERED;
    }

    @Override
    protected EntityDataAccessor<Boolean> getIgnitedAccessor() {
        return DATA_IS_IGNITED;
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.PoisonousPotatoCreeperServantHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ARMOR, AttributesConfig.PoisonousPotatoCreeperServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.PoisonousPotatoCreeperServantArmorToughness.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.PoisonousPotatoCreeperServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED), 0.25D);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR),
                AttributesConfig.PoisonousPotatoCreeperServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.PoisonousPotatoCreeperServantArmorToughness.get());
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        if (effectInstance.getEffect() == MobEffects.POISON) {
            return false;
        }
        return super.canBeAffected(effectInstance);
    }

    @Override
    protected void applyEffectToEntity(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(ModEffects.POTENT_VENOM.get(),
                MathHelper.secondsToTicks(10), 1, false, false));
    }

    @Override
    protected void spawnExplosionParticles(ServerLevel serverLevel, float explosionRadius) {
        ColorUtil colorUtil = new ColorUtil(0xADFF2F);
        Vec3 vec3 = this.position();
        serverLevel.sendParticles(
                new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(),
                        explosionRadius * 2, 1),
                vec3.x, vec3.y, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        serverLevel.sendParticles(
                new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(),
                        explosionRadius, 1),
                vec3.x, vec3.y, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        serverLevel.sendParticles(
                new VerticalCircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(),
                        explosionRadius * 2, 1),
                vec3.x, vec3.y, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        serverLevel.sendParticles(
                new SphereExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(),
                        explosionRadius * 2.0F, 1),
                vec3.x, vec3.y + 0.5D, vec3.z, 1, 0, 0, 0, 0);

        for (int i = 0; i < 100; i++) {
            double offsetX = (this.random.nextDouble() - 0.5D) * explosionRadius * 2;
            double offsetY = (this.random.nextDouble() - 0.5D) * explosionRadius * 2;
            double offsetZ = (this.random.nextDouble() - 0.5D) * explosionRadius * 2;
            serverLevel.sendParticles(ModParticleTypes.CULT_SPELL.get(),
                    this.getX() + offsetX, this.getY() + 0.5D + offsetY, this.getZ() + offsetZ,
                    1, 0, 0, 0, 0);
        }
    }

    @Override
    public boolean killedEntity(ServerLevel world, LivingEntity killedEntity) {
        boolean flag = super.killedEntity(world, killedEntity);
        if (killedEntity instanceof Creeper creeperEntity) {
            if (ForgeEventFactory.canLivingConvert(creeperEntity,
                    ModEntityType.POISONOUS_POTATO_CREEPER_SERVANT.get(), (timer) -> {
                    })) {
                PoisonousPotatoCreeperServant servant = creeperEntity.convertTo(
                        ModEntityType.POISONOUS_POTATO_CREEPER_SERVANT.get(), true);
                if (servant != null) {
                    if (this.getTrueOwner() != null) {
                        servant.setTrueOwner(this.getTrueOwner());
                    }
                    servant.finalizeSpawn(world, world.getCurrentDifficultyAt(servant.blockPosition()),
                            MobSpawnType.CONVERSION, null, null);
                    servant.setLimitedLife(10 * (15 + world.random.nextInt(45)));
                    if (this.isHostile()) {
                        servant.setHostile(true);
                    }
                    ForgeEventFactory.onLivingConvert(creeperEntity, servant);
                    this.playSound(ModSounds.POISONOUS_POTATO_ZOMBIE_INFECT.get(), 1.0F, 1.0F);
                    if (!servant.isSilent()) {
                        world.levelEvent(null, 1026, servant.blockPosition(), 0);
                    }
                }
            }
        }
        return flag;
    }
}
