package com.k1sak1.goetyawaken.common.entities.ally.illager;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.illager.cultist.WitchServant;
import com.Polarice3.Goety.common.entities.ally.illager.cultist.WarlockServant;
import com.Polarice3.Goety.common.entities.ally.illager.cultist.MaverickServant;
import com.Polarice3.Goety.common.entities.ally.illager.cultist.HereticServant;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.goal.Goal;
import com.Polarice3.Goety.api.entities.IOwned;

public class ApostleConvertVillagerGoal extends Goal {
    private final ApostleServant apostleServant;
    private LivingEntity victim;
    private int coolDown;
    private int spellWarmup;

    public ApostleConvertVillagerGoal(ApostleServant apostleServant) {
        this.apostleServant = apostleServant;
        this.coolDown = 0;
        this.spellWarmup = 0;
    }

    @Override
    public boolean canUse() {
        if (this.coolDown > 0) {
            this.coolDown--;
            return false;
        }

        if (this.apostleServant.getCommandPosEntity() != null) {
            Entity commandEntity = this.apostleServant.getCommandPosEntity();
            if (commandEntity instanceof Villager || commandEntity instanceof WanderingTrader) {
                this.victim = (LivingEntity) commandEntity;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.victim != null && this.victim.isAlive() && this.spellWarmup > 0;
    }

    @Override
    public void start() {
        this.spellWarmup = this.getCastWarmupTime();
        this.apostleServant.getLookControl().setLookAt(this.victim);
        this.apostleServant.setSpellType(SpellCastingCultistServant.SpellType.ZOMBIE);
    }

    @Override
    public void tick() {
        --this.spellWarmup;
        if (this.spellWarmup == 0) {
            this.castSpell();
        }

        if (this.victim != null && this.victim.isAlive()) {
            if (this.victim instanceof Mob mobVictim) {
                mobVictim.getNavigation().stop();
                mobVictim.getMoveControl().strafe(0.0F, 0.0F);
            }
            this.apostleServant.getLookControl().setLookAt(this.victim);
            Vec3 offset = new Vec3(2, 0, 0);
            Vec3 at = this.groundOf(this.victim.position().add(offset));
            this.apostleServant.getNavigation().moveTo(at.x, at.y, at.z, 0.75F);
            if (this.apostleServant.tickCount % 5 == 0) {
                if (this.apostleServant.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.ENCHANT,
                            this.victim.getX(), this.victim.getY() + 1.0, this.victim.getZ(),
                            10, 0.5, 0.5, 0.5, 0.0);
                }
            }
        }
    }

    @Override
    public void stop() {
        this.victim = null;
        this.apostleServant.setCommandPosEntity(null);
        this.coolDown = 20;
        this.apostleServant.setSpellType(SpellCastingCultistServant.SpellType.NONE);
    }

    protected void castSpell() {
        if (this.victim != null && this.victim.isAlive()) {
            if (this.victim instanceof WanderingTrader) {
                convertToMaverick();
            } else if (this.victim instanceof Villager) {
                convertToWitchOrWarlock();
            }
        }
    }

    private void convertToMaverick() {
        if (this.apostleServant.level() instanceof ServerLevel serverLevel) {
            net.minecraft.world.entity.player.Player player = null;
            if (this.apostleServant.getTrueOwner() instanceof net.minecraft.world.entity.player.Player player1) {
                player = player1;
            }

            Entity convertedEntity = MobUtil.convertTo(this.victim,
                    ModEntityType.MAVERICK_SERVANT.get(), true, player);

            if (convertedEntity instanceof Mob mob && mob instanceof IOwned owned) {
                owned.setTrueOwner(this.apostleServant.getTrueOwner());
                mob.setYHeadRot(this.victim.getYHeadRot());
                mob.setYRot(this.victim.getYRot());
                mob.spawnAnim();
                serverLevel.playSound(null, mob.getX(), mob.getY(), mob.getZ(),
                        SoundEvents.ZOMBIE_VILLAGER_CURE, mob.getSoundSource(), 1.0F, 1.0F);
            }
        }
    }

    private void convertToWitchOrWarlock() {
        if (this.apostleServant.level() instanceof ServerLevel serverLevel) {
            net.minecraft.world.entity.player.Player player = null;
            if (this.apostleServant.getTrueOwner() instanceof net.minecraft.world.entity.player.Player player1) {
                player = player1;
            }
            Entity convertedEntity;
            double rand = Math.random();
            if (rand < 0.3333) {
                convertedEntity = MobUtil.convertTo(this.victim,
                        ModEntityType.WARLOCK_SERVANT.get(), true, player);
            } else if (rand < 0.6666) {
                convertedEntity = MobUtil.convertTo(this.victim,
                        ModEntityType.WITCH_SERVANT.get(), true, player);
            } else {
                convertedEntity = MobUtil.convertTo(this.victim,
                        ModEntityType.HERETIC_SERVANT.get(), true, player);
            }

            if (convertedEntity instanceof WitchServant witchServant) {
                witchServant.setTrueOwner(this.apostleServant.getTrueOwner());
                witchServant.setYHeadRot(this.victim.getYHeadRot());
                witchServant.setYRot(this.victim.getYRot());
                witchServant.spawnAnim();
                serverLevel.playSound(null, witchServant.getX(), witchServant.getY(), witchServant.getZ(),
                        SoundEvents.ZOMBIE_VILLAGER_CURE, witchServant.getSoundSource(), 1.0F, 1.0F);
            } else if (convertedEntity instanceof MaverickServant maverickServant) {
                maverickServant.setTrueOwner(this.apostleServant.getTrueOwner());
                maverickServant.setYHeadRot(this.victim.getYHeadRot());
                maverickServant.setYRot(this.victim.getYRot());
                maverickServant.spawnAnim();
                serverLevel.playSound(null, maverickServant.getX(), maverickServant.getY(), maverickServant.getZ(),
                        SoundEvents.ZOMBIE_VILLAGER_CURE, maverickServant.getSoundSource(), 1.0F, 1.0F);
            } else if (convertedEntity instanceof WarlockServant warlockServant) {
                warlockServant.setTrueOwner(this.apostleServant.getTrueOwner());
                warlockServant.setYHeadRot(this.victim.getYHeadRot());
                warlockServant.setYRot(this.victim.getYRot());
                warlockServant.spawnAnim();
                serverLevel.playSound(null, warlockServant.getX(), warlockServant.getY(), warlockServant.getZ(),
                        SoundEvents.ZOMBIE_VILLAGER_CURE, warlockServant.getSoundSource(), 1.0F, 1.0F);
            } else if (convertedEntity instanceof HereticServant hereticServant) {
                hereticServant.setTrueOwner(this.apostleServant.getTrueOwner());
                hereticServant.setYHeadRot(this.victim.getYHeadRot());
                hereticServant.setYRot(this.victim.getYRot());
                hereticServant.spawnAnim();
                serverLevel.playSound(null, hereticServant.getX(), hereticServant.getY(), hereticServant.getZ(),
                        SoundEvents.ZOMBIE_VILLAGER_CURE, hereticServant.getSoundSource(), 1.0F, 1.0F);
            }
        }
    }

    private Vec3 groundOf(Vec3 in) {
        int x = (int) in.x;
        int y = (int) in.y;
        int z = (int) in.z;
        while (this.apostleServant.level().isEmptyBlock(new net.minecraft.core.BlockPos(x, y - 1, z))
                && y > this.apostleServant.level().getMinBuildHeight()) {
            y--;
        }
        while (!this.apostleServant.level().isEmptyBlock(new net.minecraft.core.BlockPos(x, y, z))
                && y < this.apostleServant.level().getMaxBuildHeight()) {
            y++;
        }

        return new Vec3(in.x, y, in.z);
    }

    protected int getCastWarmupTime() {
        return 40;
    }
}
