package com.k1sak1.goetyawaken.common.items.armor;

import com.Polarice3.Goety.api.items.ISoulRepair;
import com.Polarice3.Goety.init.ModAttributes;
import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.armor.ChampionArmorModel;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ChampionArmorItem extends ArmorItem implements ISoulRepair {

    private static final String TAG_SERVANT_VARIANT = "ServantVariant";
    private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[] {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private static final UUID COOLDOWN_UUID_HELMET = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private static final UUID COOLDOWN_UUID_CHESTPLATE = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901");
    private static final UUID COOLDOWN_UUID_LEGGINGS = UUID.fromString("c3d4e5f6-a7b8-9012-cdef-123456789012");
    private static final UUID COOLDOWN_UUID_BOOTS = UUID.fromString("d4e5f6a7-b8c9-0123-defa-234567890123");
    private static final UUID SPEED_UUID_BOOTS = UUID.fromString("e5f6a7b8-c9d0-1234-efab-345678901234");

    private final float magicResistance;
    private final float fireResistance;
    private final float explosionResistance;
    private final float projectileResistance;
    private final float spellCooldownReduction;
    private final float movementSpeedBonus;

    public ChampionArmorItem(ArmorItem.Type type) {
        super(ChampionArmorMaterial.CHAMPION, type,
                new net.minecraft.world.item.Item.Properties().durability(600).rarity(Rarity.RARE));
        switch (type) {
            case HELMET -> {
                this.magicResistance = 0.10F;
                this.fireResistance = 0.20F;
                this.explosionResistance = 0.25F;
                this.projectileResistance = 0.10F;
                this.spellCooldownReduction = 0.03F;
                this.movementSpeedBonus = 0.0F;
            }
            case CHESTPLATE -> {
                this.magicResistance = 0.15F;
                this.fireResistance = 0.35F;
                this.explosionResistance = 0.25F;
                this.projectileResistance = 0.10F;
                this.spellCooldownReduction = 0.05F;
                this.movementSpeedBonus = 0.0F;
            }
            case LEGGINGS -> {
                this.magicResistance = 0.10F;
                this.fireResistance = 0.20F;
                this.explosionResistance = 0.25F;
                this.projectileResistance = 0.10F;
                this.spellCooldownReduction = 0.04F;
                this.movementSpeedBonus = 0.0F;
            }
            case BOOTS -> {
                this.magicResistance = 0.05F;
                this.fireResistance = 0.25F;
                this.explosionResistance = 0.25F;
                this.projectileResistance = 0.10F;
                this.spellCooldownReduction = 0.03F;
                this.movementSpeedBonus = 0.02F;
            }
            default -> {
                this.magicResistance = 0.0F;
                this.fireResistance = 0.0F;
                this.explosionResistance = 0.0F;
                this.projectileResistance = 0.0F;
                this.spellCooldownReduction = 0.0F;
                this.movementSpeedBonus = 0.0F;
            }
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getAttributeModifiers(slot, stack));

        if (slot == this.getType().getSlot()) {
            if (this.spellCooldownReduction > 0) {
                Attribute cooldownAttribute = getCooldownDiscountAttribute();
                if (cooldownAttribute != null) {
                    UUID uuid = getCooldownUUID();
                    builder.put(cooldownAttribute, new AttributeModifier(
                            uuid,
                            "Champion armor cooldown reduction",
                            this.spellCooldownReduction,
                            AttributeModifier.Operation.ADDITION));
                }
            }

            if (this.type == Type.BOOTS && this.movementSpeedBonus > 0) {
                builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(
                        SPEED_UUID_BOOTS,
                        "Champion boots speed bonus",
                        this.movementSpeedBonus,
                        AttributeModifier.Operation.ADDITION));
            }
        }

        return builder.build();
    }

    private Attribute getCooldownDiscountAttribute() {
        boolean revelationLoaded = ModList.get().isLoaded("revelation") ||
                ModList.get().isLoaded("revelationfix");

        if (revelationLoaded) {
            try {
                var revelationAttributes = Class.forName("com.mega.revelationfix.common.init.ModAttributes");
                var spellCooldownField = revelationAttributes.getField("SPELL_COOLDOWN");
                var spellCooldown = (net.minecraftforge.registries.RegistryObject<Attribute>) spellCooldownField
                        .get(null);
                return spellCooldown.get();
            } catch (Exception e) {
                return ModAttributes.COOLDOWN_DISCOUNT.get();
            }
        } else {
            return ModAttributes.COOLDOWN_DISCOUNT.get();
        }
    }

    private UUID getCooldownUUID() {
        return switch (this.type) {
            case HELMET -> COOLDOWN_UUID_HELMET;
            case CHESTPLATE -> COOLDOWN_UUID_CHESTPLATE;
            case LEGGINGS -> COOLDOWN_UUID_LEGGINGS;
            case BOOTS -> COOLDOWN_UUID_BOOTS;
            default -> UUID.randomUUID();
        };
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (!level.isClientSide && entity instanceof Player) {
            CompoundTag tag = stack.getOrCreateTag();
            if (!tag.contains(TAG_SERVANT_VARIANT)) {
                if (RandomSource.create().nextBoolean()) {
                    tag.putBoolean(TAG_SERVANT_VARIANT, true);
                } else {
                    tag.putBoolean(TAG_SERVANT_VARIANT, false);
                }
            }
        }
    }

    private boolean isServantVariant(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(TAG_SERVANT_VARIANT);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (isServantVariant(stack)) {
            return GoetyAwaken.MODID + ":textures/entity/undead/skeleton/vanguard_champion_servant.png";
        }
        return GoetyAwaken.MODID + ":textures/entity/undead/skeleton/vanguard_champion.png";
    }

    @Override
    public int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
        int level = super.getEnchantmentLevel(stack, enchantment);
        if (enchantment instanceof ProtectionEnchantment && level > 0) {
            return level + 1;
        }
        return level;
    }

    @Override
    public Map<Enchantment, Integer> getAllEnchantments(ItemStack stack) {
        Map<Enchantment, Integer> enchantments = super.getAllEnchantments(stack);
        enchantments.forEach((enchantment, level) -> {
            if (enchantment instanceof ProtectionEnchantment) {
                enchantments.put(enchantment, level + 1);
            }
        });
        return enchantments;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack,
                    EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                EntityModelSet modelSet = Minecraft.getInstance().getEntityModels();
                ModelPart root = modelSet.bakeLayer(ChampionArmorModel.CHAMPION_ARMOR_OUTER_LAYER);
                ChampionArmorModel armorModel = new ChampionArmorModel(root);
                armorModel.head.visible = false;
                armorModel.body.visible = false;
                armorModel.rightArm.visible = false;
                armorModel.leftArm.visible = false;
                armorModel.rightLeg.visible = false;
                armorModel.leftLeg.visible = false;
                armorModel.hat.visible = false;
                switch (equipmentSlot) {
                    case HEAD:
                        armorModel.head.visible = true;
                        break;
                    case CHEST:
                        armorModel.body.visible = true;
                        armorModel.rightArm.visible = true;
                        armorModel.leftArm.visible = true;
                        break;
                    case LEGS:
                        armorModel.rightLeg.visible = true;
                        armorModel.leftLeg.visible = true;
                        armorModel.right_boot.visible = false;
                        armorModel.left_boot.visible = false;
                        break;
                    case FEET:
                        armorModel.rightLeg.visible = true;
                        armorModel.leftLeg.visible = true;
                        armorModel.right_plate.visible = false;
                        armorModel.left_plate.visible = false;
                        break;
                }

                armorModel.young = original.young;
                armorModel.crouching = original.crouching;
                armorModel.riding = original.riding;
                armorModel.rightArmPose = original.rightArmPose;
                armorModel.leftArmPose = original.leftArmPose;

                return armorModel;
            }
        });
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.goetyawaken.holdShift").withStyle(ChatFormatting.GRAY,
                ChatFormatting.ITALIC));

        if (org.lwjgl.glfw.GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), 340) == 1 ||
                org.lwjgl.glfw.GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), 344) == 1) {
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("tooltip.goetyawaken.specialEffect").withStyle(ChatFormatting.GOLD));
            if (magicResistance > 0) {
                tooltip.add(Component.translatable("tooltip.goetyawaken.armor.reduce_magic_damage",
                        String.format("%.0f%%", magicResistance * 100)).withStyle(ChatFormatting.GREEN));
            }
            if (fireResistance > 0) {
                tooltip.add(Component.translatable("tooltip.goetyawaken.armor.reduce_fire_damage",
                        String.format("%.0f%%", fireResistance * 100)).withStyle(ChatFormatting.GREEN));
            }
            if (explosionResistance > 0) {
                tooltip.add(Component.translatable("tooltip.goetyawaken.armor.reduce_explosion_damage",
                        String.format("%.0f%%", explosionResistance * 100)).withStyle(ChatFormatting.GREEN));
            }
            if (projectileResistance > 0) {
                tooltip.add(Component.translatable("tooltip.goetyawaken.armor.reduce_projectile_damage",
                        String.format("%.0f%%", projectileResistance * 100)).withStyle(ChatFormatting.GREEN));
            }
            if (spellCooldownReduction > 0) {
                tooltip.add(Component.translatable("tooltip.goetyawaken.armor.spell_cooldown_reduction",
                        String.format("%.2f", spellCooldownReduction)).withStyle(ChatFormatting.AQUA));
            }
            if (movementSpeedBonus > 0) {
                tooltip.add(Component.translatable("tooltip.goetyawaken.armor.movement_speed",
                        String.format("%.2f", movementSpeedBonus)).withStyle(ChatFormatting.YELLOW));
            }

            tooltip.add(Component.translatable("tooltip.goetyawaken.armor.protection_enchantment_level")
                    .withStyle(ChatFormatting.DARK_PURPLE));

            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("tooltip.goetyawaken.setEffect").withStyle(ChatFormatting.LIGHT_PURPLE));
            tooltip.add(
                    Component.translatable("tooltip.goetyawaken.champion_set.2piece").withStyle(ChatFormatting.GRAY));
            tooltip.add(
                    Component.translatable("tooltip.goetyawaken.champion_set.4piece").withStyle(ChatFormatting.GRAY));
        }
    }

    public float getMagicResistance() {
        return magicResistance;
    }

    public float getFireResistance() {
        return fireResistance;
    }

    public float getExplosionResistance() {
        return explosionResistance;
    }

    public float getProjectileResistance() {
        return projectileResistance;
    }

    public float getSpellCooldownReduction() {
        return spellCooldownReduction;
    }

    public float getMovementSpeedBonus() {
        return movementSpeedBonus;
    }

    public static EquipmentSlot[] getArmorSlots() {
        return ARMOR_SLOTS;
    }

    @Override
    public void repairTick(ItemStack stack, Entity entityIn, boolean isSelected) {
        com.Polarice3.Goety.utils.ItemHelper.repairTick(stack, entityIn, isSelected);
    }
}
