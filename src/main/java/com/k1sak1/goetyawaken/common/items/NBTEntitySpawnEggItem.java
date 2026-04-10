package com.k1sak1.goetyawaken.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeSpawnEggItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.world.phys.AABB;

public class NBTEntitySpawnEggItem extends ForgeSpawnEggItem {

    public static final String ENTITY_TAG_KEY = "StoredEntityData";
    public static final String ENTITY_ID_KEY = "StoredEntityId";

    public NBTEntitySpawnEggItem(Properties builder) {
        super(() -> EntityType.ZOMBIE, 0x9D9D9D, 0x4C4C4C, builder);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();

        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        ItemStack itemstack = context.getItemInHand();
        BlockPos blockpos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        BlockState blockstate = level.getBlockState(blockpos);
        if (blockstate.is(Blocks.SPAWNER)) {
            BlockEntity blockentity = level.getBlockEntity(blockpos);
            if (blockentity instanceof SpawnerBlockEntity spawnerblockentity) {
                EntityType<?> entitytype1 = this.getType(itemstack.getTag());
                spawnerblockentity.setEntityId(entitytype1, level.getRandom());
                blockentity.setChanged();
                level.sendBlockUpdated(blockpos, blockstate, blockstate, 3);
                level.gameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, blockpos);
                itemstack.shrink(1);
                return InteractionResult.CONSUME;
            }
        }

