package com.k1sak1.goetyawaken.mixin;

import com.Polarice3.Goety.common.research.Research;
import com.Polarice3.Goety.utils.SEHelper;
import com.k1sak1.goetyawaken.common.advancements.ModCriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SEHelper.class, remap = false)
public class SEHelperMixin {

    @Inject(method = "addResearch", at = @At("RETURN"), remap = false)
    private static void goetyawaken_onAddResearch(Player player, Research research,
            CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && player instanceof ServerPlayer serverPlayer
                && "royal".equals(research.getId())) {
            ModCriteriaTriggers.ROYAL_RESEARCH_COMPLETED.trigger(serverPlayer);
        }
    }
}
