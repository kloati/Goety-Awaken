// package com.k1sak1.goetyawaken.common.compat.tetra;

// import net.minecraft.world.entity.player.Player;
// import net.minecraft.world.item.ItemStack;
// import net.minecraftforge.api.distmarker.Dist;
// import net.minecraftforge.api.distmarker.OnlyIn;
// import se.mickelus.tetra.effect.ItemEffect;
// import se.mickelus.tetra.gui.stats.getter.IStatGetter;
// import se.mickelus.tetra.gui.stats.getter.LabelGetterBasic;
// import se.mickelus.tetra.gui.stats.getter.StatGetterEffectLevel;

// public class BlueMoonEffect {

// public static final ItemEffect BLUE_MOON =
// ItemEffect.get("goetyawaken.blue_moon");

// @OnlyIn(Dist.CLIENT)
// public static void init() {
// try {
// StatGetterEffectLevel levelGetter = new StatGetterEffectLevel(BLUE_MOON,
// 0.5D);

// IStatGetter[] stats = new IStatGetter[] { levelGetter };

// se.mickelus.tetra.gui.stats.bar.GuiStatBar effectBar = new
// se.mickelus.tetra.gui.stats.bar.GuiStatBar(
// 0, 0, 59,
// "goetyawaken.effect.blue_moon.name",
// 0.0D, 4.0,
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

// double damageMultiplier = 1.0 + (levelValue - 1) * 0.5;

// return String.format("%.1fx %s", damageMultiplier, getDamageLabel());
// }

// @Override
// public boolean hasExtendedTooltip(Player player, ItemStack itemStack) {
// return true;
// }

// @Override
// public String getTooltipExtension(Player player, ItemStack itemStack) {
// return net.minecraft.client.resources.language.I18n
// .get("goetyawaken.effect.blue_moon.tooltip_extended");
// }

// private String getDamageLabel() {
// return
// net.minecraft.client.resources.language.I18n.get("goetyawaken.effect.blue_moon.damage");
// }
// };
// }
// }