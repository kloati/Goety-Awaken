package com.k1sak1.goetyawaken.common.entities.ally.Integration;

import com.Polarice3.Goety.common.entities.ally.SlimeServant;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.kyanite.deeperdarker.content.DDBlocks;
import com.kyanite.deeperdarker.content.entities.DDMobType;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

//Based on https://github.com/KyaniteMods/DeeperAndDarker/tree/forge-1.20, Original by kyanite
public class SludgeServant extends SlimeServant {
    public SludgeServant(EntityType<? extends SludgeServant> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.SludgeServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.SludgeServantDamage.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.SludgeServantMovementSpeed.get())
                .add(Attributes.ARMOR, AttributesConfig.SludgeServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.SludgeServantArmorToughness.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.SludgeServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.SludgeServantDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED),
                AttributesConfig.SludgeServantMovementSpeed.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR),
                AttributesConfig.SludgeServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.SludgeServantArmorToughness.get());
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return com.k1sak1.goetyawaken.Config.SLUDGE_SERVANT_LIMIT.get();
    }

    @Override
    public @NotNull MobType getMobType() {
        return DDMobType.SCULK;
    }

    @Override
    protected @NotNull ParticleOptions getParticleType() {
        return new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(DDBlocks.BLOOMING_MOSS_BLOCK.get()));
    }
}
