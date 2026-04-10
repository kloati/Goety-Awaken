package com.k1sak1.goetyawaken.common.items;

import com.k1sak1.goetyawaken.common.entities.projectiles.MirageEyeEntity;
import com.k1sak1.goetyawaken.common.world.structures.ModStructureTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.List;

public class MirageEyeItem extends Item {

    public MirageEyeItem() {
        super(new Properties()
                .stacksTo(64));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        pPlayer.startUsingItem(pHand);
        if (pLevel instanceof ServerLevel serverlevel) {
            BlockPos blockpos = serverlevel.findNearestMapStructure(
                    ModStructureTags.MIRAGE,
                    pPlayer.blockPosition(),
                    100,
                    false);
            if (blockpos != null) {
                MirageEyeEntity eyeofender = MirageEyeEntity.create(pLevel,
                        pPlayer.getX(), pPlayer.getY(0.5D), pPlayer.getZ());
                eyeofender.setItem(itemstack);
                eyeofender.signalTo(blockpos);

                eyeofender.setParticle(ParticleTypes.PORTAL);
                pLevel.gameEvent(GameEvent.PROJECTILE_SHOOT,
                        eyeofender.position(),
                        GameEvent.Context.of(pPlayer));
                pLevel.addFreshEntity(eyeofender);
                pLevel.playSound((Player) null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(),
                        SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 0.5F,
                        0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
                pLevel.levelEvent((Player) null, 1003, pPlayer.blockPosition(), 0);
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                pPlayer.awardStat(Stats.ITEM_USED.get(this));
                pPlayer.swing(pHand, true);
                return InteractionResultHolder.success(itemstack);
            }
        }

        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
            TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("tooltip.goetyawaken.mirage_eye.desc"));
    }
}