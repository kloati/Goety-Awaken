package com.k1sak1.goetyawaken.common.items.magic;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.items.magic.DarkStaff;
import com.Polarice3.Goety.common.items.magic.DarkWand;
import com.k1sak1.goetyawaken.client.renderer.item.PotatoStaffItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class PotatoStaff extends DarkStaff {
    public PotatoStaff() {
        super(6.0D, SpellType.WILD);
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        pTarget.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0, false, false),
                pAttacker);
        return super.hurtEnemy(pStack, pTarget, pAttacker);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        InteractionResult result = super.useOn(pContext);
        Level level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();

        if (result.consumesAction()) {
            return result;
        }

        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof ComposterBlock) {
            int currentLevel = state.getValue(ComposterBlock.LEVEL);
            int maxLevel = 7;
            if (currentLevel < maxLevel) {
                if (!level.isClientSide) {
                    level.setBlock(pos, state.setValue(ComposterBlock.LEVEL, currentLevel + 1), 3);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return result;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new DarkWand.DarkWandClient() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new PotatoStaffItemRenderer();
            }
        });
    }
}
