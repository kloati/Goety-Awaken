package com.k1sak1.goetyawaken.mixin;

import com.Polarice3.Goety.client.render.PrismaBeamRenderer;
import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = PrismaBeamRenderer.class, remap = false)
public class PrismaBeamRendererMixin {

    @Unique
    private static final ResourceLocation TOXIFIN_BEAM_LOCATION = GoetyAwaken
            .location("textures/entity/toxifin_beam.png");

    @Unique
    private static boolean isCastingWithPotatoStaff() {
        Player player = Minecraft.getInstance().player;
        return player != null && player.getUseItem().is(ModItems.POTATO_STAFF.get());
    }

    @ModifyArg(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
            remap = true
        ),
        index = 0,
        remap = false
    )
    private static RenderType modifyBeamRenderType(RenderType original) {
        if (isCastingWithPotatoStaff()) {
            return RenderType.entityCutoutNoCull(TOXIFIN_BEAM_LOCATION);
        }
        return original;
    }
}