        if (hasStoredEntityData(itemstack)) {
            BlockPos blockpos1;
            if (blockstate.getCollisionShape(level, blockpos).isEmpty()) {
                blockpos1 = blockpos;
            } else {
                blockpos1 = blockpos.relative(direction);
            }

            CompoundTag storedTag = itemstack.getTagElement(ENTITY_TAG_KEY);

            EntityType<?> entitytype = this.getType(itemstack.getTag());
            Entity entity = entitytype.spawn(serverLevel, itemstack, context.getPlayer(), blockpos1,
                    MobSpawnType.SPAWN_EGG, true, !Objects.equals(blockpos, blockpos1) && direction == Direction.UP);

            if (entity != null) {
                if (storedTag != null) {
                    Vec3 currentPos = entity.position();
                    float currentYaw = entity.getYRot();
                    float currentPitch = entity.getXRot();
                    entity.load(storedTag);
                    entity.moveTo(currentPos.x(), currentPos.y(), currentPos.z(), currentYaw, currentPitch);
                }

                itemstack.shrink(1);
                level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockpos);
            }

            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player,
            @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.isShiftKeyDown() && hasStoredEntityData(itemstack)) {
            clearStoredEntityData(itemstack);
            player.displayClientMessage(
                    Component.translatable("item.goetyawaken.nbt_entity_spawn_egg.cleared"), true);
            return InteractionResultHolder.success(itemstack);
        }

        if (player.isCreative() && !hasStoredEntityData(itemstack)) {
            Entity targetedEntity = getEntityLookingAt(player);
            if (targetedEntity != null && targetedEntity instanceof LivingEntity livingEntity) {
                boolean captureResult = tryCaptureEntityFromEntity(livingEntity, player, itemstack);
                if (captureResult) {
                    return InteractionResultHolder.success(itemstack);
                }
            }
        }

        BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);

        if (blockhitresult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(itemstack);
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResultHolder.success(itemstack);
        }

        BlockPos blockpos = blockhitresult.getBlockPos();
        if (!(level.getBlockState(blockpos).getBlock() instanceof LiquidBlock)) {
            return InteractionResultHolder.pass(itemstack);
        }

        if (level.mayInteract(player, blockpos)
                && player.mayUseItemAt(blockpos, blockhitresult.getDirection(), itemstack)) {
            if (hasStoredEntityData(itemstack)) {
                EntityType<?> entitytype = this.getType(itemstack.getTag());
                CompoundTag storedTag = itemstack.getTagElement(ENTITY_TAG_KEY);

                Entity entity = entitytype.spawn(serverLevel, itemstack, player, blockpos, MobSpawnType.SPAWN_EGG,
                        false, false);
                if (entity == null) {
                    return InteractionResultHolder.pass(itemstack);
                } else {
                    if (storedTag != null) {
                        Vec3 currentPos = entity.position();
                        float currentYaw = entity.getYRot();
                        float currentPitch = entity.getXRot();
                        entity.load(storedTag);
                        entity.moveTo(currentPos.x(), currentPos.y(), currentPos.z(), currentYaw, currentPitch);
                    }

                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                    level.gameEvent(player, GameEvent.ENTITY_PLACE, entity.position());
                    return InteractionResultHolder.consume(itemstack);
                }
            } else {
                return InteractionResultHolder.pass(itemstack);
            }
        } else {
            return InteractionResultHolder.fail(itemstack);
        }
    }

    private InteractionResult tryCaptureEntity(UseOnContext context, ItemStack itemstack) {
        Player player = context.getPlayer();
        if (player == null)
            return InteractionResult.PASS;
        Entity targetEntity = getEntityLookingAt(player);
        if (targetEntity == null)
            return InteractionResult.PASS;

        if (!(targetEntity instanceof LivingEntity)) {
            return InteractionResult.PASS;
        }

        LivingEntity targetLivingEntity = (LivingEntity) targetEntity;

        boolean result = tryCaptureEntityFromEntity(targetEntity, player, itemstack);
        return result ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    public boolean tryCaptureEntityFromEntity(Entity targetEntity, Player player, ItemStack itemstack) {
        if (!(targetEntity instanceof LivingEntity)) {
            return false;
        }
        LivingEntity targetLivingEntity = (LivingEntity) targetEntity;
        CompoundTag entityTag = new CompoundTag();
        targetEntity.saveWithoutId(entityTag);
        entityTag.remove("Pos");
        entityTag.remove("Motion");
        entityTag.remove("FallDistance");
        entityTag.remove("OnGround");
        entityTag.remove("UUID");
        entityTag.remove("UUIDMost");
        entityTag.remove("UUIDLeast");
        String entityId = EntityType.getKey(targetEntity.getType()).toString();
        if (!itemstack.hasTag()) {
            itemstack.setTag(new CompoundTag());
        }
        itemstack.getTag().putString(ENTITY_ID_KEY, entityId);
        itemstack.getTag().put(ENTITY_TAG_KEY, entityTag);
        String entityName = targetEntity.getName().getString();
        player.displayClientMessage(
                Component.translatable("item.goetyawaken.nbt_entity_spawn_egg.captured", entityName), true);

        return true;
    }

    public static boolean hasStoredEntityData(ItemStack stack) {
        if (!stack.hasTag()) {
            return false;
        }
        return stack.getTag().contains(ENTITY_ID_KEY) && stack.getTag().contains(ENTITY_TAG_KEY);
    }

    public static void clearStoredEntityData(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag.contains(ENTITY_ID_KEY)) {
                tag.remove(ENTITY_ID_KEY);
            }
            if (tag.contains(ENTITY_TAG_KEY)) {
                tag.remove(ENTITY_TAG_KEY);
            }
            if (tag.isEmpty()) {
                stack.setTag(null);
            }
        }
        stack.setHoverName(null);
    }

    @Override
    public EntityType<?> getType(@Nullable CompoundTag tag) {
        if (tag != null && tag.contains(ENTITY_ID_KEY)) {
            String entityId = tag.getString(ENTITY_ID_KEY);
            EntityType<?> storedType = EntityType.byString(entityId).orElse(null);
            if (storedType != null) {
                return storedType;
            }
        }
        return EntityType.ZOMBIE;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltip,
            @Nonnull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (hasStoredEntityData(stack)) {
            CompoundTag tag = stack.getTag();
            String entityId = tag.getString(ENTITY_ID_KEY);
            tooltip.add(Component.translatable("item.goetyawaken.nbt_entity_spawn_egg.stored_entity", entityId)
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("item.goetyawaken.nbt_entity_spawn_egg.clear_tooltip")
                    .withStyle(ChatFormatting.YELLOW));
        } else {
            MutableComponent textComponent = Component
                    .translatable("item.goetyawaken.nbt_entity_spawn_egg.capture_tooltip")
                    .withStyle(ChatFormatting.GOLD);
            tooltip.add(textComponent);
        }
    }

    @Override
    public Optional<Mob> spawnOffspringFromSpawnEgg(@NotNull Player player, @NotNull Mob targetMob,
            @NotNull EntityType<? extends Mob> entityType, @NotNull ServerLevel serverLevel, @NotNull Vec3 vec3,
            ItemStack itemStack) {
        if (!this.spawnsEntity(itemStack.getTag(), entityType)) {
            return Optional.empty();
        } else {
            Mob mob = entityType.create(serverLevel);
            if (mob == null) {
                return Optional.empty();
            } else {
                mob.setBaby(true);
                if (!mob.isBaby()) {
                    return Optional.empty();
                } else {
                    mob.moveTo(vec3.x(), vec3.y(), vec3.z(), 0.0F, 0.0F);
                    serverLevel.addFreshEntityWithPassengers(mob);

                    if (itemStack.hasCustomHoverName()) {
                        mob.setCustomName(itemStack.getHoverName());
                    }

                    if (!player.getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }

                    return Optional.of(mob);
                }
            }
        }
    }

    private Entity getEntityLookingAt(Player player) {
        double reachDistance = player.getAttributeValue(net.minecraftforge.common.ForgeMod.BLOCK_REACH.get());
        Vec3 eyePos = player.getEyePosition(1.0F);
        Vec3 lookVec = player.getLookAngle();
        Vec3 reachVec = eyePos.add(lookVec.x * reachDistance, lookVec.y * reachDistance, lookVec.z * reachDistance);

        AABB aabb = player.getBoundingBox().expandTowards(lookVec.scale(reachDistance)).inflate(1.0D, 1.0D, 1.0D);
        List<Entity> entities = player.level().getEntities(player, aabb, entity -> {
            return entity != null && entity.isAlive() && entity != player && entity instanceof LivingEntity;
        });

        Entity closestEntity = null;
        double closestDistance = Double.MAX_VALUE;

        for (Entity entity : entities) {
            Vec3 entityCenter = new Vec3(entity.getX(), entity.getY() + entity.getBbHeight() / 2.0D, entity.getZ());
            double distance = eyePos.distanceToSqr(entityCenter);

            if (distance < closestDistance) {
                Vec3 entityDir = entityCenter.subtract(eyePos).normalize();
                double dotProduct = lookVec.dot(entityDir);

                if (dotProduct > 0.95) {
                    closestEntity = entity;
                    closestDistance = distance;
                }
            }
        }

        return closestEntity;
    }
}