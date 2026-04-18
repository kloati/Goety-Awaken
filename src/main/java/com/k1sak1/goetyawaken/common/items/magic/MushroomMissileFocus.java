package com.k1sak1.goetyawaken.common.items.magic;

import com.Polarice3.Goety.common.items.magic.MagicFocus;
import com.k1sak1.goetyawaken.common.magic.spells.MushroomMissileSpell;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class MushroomMissileFocus extends MagicFocus {
    public MushroomMissileFocus() {
        super(new MushroomMissileSpell());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag.contains("PotionEffect")) {
                CompoundTag potionTag = tag.getCompound("PotionEffect");
                String effectName = potionTag.getString("EffectName");
                int duration = potionTag.getInt("Duration");
                int amplifier = potionTag.getInt("Amplifier");
                Component effectComponent;
                try {
                    ResourceLocation effectLocation = new ResourceLocation(effectName);
                    String translationKey = "effect." + effectLocation.getNamespace() + "." + effectLocation.getPath();
                    effectComponent = Component.translatable(translationKey);
                } catch (Exception e) {
                    effectComponent = Component.translatable(effectName);
                }

                Component effectText = Component.translatable("potion.withDuration",
                        effectComponent,
                        Component.literal(String.format("%d:%02d", duration / 20 / 60, (duration / 20) % 60)))
                        .withStyle(ChatFormatting.GRAY);

                if (amplifier > 0) {
                    effectText = Component.translatable("potion.withAmplifier",
                            effectText,
                            Component.translatable("potion.potency." + amplifier))
                            .withStyle(ChatFormatting.GRAY);
                }

                tooltip.add(Component.translatable("item.goetyawaken.shulker_missile_focus.stored_effect")
                        .append(": ").append(effectText));
            }
        }
    }
}