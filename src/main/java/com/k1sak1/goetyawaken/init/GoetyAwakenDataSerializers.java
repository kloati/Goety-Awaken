package com.k1sak1.goetyawaken.init;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class GoetyAwakenDataSerializers {

    public static final DeferredRegister<EntityDataSerializer<?>> DATA_SERIALIZERS = DeferredRegister
            .create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, "goetyawaken");

    public static final EntityDataSerializer<Map<BlockPos, BlockState>> BLOCK_STATE_POS_MAP = new EntityDataSerializer<>() {
        @Override
        public void write(FriendlyByteBuf buffer, Map<BlockPos, BlockState> map) {
            buffer.writeVarInt(map.size());
            for (var entry : map.entrySet()) {
                BlockPos pos = entry.getKey();
                buffer.writeVarInt(pos.getX());
                buffer.writeVarInt(pos.getY());
                buffer.writeVarInt(pos.getZ());
                buffer.writeVarInt(Block.getId(entry.getValue()));
            }
        }

        @Override
        public Map<BlockPos, BlockState> read(FriendlyByteBuf buffer) {
            int size = buffer.readVarInt();
            Map<BlockPos, BlockState> map = new LinkedHashMap<>(size);
            for (int i = 0; i < size; i++) {
                BlockPos pos = new BlockPos(buffer.readVarInt(), buffer.readVarInt(), buffer.readVarInt());
                map.put(pos, Block.stateById(buffer.readVarInt()));
            }
            return map;
        }

        @Override
        public Map<BlockPos, BlockState> copy(Map<BlockPos, BlockState> map) {
            return new LinkedHashMap<>(map);
        }
    };

    public static final EntityDataSerializer<List<CompoundTag>> COMPOUND_LIST = new EntityDataSerializer<>() {
        @Override
        public void write(FriendlyByteBuf buffer, List<CompoundTag> list) {
            buffer.writeVarInt(list.size());
            for (CompoundTag tag : list) {
                buffer.writeNbt(tag);
            }
        }

        @Override
        public List<CompoundTag> read(FriendlyByteBuf buffer) {
            int size = buffer.readVarInt();
            List<CompoundTag> list = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                list.add(buffer.readNbt());
            }
            return list;
        }

        @Override
        public List<CompoundTag> copy(List<CompoundTag> list) {
            List<CompoundTag> result = new ArrayList<>(list.size());
            for (CompoundTag tag : list) {
                result.add(tag.copy());
            }
            return result;
        }
    };

    public static final EntityDataSerializer<Vec2> VECTOR_2F = new EntityDataSerializer<>() {
        @Override
        public void write(FriendlyByteBuf buffer, Vec2 vector) {
            buffer.writeFloat(vector.x);
            buffer.writeFloat(vector.y);
        }

        @Override
        public Vec2 read(FriendlyByteBuf buffer) {
            return new Vec2(buffer.readFloat(), buffer.readFloat());
        }

        @Override
        public Vec2 copy(Vec2 vector) {
            return new Vec2(vector.x, vector.y);
        }
    };

    static {
        DATA_SERIALIZERS.register("cluster_block_map", () -> BLOCK_STATE_POS_MAP);
        DATA_SERIALIZERS.register("compound_tag_list", () -> COMPOUND_LIST);
        DATA_SERIALIZERS.register("rotation_vector", () -> VECTOR_2F);
    }
}
