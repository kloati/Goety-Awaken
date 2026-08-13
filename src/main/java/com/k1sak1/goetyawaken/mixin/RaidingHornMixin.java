package com.k1sak1.goetyawaken.mixin;

import com.Polarice3.Goety.common.items.RaidingHorn;
import com.k1sak1.goetyawaken.common.events.eliteassault.EliteAssaultSpawner;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(RaidingHorn.class)
public class RaidingHornMixin {

    @Inject(method = "finishUsingItem", at = @At("RETURN"), cancellable = true)
    private void goetyawaken$onFinishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving,
            CallbackInfoReturnable<ItemStack> cir) {
        if (worldIn instanceof ServerLevel serverWorld && entityLiving instanceof ServerPlayer player) {
            if (serverWorld.dimension() == Level.OVERWORLD
                    && !serverWorld.isVillage(entityLiving.blockPosition())
                    && player.hasEffect(MobEffects.BAD_OMEN)) {
                ResourceLocation bossType = determineBossType(serverWorld, player);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                EliteAssaultSpawner.triggerAssault(player, bossType);
                cir.setReturnValue(stack);
            }
        }
    }

    @Inject(method = "appendHoverText", at = @At("RETURN"))
    private void goetyawaken$onAppendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip,
            TooltipFlag flagIn, CallbackInfo ci) {
        tooltip.add(Component.translatable("info.goetyawaken.raiding_horn").withStyle(ChatFormatting.GRAY));
    }

    private static ResourceLocation determineBossType(ServerLevel level, ServerPlayer player) {
        BlockPos pos = player.blockPosition();

        if (isInStructure(level, pos, new ResourceLocation("goetyawaken", "arch_illusioner_keep"))) {
            return new ResourceLocation("goetyawaken", "arch_illusioner");
        }

        if (isInStructure(level, pos, new ResourceLocation("goetyawaken", "ominous_castle"))) {
            return new ResourceLocation("goetyawaken", "ruby_sorcerer");
        }

        if (level.getBiome(pos).is(BiomeTags.IS_MOUNTAIN)
                || isInStructure(level, pos, new ResourceLocation("goety", "wind_shrine"))) {
            return new ResourceLocation("goetyawaken", "hostile_rampart_captain");
        }

        return null;
    }

    private static boolean isInStructure(ServerLevel level, BlockPos pos, ResourceLocation structureId) {
        Structure structure = level.registryAccess().registryOrThrow(Registries.STRUCTURE).get(structureId);
        if (structure != null) {
            StructureStart start = level.structureManager().getStructureWithPieceAt(pos, structure);
            return start.isValid();
        }
        return false;
    }
}
