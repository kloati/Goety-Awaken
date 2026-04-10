// package com.k1sak1.goetyawaken.common.compat.tetra;

// import com.Polarice3.Goety.common.effects.GoetyEffects;
// import net.minecraft.world.effect.MobEffectInstance;
// import net.minecraft.world.entity.LivingEntity;
// import net.minecraft.world.entity.player.Player;
// import net.minecraft.world.item.ItemStack;
// import net.minecraft.world.phys.Vec3;
// import net.minecraftforge.api.distmarker.Dist;
// import net.minecraftforge.api.distmarker.OnlyIn;
// import net.minecraftforge.event.entity.living.LivingHurtEvent;
// import net.minecraftforge.eventbus.api.SubscribeEvent;
// import se.mickelus.tetra.effect.ItemEffect;
// import se.mickelus.tetra.gui.stats.getter.IStatGetter;
// import se.mickelus.tetra.gui.stats.getter.LabelGetterBasic;
// import se.mickelus.tetra.gui.stats.getter.StatFormat;
// import se.mickelus.tetra.gui.stats.getter.StatGetterEffectEfficiency;
// import se.mickelus.tetra.gui.stats.getter.StatGetterEffectLevel;

// public class VoidStrikeEffect {

// public static final ItemEffect VOID_STRIKE =
// ItemEffect.get("goetyawaken.void_strike");

// @OnlyIn(Dist.CLIENT)
// public static void init() {
// try {
// StatGetterEffectLevel levelGetter = new StatGetterEffectLevel(VOID_STRIKE,
// 1.0D);
// StatGetterEffectEfficiency efficiencyGetter = new
// StatGetterEffectEfficiency(VOID_STRIKE, 2.0D);

// IStatGetter[] stats = new IStatGetter[] { levelGetter, efficiencyGetter };

// se.mickelus.tetra.gui.stats.bar.GuiStatBar effectBar = new
// se.mickelus.tetra.gui.stats.bar.GuiStatBar(
// 0, 0, 59,
// "goetyawaken.effect.void_strike.name",
// 0.0D, 25.0,
// false,
// levelGetter,
// LabelGetterBasic.decimalLabel,
// createTooltipGetter(stats));

// se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui.addBar(effectBar);
// se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui.addBar(effectBar);
// } catch (Throwable throwable) {

// throwable.printStackTrace();
// }
// }

// @OnlyIn(Dist.CLIENT)
// private static se.mickelus.tetra.gui.stats.getter.ITooltipGetter
// createTooltipGetter(IStatGetter[] stats) {
// return new se.mickelus.tetra.gui.stats.getter.ITooltipGetter() {
// @Override
// public String getTooltipBase(Player player, ItemStack itemStack) {
// double levelValue = stats[0].getValue(player, itemStack);
// double efficiencyValue = stats[1].getValue(player, itemStack);

// double triggerChance = levelValue * 5.0;
// double duration = efficiencyValue * 2.0;
// int amplifier = Math.min((int) levelValue - 1, 3);

// return String.format("%.0f%% %s，%.0fs %s， %d",
// triggerChance, getChanceLabel(),
// duration / 2, getDurationLabel(),
// amplifier + 1, getLevelLabel());
// }

// @Override
// public boolean hasExtendedTooltip(Player player, ItemStack itemStack) {
// return false;
// }

// private String getChanceLabel() {
// return
// net.minecraft.client.resources.language.I18n.get("goetyawaken.effect.void_strike.chance");
// }

// private String getDurationLabel() {
// return
// net.minecraft.client.resources.language.I18n.get("goetyawaken.effect.void_strike.duration");
// }

// private String getLevelLabel() {
// return
// net.minecraft.client.resources.language.I18n.get("goetyawaken.effect.void_strike.level");
// }
// };
// }

// @SubscribeEvent
// public void onLivingHurt(LivingHurtEvent event) {
// if (!TetraSafeClass.isTetraLoaded()) {
// return;
// }

// if (!(event.getSource().getEntity() instanceof Player attacker)) {
// return;
// }

// ItemStack itemStack = attacker.getMainHandItem();

// if (!(itemStack.getItem() instanceof
// se.mickelus.tetra.items.modular.ModularItem modularItem)) {
// return;
// }

// int level = modularItem.getEffectLevel(itemStack, VOID_STRIKE);
// if (level <= 0) {
// return;
// }

// float triggerChance = level * 0.05f;

// if (attacker.getRandom().nextFloat() >= triggerChance) {
// return;
// }

// LivingEntity target = event.getEntity();

// applyVoidTouched(target, level, modularItem.getEffectEfficiency(itemStack,
// VOID_STRIKE), attacker);
// }

// private void applyVoidTouched(LivingEntity target, int level, float
// efficiency, LivingEntity source) {
// int duration = (int) (efficiency * 2.0f * 20.0f);
// int amplifier = Math.min(level - 1, 3);

// MobEffectInstance existingEffect =
// target.getEffect(GoetyEffects.VOID_TOUCHED.get());
// if (existingEffect != null) {
// amplifier = Math.min(existingEffect.getAmplifier() + 1, 3);
// duration = Math.max(duration, existingEffect.getDuration());
// }

// MobEffectInstance effectInstance = new MobEffectInstance(
// GoetyEffects.VOID_TOUCHED.get(),
// duration,
// amplifier);

// target.addEffect(effectInstance);
// }
// }
