package com.k1sak1.goetyawaken.mixin;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.NecroBrazierBlock;
import com.Polarice3.Goety.common.blocks.entities.NecroBrazierBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.SoulCandlestickBlockEntity;
import com.Polarice3.Goety.common.crafting.BrazierRecipe;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import com.k1sak1.goetyawaken.api.IAcceleratedSoulCandle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = NecroBrazierBlockEntity.class, remap = false)
public abstract class NecroBrazierBlockEntityMixin extends BlockEntity {

    public NecroBrazierBlockEntityMixin(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Shadow
    public int currentTime;
    @Shadow
    public List<SoulCandlestickBlockEntity> candlestickBlockEntityList;
    @Shadow
    public BrazierRecipe recipe;

    @Shadow
    private void findCandlesticks() {
    }

    @Shadow
    private void makeParticles() {
    }

    @Shadow
    protected boolean activate(Level level) {
        return false;
    }

    @Shadow
    public BrazierRecipe getRecipe() {
        return null;
    }

    @Shadow
    public void stopBrazier(boolean finished) {
    }

    @Shadow
    public void updateRecipe(Level level) {
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void goetyawaken$tick(CallbackInfo ci) {
        if (this.level != null) {
            boolean flag = this.level.getBiome(this.getBlockPos()).is(Biomes.DEEP_DARK);
            if (flag) {
                this.findCandlesticks();
                if (!this.level.isClientSide) {
                    if (this.level.random.nextFloat() < 0.3F) {
                        if (this.level.random.nextFloat() < 0.17F) {
                            ModNetwork.sendToALL(new SPlayWorldSoundPacket(this.getBlockPos(),
                                    SoundEvents.FURNACE_FIRE_CRACKLE, 0.5F + this.level.random.nextFloat(),
                                    this.level.random.nextFloat() * 0.7F + this.level.random.nextFloat()));
                        }
                    }
                }
                if (!this.candlestickBlockEntityList.isEmpty()) {
                    BrazierRecipe recipe = this.getRecipe();
                    double d0 = (double) this.getBlockPos().getX() + this.level.random.nextDouble();
                    double d1 = (double) this.getBlockPos().getY() + 0.5D + this.level.random.nextDouble();
                    double d2 = (double) this.getBlockPos().getZ() + this.level.random.nextDouble();
                    if (!this.level.isClientSide) {
                        ServerLevel serverWorld = (ServerLevel) this.level;
                        this.makeParticles();
                        if (this.activate(this.level)) {
                            if (recipe != null) {
                                for (int p = 0; p < 2; ++p) {
                                    serverWorld.sendParticles(ModParticleTypes.SMALL_NECRO_FIRE.get(), d0,
                                            this.getBlockPos().getY() + 0.5F, d2, 1, 0, 0, 0, 0);
                                    serverWorld.sendParticles(ParticleTypes.SMOKE, d0, d1, d2, 0, 0.0D, 5.0E-4D, 0.0D,
                                            0.5F);
                                    serverWorld.sendParticles(ModParticleTypes.NECRO_EFFECT.get(), d0, d1, d2, 1, 0.0F,
                                            0.0F, 0.0F, 0.0F);
                                }
                                for (SoulCandlestickBlockEntity candlestickBlock : this.candlestickBlockEntityList) {
                                    if (candlestickBlock.getSouls() > 0) {
                                        candlestickBlock.drainSouls(1, this.getBlockPos());
                                        if (candlestickBlock instanceof IAcceleratedSoulCandle accelerated) {
                                            this.currentTime += accelerated.getAccelerationFactor();
                                        } else {
                                            this.currentTime++;
                                        }
                                    }
                                }
                                if (this.currentTime == 1) {
                                    ModNetwork.sendToALL(
                                            new SPlayWorldSoundPacket(this.getBlockPos(), SoundEvents.BLAZE_AMBIENT,
                                                    1.0F + this.level.random.nextFloat() * 0.1F, 0.9F));
                                }
                                if (this.level.getGameTime() % 20 == 0) {
                                    ModNetwork.sendToALL(new SPlayWorldSoundPacket(this.getBlockPos(),
                                            SoundEvents.FIRE_AMBIENT, 1.0F + this.level.random.nextFloat(),
                                            this.level.random.nextFloat() * 0.7F + 0.3F));
                                    ModNetwork.sendToALL(new SPlayWorldSoundPacket(this.getBlockPos(),
                                            SoundEvents.SCULK_CATALYST_BLOOM, 1.0F,
                                            this.level.random.nextFloat() * 0.1F + 0.9F));
                                    serverWorld.sendParticles(ParticleTypes.SCULK_SOUL,
                                            (double) this.getBlockPos().getX() + 0.5D,
                                            (double) this.getBlockPos().getY() + 1.15D,
                                            (double) this.getBlockPos().getZ() + 0.5D, 2, 0.2D, 0.0D, 0.2D, 0.0D);
                                }
                                if (this.currentTime >= recipe.getSoulCost()) {
                                    this.stopBrazier(true);
                                } else {
                                    this.updateRecipe(this.level);
                                }
                            }
                        }
                    }
                } else {
                    if (!this.level.isClientSide) {
                        this.stopBrazier(false);
                    }
                }
            } else {
                if (!this.level.isClientSide) {
                    this.stopBrazier(false);
                }
            }
            this.level.setBlock(this.getBlockPos(),
                    ((NecroBrazierBlockEntity) (Object) this).getBlockState().setValue(NecroBrazierBlock.LIT, flag), 3);
        }

        ci.cancel();
    }
}
