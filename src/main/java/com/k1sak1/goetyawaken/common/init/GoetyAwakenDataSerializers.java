package com.k1sak1.goetyawaken.common.init;

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

import com.k1sak1.goetyawaken.utils.GoetyAwakenNBTUtil;

public class GoetyAwakenDataSerializers {

    public static final DeferredRegister<EntityDataSerializer<?>> DATA_SERIALIZERS = DeferredRegister
            .create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, "goetyawaken");

    public static final EntityDataSerializer<Map<BlockPos, BlockState>> BLOCK_STATE_POS_MAP = new EntityDataSerializer<>() {
        @Override
        public void write(FriendlyByteBuf buffer, Map<BlockPos, BlockState> map) {
            buffer.writeMap(map,
                    (buf, pos) -> buf.writeBlockPos(pos),
                    (buf, state) -> buf.writeVarInt(Block.getId(state)));
        }

        @Override
        public Map<BlockPos, BlockState> read(FriendlyByteBuf buffer) {
            return buffer.readMap(
                    (buf) -> buf.readBlockPos(),
                    (buf) -> Block.stateById(buf.readVarInt()));
        }

        @Override
        public Map<BlockPos, BlockState> copy(Map<BlockPos, BlockState> map) {
            return new HashMap<>(map);
        }
    };

    public static final EntityDataSerializer<List<CompoundTag>> COMPOUND_LIST = new EntityDataSerializer<>() {
        @Override
        public void write(FriendlyByteBuf buffer, List<CompoundTag> list) {
            CompoundTag compound = new CompoundTag();
            compound.put("List", GoetyAwakenNBTUtil.writeCompoundList(list));
            buffer.writeNbt(compound);
        }

        @Override
        public List<CompoundTag> read(FriendlyByteBuf buffer) {
            CompoundTag compound = buffer.readNbt();
            return GoetyAwakenNBTUtil.readCompoundList(compound.getList("List", 10));
        }

        @Override
        public List<CompoundTag> copy(List<CompoundTag> list) {
            return new ArrayList<>(list);
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

    public static void register() {

    }

    static {
        DATA_SERIALIZERS.register("block_state_pos_map", () -> BLOCK_STATE_POS_MAP);
        DATA_SERIALIZERS.register("compound_list", () -> COMPOUND_LIST);
        DATA_SERIALIZERS.register("vector2f", () -> VECTOR_2F);
    }
}
