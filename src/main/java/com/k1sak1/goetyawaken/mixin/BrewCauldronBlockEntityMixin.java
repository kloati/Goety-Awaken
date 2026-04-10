package com.k1sak1.goetyawaken.mixin;

import com.Polarice3.Goety.common.blocks.entities.BrewCauldronBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.SoulCandlestickBlockEntity;
import com.k1sak1.goetyawaken.api.IAcceleratedSoulCandle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = BrewCauldronBlockEntity.class, remap = false)
public abstract class BrewCauldronBlockEntityMixin extends BlockEntity {

    public BrewCauldronBlockEntityMixin(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Shadow
    public int soulTime;
    @Shadow
    public boolean isBrewing;
    @Shadow
    public BrewCauldronBlockEntity.Mode mode;

    @Shadow
    public int getBrewCost() {
        return 0;
    }

    @Shadow
    public List<SoulCandlestickBlockEntity> candlestickBlockEntityList;

    @Shadow
    public void markUpdated() {
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void goetyawaken$tick(CallbackInfo ci) {
        if (this.level != null && !this.level.isClientSide) {
            if (this.isBrewing && this.mode == BrewCauldronBlockEntity.Mode.BREWING
                    && this.soulTime < this.getBrewCost()) {
                if (!this.candlestickBlockEntityList.isEmpty()) {
                    for (SoulCandlestickBlockEntity candlestick : this.candlestickBlockEntityList) {
                        if (candlestick instanceof IAcceleratedSoulCandle accelerated) {
                            int factor = accelerated.getAccelerationFactor();
                            if (factor > 1) {
                                this.soulTime += (factor - 1);
                                this.markUpdated();
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
