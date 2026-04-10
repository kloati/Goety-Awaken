// package com.k1sak1.goetyawaken.common.compat.tetra;

// import net.minecraftforge.api.distmarker.Dist;
// import net.minecraftforge.common.MinecraftForge;
// import net.minecraftforge.fml.loading.FMLEnvironment;

// public class TetraCompat {

// public static void initIfPresent() {
// if (!TetraSafeClass.isTetraLoaded()) {
// return;
// }

// initEffects();

// registerEventHandlers();
// }

// private static void initEffects() {
// VoidStrikeEffect.init();
// BlueMoonEffect.init();
// }

// public static void registerEventHandlers() {
// if (!TetraSafeClass.isTetraLoaded()) {
// return;
// }

// MinecraftForge.EVENT_BUS.register(VoidStrikeEffect.class);
// MinecraftForge.EVENT_BUS.register(BlueMoonEffect.class);

// if (FMLEnvironment.dist == Dist.CLIENT) {
// MinecraftForge.EVENT_BUS.register(TetraClientEventHandler.class);
// }
// }
// }
