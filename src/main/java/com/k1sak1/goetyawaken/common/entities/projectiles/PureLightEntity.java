package com.k1sak1.goetyawaken.common.entities.projectiles;

import com.Polarice3.Goety.client.particles.SmashParticleOption;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.utils.ModDamageSource;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import java.util.Optional;

public class PureLightEntity extends AoeEntity {

    public static final int WARMUP_TIME = 30;

    public PureLightEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setRadius(2.5F);
        this.setNoGravity(true);
        this.setDamage((float) Config.heavenRiftBaseDamage);
    }

    public PureLightEntity(Level level) {
        this(com.k1sak1.goetyawaken.common.entities.ModEntityType.PURE_LIGHT.get(), level);
    }

    @Override
    public void tick() {
        this.setOldPosAndRot();
        // if (!level().isClientSide) {
        // if (level() instanceof ServerLevel serverLevel) {
        // double radius = 2.5F;
        // ColorUtil colorUtil = new ColorUtil(0xFFFFFF);
        // ServerParticleUtil.windParticle(serverLevel, colorUtil, (float) radius, 1.0F,
        // this.getId(),
        // this.position());
        // }
        // }
        if (tickCount == WARMUP_TIME) {
            if (!level().isClientSide) {
                checkHits();
                if (level() instanceof ServerLevel serverLevel) {
                    if (random.nextFloat() <= 0.1F) {
                        ColorUtil whiteColor = new ColorUtil(0xFFFFFF);
                        ServerParticleUtil.windShockwaveParticle(serverLevel,
                                whiteColor, 0.2f, 0.2f, -1, new Vec3(getX(), getY() + 0.1D, getZ()));
                    }

                    ColorUtil whiteColor = new ColorUtil(0xFFFFFF);
                    serverLevel.sendParticles(
                            new SmashParticleOption(whiteColor, 2.5F, 1.0F, 15),
                            getX(), getY(), getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }

                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        getRandomPureLightEntitySound(), this.getSoundSource(), 1.0F, 1.0F);
            }
        }
        if (this.tickCount > WARMUP_TIME) {
            discard();
        }
    }

    private SoundEvent getRandomPureLightEntitySound() {
        SoundEvent[] sounds = {
                com.k1sak1.goetyawaken.init.ModSounds.PURE_LIGHT_1.get(),
                com.k1sak1.goetyawaken.init.ModSounds.PURE_LIGHT_2.get()
        };
        return sounds[this.random.nextInt(sounds.length)];
    }

    @Override
    protected boolean canHitTargetForGroundContext(LivingEntity target) {
        return true;
    }

    @Override
    public void applyEffect(LivingEntity target) {
        if (getOwner() != null && com.Polarice3.Goety.utils.MobUtil.areAllies(getOwner(), target)) {
            return;
        }
        float totalDamage = getDamage() + getExtraDamage();
        target.hurt(ModDamageSource.pureLight(this, getOwner()), totalDamage);

    }

    @Override
    protected Vec3 getInflation() {
        return new Vec3(2, 2, 2);
    }

    @Override
    public float getParticleCount() {
        return 0f;
    }

    @Override
    public void refreshDimensions() {
        return;
    }

    @Override
    public void ambientParticles() {
        return;
    }

    @Override
    public Optional<ParticleOptions> getParticle() {
        return Optional.empty();
    }

    private float extraDamage = 0.0F;

    private int renderColor = 0xFFFFFF;

    public void setExtraDamage(float extraDamage) {
        this.extraDamage = extraDamage;
    }

    public float getExtraDamage() {
        return this.extraDamage;
    }

    public void setRenderColor(int color) {
        this.renderColor = color;
    }

    public void setRenderColor(int r, int g, int b) {
        this.renderColor = (r << 16) | (g << 8) | b;
    }

    public int getRenderColor() {
        return this.renderColor;
    }

    public int getRed() {
        return (renderColor >> 16) & 0xFF;
    }

    public int getGreen() {
        return (renderColor >> 8) & 0xFF;
    }

    public int getBlue() {
        return renderColor & 0xFF;
    }
}