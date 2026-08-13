package com.k1sak1.goetyawaken.common.mobenchant;

import com.Polarice3.Goety.common.entities.projectiles.IceSpike;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.GoetyAwaken;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

@Mod.EventBusSubscriber(modid = GoetyAwaken.MODID)
public class MultiShotMobEnchant {
    private static final String MULTISHOT_CLONE_TAG = "GoetyAwakenMultiShotClone";

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.loadedFromDisk()) {
            return;
        }
        if (!(event.getEntity() instanceof Projectile projectile)) {
            return;
        }
        Level level = event.getLevel();
        if (level.isClientSide) {
            return;
        }
        if (projectile.getPersistentData().getBoolean(MULTISHOT_CLONE_TAG)) {
            return;
        }
        if (!isAllowedProjectile(projectile)) {
            return;
        }
        if (!(projectile.getOwner() instanceof LivingEntity livingOwner)) {
            return;
        }
        if (!(livingOwner instanceof IMobEnchantable enchantable)) {
            return;
        }
        MobEnchantCapability capability = enchantable.getMobEnchantCapabilityInstance();
        if (capability == null) {
            return;
        }
        int multishotLevel = capability.getMobEnchantLevel(MobEnchantType.MULTISHOT);
        if (multishotLevel <= 0) {
            return;
        }

        int totalProjectiles = 1 + 2 * multishotLevel;
        float angleStep = totalProjectiles <= 5 ? 15.0F : 7.5F;
        int centerIndex = totalProjectiles / 2;

        for (int i = 0; i < totalProjectiles; i++) {
            if (i == centerIndex) {
                continue;
            }
            float angleDegrees = (i - centerIndex) * angleStep;
            addProjectile(projectile, livingOwner, level, angleDegrees);
        }
    }

    private static boolean isAllowedProjectile(Projectile projectile) {
        List<? extends String> allowedProjectiles = Config.allowMultiShotProjectiles;
        if (allowedProjectiles == null) {
            return false;
        }
        ResourceLocation registryName = ForgeRegistries.ENTITY_TYPES.getKey(projectile.getType());
        if (registryName == null) {
            return false;
        }
        String projectileId = registryName.toString();
        for (String allowed : allowedProjectiles) {
            if (allowed.equals(projectileId)) {
                return true;
            }
        }
        return false;
    }

    private static void addProjectile(Projectile original, LivingEntity owner, Level level, float angleDegrees) {
        Entity newEntity = original.getType().create(level);
        if (!(newEntity instanceof Projectile clone)) {
            return;
        }

        clone.getPersistentData().putBoolean(MULTISHOT_CLONE_TAG, true);
        clone.setOwner(owner);
        clone.setPos(original.position());
        clone.setDeltaMovement(original.getDeltaMovement());

        float angleRadians = angleDegrees * ((float) Math.PI / 180.0F);
        Vec3 newMotion = clone.getDeltaMovement().yRot(angleRadians);
        clone.setDeltaMovement(newMotion);

        float horizontalSpeed = Mth.sqrt((float) newMotion.horizontalDistanceSqr());
        clone.setYRot((float) (Mth.atan2(newMotion.x, newMotion.z) * (180.0 / Math.PI)));
        clone.setXRot((float) (Mth.atan2(newMotion.y, horizontalSpeed) * (180.0 / Math.PI)));
        clone.yRotO = clone.getYRot();
        clone.xRotO = clone.getXRot();

        if (clone instanceof AbstractArrow arrowClone && original instanceof AbstractArrow arrowOriginal) {
            arrowClone.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            arrowClone.setBaseDamage(arrowOriginal.getBaseDamage());
            arrowClone.setCritArrow(arrowOriginal.isCritArrow());
            if (original instanceof IceSpike originalSpike && clone instanceof IceSpike cloneSpike) {
                cloneSpike.setExtraDamage(originalSpike.getExtraDamage());
            }
        }

        if (clone instanceof AbstractHurtingProjectile hurtingClone
                && original instanceof AbstractHurtingProjectile hurtingOriginal) {
            Vec3 rotatedPower = new Vec3(hurtingOriginal.xPower, hurtingOriginal.yPower, hurtingOriginal.zPower)
                    .yRot(angleRadians);
            hurtingClone.xPower = rotatedPower.x;
            hurtingClone.yPower = rotatedPower.y;
            hurtingClone.zPower = rotatedPower.z;
        }

        level.addFreshEntity(clone);
    }
}
