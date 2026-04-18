package com.k1sak1.goetyawaken.common.entities.deco;

import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.init.ModPaintings;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OminousPainting extends Painting {

    public OminousPainting(EntityType<? extends Painting> entityType, Level level) {
        super(entityType, level);
    }

    public OminousPainting(EntityType<? extends Painting> entityType, Level level, BlockPos blockPos) {
        this(entityType, level);
        this.pos = blockPos;
    }

    public OminousPainting(Level level, BlockPos blockPos) {
        this(ModEntityType.OMINOUS_PAINTING.get(), level, blockPos);
    }

    public OminousPainting(Level level, BlockPos blockPos, Direction direction, Holder<PaintingVariant> variant) {
        this(ModEntityType.OMINOUS_PAINTING.get(), level);
        this.setVariant(variant);
        this.setDirection(direction);
        this.pos = blockPos;
    }

    public static Optional<OminousPainting> createOminousRandom(Level level, BlockPos blockPos, Direction direction) {
        OminousPainting painting = new OminousPainting(level, blockPos);
        List<Holder<PaintingVariant>> list = new ArrayList<>();

        ModPaintings.OMINOUS_PAINTING_VARIANTS.getEntries().forEach(entry -> {
            BuiltInRegistries.PAINTING_VARIANT.getHolder(entry.getKey()).ifPresent(list::add);
        });

        if (list.isEmpty()) {
            return Optional.empty();
        } else {
            painting.setDirection(direction);
            list.removeIf((variant) -> {
                painting.setVariant(variant);
                return !painting.survives();
            });

            if (list.isEmpty()) {
                return Optional.empty();
            } else {
                list.removeIf((variant) -> {
                    return variant.value().getWidth() != 32 || variant.value().getHeight() != 32;
                });

                if (list.isEmpty()) {
                    return Optional.empty();
                }

                Holder<PaintingVariant> randomVariant = list.get(level.random.nextInt(list.size()));
                painting.setVariant(randomVariant);
                painting.setDirection(direction);
                return Optional.of(painting);
            }
        }
    }

    @Override
    public void dropItem(@Nullable Entity entity) {
        if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.playSound(net.minecraft.sounds.SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
            if (entity instanceof Player player) {
                if (player.getAbilities().instabuild) {
                    return;
                }
            }

            this.spawnAtLocation(com.k1sak1.goetyawaken.common.items.ModItems.OMINOUS_PAINTING.get());
        }
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(com.k1sak1.goetyawaken.common.items.ModItems.OMINOUS_PAINTING.get());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(
                this, this.getDirection().get3DDataValue(), this.getPos());
    }
}