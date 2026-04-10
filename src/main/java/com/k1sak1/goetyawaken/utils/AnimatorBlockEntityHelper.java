package com.k1sak1.goetyawaken.utils;

import com.Polarice3.Goety.api.magic.IMold;
import com.Polarice3.Goety.common.blocks.entities.AnimatorBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.undead.HauntedArmorServant;
import com.Polarice3.Goety.common.entities.deco.HauntedArmorStand;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.WaystoneItem;
import com.Polarice3.Goety.common.magic.construct.SpawnFromBlock;
import com.Polarice3.Goety.common.research.ResearchList;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AnimatorBlockEntityHelper {
    private static final Logger LOGGER = LogManager.getLogger();

    public static boolean enhancedSummonGolem(AnimatorBlockEntity animator) {
        try {
            Level level = animator.getLevel();

            if (level == null) {
                return false;
            }

            Method getPositionMethod = AnimatorBlockEntity.class.getDeclaredMethod("getPosition");
            getPositionMethod.setAccessible(true);
            GlobalPos position = (GlobalPos) getPositionMethod.invoke(animator);

            if (position != null) {
                if (level.dimension() == position.dimension()) {
                    if (level.isLoaded(position.pos())) {
                        Method checkCageMethod = AnimatorBlockEntity.class.getDeclaredMethod("checkCage");
                        checkCageMethod.setAccessible(true);
                        boolean checkCageResult = (boolean) checkCageMethod.invoke(animator);

                        Field cursedCageTileField = AnimatorBlockEntity.class.getDeclaredField("cursedCageTile");
                        cursedCageTileField.setAccessible(true);
                        CursedCageBlockEntity cursedCageTile = (CursedCageBlockEntity) cursedCageTileField
                                .get(animator);

                        if (checkCageResult && cursedCageTile != null
                                && cursedCageTile.getSouls() >= getSoulCost(animator)) {
                            ItemStack itemStack = ModItems.ANIMATION_CORE.get().getDefaultInstance();
                            BlockState blockState = level.getBlockState(position.pos());

                            AABB aabb = new AABB(position.pos());
                            List<HauntedArmorStand> list = level.getEntitiesOfClass(HauntedArmorStand.class, aabb,
                                    ItemHelper::isFullEquipped);
                            Optional<HauntedArmorStand> optional = !list.isEmpty() ? list.stream().findFirst()
                                    : Optional.empty();

                            if (optional.isPresent()
                                    && SEHelper.hasResearch(getOwner(animator), ResearchList.HAUNTING)) {
                                HauntedArmorStand hauntedArmorStand = optional.get();
                                HauntedArmorServant hauntedArmorServant = new HauntedArmorServant(
                                        ModEntityType.HAUNTED_ARMOR_SERVANT.get(), level);
                                for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                                    hauntedArmorServant.setItemSlot(equipmentSlot,
                                            hauntedArmorStand.getItemBySlot(equipmentSlot));
                                    hauntedArmorServant.setGuaranteedDrop(equipmentSlot);
                                }
                                hauntedArmorServant.setPersistenceRequired();
                                hauntedArmorServant.setTrueOwner(getOwner(animator));
                                hauntedArmorServant.moveTo(hauntedArmorStand.blockPosition(),
                                        hauntedArmorStand.getYRot(), hauntedArmorStand.getXRot());
                                hauntedArmorServant.setLeftHanded(getOwner(animator).getMainArm() == HumanoidArm.LEFT);
                                if (level.addFreshEntity(hauntedArmorServant)) {
                                    hauntedArmorStand.playSound(ModSounds.SUMMON_SPELL.get());
                                    showBreakingParticles(hauntedArmorStand, level);
                                    hauntedArmorStand.discard();
                                    return true;
                                }
                            } else if (GolemTypeHelper.getEnhancedGolemList().containsKey(blockState)) {
                                IMold mold = GolemTypeHelper.getEnhancedGolemList().get(blockState);
                                if (mold.spawnServant(getOwner(animator), itemStack, level, position.pos())) {
                                    level.playSound(null, animator.getBlockPos(), ModSounds.SUMMON_SPELL.get(),
                                            SoundSource.BLOCKS, 1.0F, 1.0F);
                                    level.playSound(null, position.pos(), ModSounds.SUMMON_SPELL.get(),
                                            SoundSource.BLOCKS, 1.0F, 1.0F);
                                    cursedCageTile.decreaseSouls(getSoulCost(animator));
                                    generateManyParticles(animator);
                                    return true;
                                }
                            } else {
                                if (SpawnFromBlock.spawnServant(getOwner(animator), itemStack, level, position.pos())) {
                                    level.playSound(null, animator.getBlockPos(), ModSounds.SUMMON_SPELL.get(),
                                            SoundSource.BLOCKS, 1.0F, 1.0F);
                                    level.playSound(null, position.pos(), ModSounds.SUMMON_SPELL.get(),
                                            SoundSource.BLOCKS, 1.0F, 1.0F);
                                    cursedCageTile.decreaseSouls(getSoulCost(animator));
                                    generateManyParticles(animator);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
        return false;
    }

    private static void showBreakingParticles(HauntedArmorStand hauntedArmorStand, Level level) {
        if (level instanceof ServerLevel) {
            ((ServerLevel) level).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK,
                    com.Polarice3.Goety.common.blocks.ModBlocks.HAUNTED_PLANKS.get().defaultBlockState()),
                    hauntedArmorStand.getX(), hauntedArmorStand.getY(0.6666666666666666D), hauntedArmorStand.getZ(),
                    10, (double) (hauntedArmorStand.getBbWidth() / 4.0F),
                    (double) (hauntedArmorStand.getBbHeight() / 4.0F),
                    (double) (hauntedArmorStand.getBbWidth() / 4.0F), 0.05D);
        }
    }

    private static int getSoulCost(AnimatorBlockEntity animator) {
        try {
            Method getPositionMethod = AnimatorBlockEntity.class.getDeclaredMethod("getPosition");
            getPositionMethod.setAccessible(true);
            GlobalPos position = (GlobalPos) getPositionMethod.invoke(animator);

            if (position != null) {
                double distance = animator.getBlockPos().distToCenterSqr(position.pos().getCenter());
                return (int) (MainConfig.AnimatorCost.get() * distance);
            }
        } catch (Exception e) {

        }
        return 0;
    }

    private static Player getOwner(AnimatorBlockEntity animator) {
        try {
            Level level = animator.getLevel();

            Field itemField = AnimatorBlockEntity.class.getDeclaredField("item");
            itemField.setAccessible(true);
            ItemStack item = (ItemStack) itemField.get(animator);

            if (level != null && !item.isEmpty()) {
                if (item.getItem() instanceof WaystoneItem && item.getTag() != null) {
                    if (item.getTag().contains(WaystoneItem.TAG_OWNER)) {
                        UUID owner = item.getTag().getUUID(WaystoneItem.TAG_OWNER);
                        return level.getPlayerByUUID(owner);
                    }
                }
            }
        } catch (Exception e) {

        }
        return null;
    }

    private static void generateManyParticles(AnimatorBlockEntity animator) {
        try {
            Level level = animator.getLevel();

            BlockPos blockPos = animator.getBlockPos();
            if (level != null && !level.isClientSide) {
                ServerLevel serverWorld = (ServerLevel) level;
                for (int k = 0; k < 20; ++k) {
                    double d9 = (double) blockPos.getX() + 0.5D + (level.random.nextDouble() - 0.5D) * 2.0D;
                    double d13 = (double) blockPos.getY() + 0.5D + (level.random.nextDouble() - 0.5D) * 2.0D;
                    double d19 = (double) blockPos.getZ() + 0.5D + (level.random.nextDouble() - 0.5D) * 2.0D;
                    serverWorld.sendParticles(ParticleTypes.SMOKE, d9, d13, d19, 1, 0.0D, 0.0D, 0.0D, 0);
                    serverWorld.sendParticles(ParticleTypes.FLAME, d9, d13, d19, 1, 0.0D, 0.0D, 0.0D, 0);
                }
            }
        } catch (Exception e) {

        }
    }
}