package com.k1sak1.goetyawaken.common.items.armor;

import com.Polarice3.Goety.common.entities.projectiles.SnapFungus;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.armor.MushroomHatModel;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.EnumMap;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.TooltipFlag;

@Mod.EventBusSubscriber(modid = GoetyAwaken.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MushroomHatItem extends ArmorItem {

    private static final ArmorMaterial MUSHROOM_HAT_ARMOR_MATERIAL = new ArmorMaterial() {
        private static final EnumMap<ArmorItem.Type, Integer> HEALTH_FUNCTION_FOR_TYPE = Util
                .make(new EnumMap<>(ArmorItem.Type.class), (p_266653_) -> {
                    p_266653_.put(ArmorItem.Type.BOOTS, 0);
                    p_266653_.put(ArmorItem.Type.LEGGINGS, 0);
                    p_266653_.put(ArmorItem.Type.CHESTPLATE, 0);
                    p_266653_.put(ArmorItem.Type.HELMET, 5);
                });

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return 25 * HEALTH_FUNCTION_FOR_TYPE.get(type);
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return HEALTH_FUNCTION_FOR_TYPE.get(type);
        }

        @Override
        public int getEnchantmentValue() {
            return 15;
        }

        @Override
        public net.minecraft.sounds.SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_GENERIC;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }

        @Override
        public String getName() {
            return "mushroom_hat";
        }

        @Override
        public float getToughness() {
            return 3.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public MushroomHatItem() {
        super(MUSHROOM_HAT_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
                new Item.Properties().durability(2000).rarity(Rarity.EPIC));
    }

    @SubscribeEvent
    public static void onMobEffectApplicable(MobEffectEvent.Applicable event) {
        LivingEntity entity = event.getEntity();
        if (entity != null) {
            ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
            if (helmet.getItem() instanceof MushroomHatItem) {
                MobEffectInstance effectInstance = event.getEffectInstance();
                if (effectInstance != null) {
                    if (effectInstance.getEffect() == MobEffects.POISON) {
                        event.setResult(Event.Result.DENY);
                    } else if (GoetyEffects.ACID_VENOM.get() != null &&
                            effectInstance.getEffect() == GoetyEffects.ACID_VENOM.get()) {
                        event.setResult(Event.Result.DENY);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();

        if (!level.isClientSide()) {
            ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
            if (helmet.getItem() instanceof MushroomHatItem) {
                if (entity.tickCount % 200 == 0) {
                    reduceRandomNegativeEffectDuration(entity);
                }

                if (entity.tickCount % 60 == 0) {
                    shootSnapFungus(level, entity);
                }
            }
        }
    }

    private static void shootSnapFungus(Level level, LivingEntity entity) {
        List<LivingEntity> nearbyEnemies = level.getEntitiesOfClass(LivingEntity.class,
                entity.getBoundingBox().inflate(16.0D),
                target -> target != entity &&
                        !(target instanceof net.minecraft.world.entity.player.Player) &&
                        target.isAlive() &&
                        entity.hasLineOfSight(target) &&
                        entity.canAttack(target) &&
                        !com.Polarice3.Goety.utils.MobUtil.areAllies(entity, target) &&
                        (target instanceof net.minecraft.world.entity.Mob mob && mob.getTarget() == entity));

        if (!nearbyEnemies.isEmpty()) {
            if (level.random.nextDouble() < 0.1) {
                int blastCount = 1 + level.random.nextInt(3);
                for (int i = 0; i < blastCount; i++) {
                    com.Polarice3.Goety.utils.MobUtil.throwBlastFungus(entity, level);
                }
                entity.playSound(SoundEvents.FIREWORK_ROCKET_LAUNCH, 0.5F,
                        0.4F / (level.random.nextFloat() * 0.4F + 0.8F));
            } else {
                LivingEntity target = nearbyEnemies.get(level.random.nextInt(nearbyEnemies.size()));
                SnapFungus snapFungus = new SnapFungus(entity, level);
                snapFungus.setOwner(entity);
                double dx = target.getX() - entity.getX();
                double dy = target.getEyeY() - entity.getEyeY();
                double dz = target.getZ() - entity.getZ();
                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                double speed = 1.5D;
                snapFungus.shoot(dx / distance * speed, dy / distance * speed, dz / distance * speed, 1.5F, 0.0F);
                level.addFreshEntity(snapFungus);
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.CRIT, entity.getX(), entity.getEyeY(), entity.getZ(), 10,
                            0.5,
                            0.5, 0.5, 0.05);
                }

                entity.playSound(SoundEvents.SNOWBALL_THROW, 0.5F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));
            }
        }
    }

    private static void reduceRandomNegativeEffectDuration(LivingEntity entity) {
        List<MobEffectInstance> negativeEffects = entity.getActiveEffects().stream()
                .filter(effect -> !effect.getEffect().isBeneficial())
                .filter(effect -> effect.getDuration() > 1)
                .toList();

        if (!negativeEffects.isEmpty()) {
            Random random = new Random();
            MobEffectInstance randomEffect = negativeEffects.get(random.nextInt(negativeEffects.size()));
            int newDuration = randomEffect.getDuration() / 2;
            entity.removeEffect(randomEffect.getEffect());
            entity.addEffect(new MobEffectInstance(randomEffect.getEffect(), newDuration, randomEffect.getAmplifier(),
                    randomEffect.isAmbient(), randomEffect.isVisible(), randomEffect.showIcon()));
        }
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return GoetyAwaken.MODID + ":textures/models/armor/mushroom_hat_layer_1.png";
    }

    @Override
    public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltipComponents,
            TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("item.goetyawaken.mushroom_hat.tooltip")
                .withStyle(ChatFormatting.DARK_PURPLE));
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        int maxDurability = stack.getMaxDamage();
        int maxDamagePerHit = Math.max(1, maxDurability / 4);
        if (amount > maxDamagePerHit) {
            return maxDamagePerHit;
        }
        return amount;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public net.minecraft.client.model.HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity,
                    ItemStack itemStack, EquipmentSlot equipmentSlot,
                    net.minecraft.client.model.HumanoidModel<?> original) {
                EntityModelSet modelSet = Minecraft.getInstance().getEntityModels();
                net.minecraft.client.model.geom.ModelPart root = modelSet.bakeLayer(MushroomHatModel.LAYER_LOCATION);
                MushroomHatModel armorModel = new MushroomHatModel(root);
                armorModel.young = original.young;
                armorModel.crouching = original.crouching;
                armorModel.riding = original.riding;
                armorModel.rightArmPose = original.rightArmPose;
                armorModel.leftArmPose = original.leftArmPose;
                armorModel.head.visible = equipmentSlot == EquipmentSlot.HEAD;
                armorModel.body.visible = false;
                armorModel.leftArm.visible = false;
                armorModel.rightArm.visible = false;
                armorModel.leftLeg.visible = false;
                armorModel.rightLeg.visible = false;

                return armorModel;
            }
        });
    }
}