package com.k1sak1.goetyawaken.common.blocks.entity.vault;

import com.Polarice3.Goety.utils.PlayerDetector;
import com.k1sak1.goetyawaken.common.items.ModItems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record VaultConfig(ResourceLocation lootTable, double activationRange, double deactivationRange,
                ItemStack keyItem, Optional<ResourceLocation> overrideLootTableToDisplay, PlayerDetector playerDetector,
                PlayerDetector.EntitySelector entitySelector) {
        static final String CONFIG_KEY = "config";
        public static VaultConfig DEFAULT = new VaultConfig();
        public static Codec<VaultConfig> CODEC = RecordCodecBuilder.create(
                        instance -> instance.group(
                                        ResourceLocation.CODEC.optionalFieldOf("loot_table", DEFAULT.lootTable())
                                                        .forGetter(VaultConfig::lootTable),
                                        Codec.DOUBLE.optionalFieldOf("activation_range", DEFAULT.activationRange())
                                                        .forGetter(VaultConfig::activationRange),
                                        Codec.DOUBLE.optionalFieldOf("deactivation_range", DEFAULT.deactivationRange())
                                                        .forGetter(VaultConfig::deactivationRange),
                                        createOptionalCodec("key_item").forGetter(VaultConfig::keyItem),
                                        ResourceLocation.CODEC.optionalFieldOf("override_loot_table_to_display")
                                                        .forGetter(VaultConfig::overrideLootTableToDisplay))
                                        .apply(instance, VaultConfig::new));

        public static MapCodec<ItemStack> createOptionalCodec(String fieldName) {
                return ItemStack.CODEC.optionalFieldOf(fieldName)
                                .xmap(optional -> optional.orElse(ItemStack.EMPTY),
                                                stack -> stack.isEmpty() ? Optional.empty() : Optional.of(stack));
        }

        public VaultConfig() {
                this(new ResourceLocation("goetyawaken:gameplay/vault_reward"), 4.0, 4.5,
                                new ItemStack(ModItems.CURSED_VAULT_KEY.get()),
                                Optional.empty(),
                                PlayerDetector.INCLUDING_CREATIVE_PLAYERS,
                                PlayerDetector.EntitySelector.SELECT_FROM_LEVEL);
        }

        public VaultConfig(ResourceLocation lootTable, double activationRange, double deactivationRange,
                        ItemStack keyItem, Optional<ResourceLocation> overrideLootTableToDisplay) {
                this(lootTable, activationRange, deactivationRange, keyItem, overrideLootTableToDisplay,
                                DEFAULT.playerDetector(), DEFAULT.entitySelector());
        }

        private DataResult<VaultConfig> validate() {
                return this.activationRange > this.deactivationRange
                                ? DataResult.error(() -> "Activation range must (" + this.activationRange
                                                + ") be less or equal to deactivation range (" + this.deactivationRange
                                                + ")")
                                : DataResult.success(this);
        }

        public static VaultConfig createDefaultWithKey() {
                return new VaultConfig(new ResourceLocation("goetyawaken:gameplay/vault_reward"), 4.0, 4.5,
                                new ItemStack(ModItems.CURSED_VAULT_KEY.get()),
                                Optional.empty(),
                                PlayerDetector.INCLUDING_CREATIVE_PLAYERS,
                                PlayerDetector.EntitySelector.SELECT_FROM_LEVEL);
        }
}
