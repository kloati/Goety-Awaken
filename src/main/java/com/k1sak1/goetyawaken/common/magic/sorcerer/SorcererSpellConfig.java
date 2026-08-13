package com.k1sak1.goetyawaken.common.magic.sorcerer;

import com.Polarice3.Goety.api.items.magic.IFocus;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.magic.Spell;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.k1sak1.goetyawaken.GoetyAwaken;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.util.*;

public class SorcererSpellConfig {

    private static final File CONFIG_FILE = FMLPaths.CONFIGDIR.get().resolve("goetyawaken/sorcerer_spells.toml")
            .toFile();
    private static final Map<String, Item> focusRegistry = new LinkedHashMap<>();
    private static final Map<String, Item> wandRegistry = new LinkedHashMap<>();
    private static volatile List<SorcererSpellEntry> spellEntries = List.of();
    private static volatile boolean needsGlobalReload = false;
    private static final Object reloadLock = new Object();

    public static void init() {
        discoverFocusItems();
        discoverWandItems();
        loadFromConfig();
    }

    public static void discoverFocusItems() {
        focusRegistry.clear();
        ForgeRegistries.ITEMS.getEntries().stream()
                .filter(e -> e.getValue() instanceof IFocus)
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> focusRegistry.put(e.getKey().location().toString(), e.getValue()));
    }

    public static void discoverWandItems() {
        wandRegistry.clear();
        ForgeRegistries.ITEMS.getEntries().stream()
                .filter(e -> e.getValue() instanceof IWand)
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> wandRegistry.put(e.getKey().location().toString(), e.getValue()));
    }

    public static Map<String, Item> getAvailableWandItems() {
        return Collections.unmodifiableMap(wandRegistry);
    }

    public static void loadFromConfig() {
        synchronized (reloadLock) {
            List<SorcererSpellEntry> entries = new ArrayList<>();
            File parentDir = CONFIG_FILE.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            if (!CONFIG_FILE.exists()) {
                entries.addAll(getDefaultEntries());
                saveToConfigInternal(entries);
            } else {
                try {
                    CommentedFileConfig config = CommentedFileConfig.builder(CONFIG_FILE).sync().build();
                    config.load();
                    List<com.electronwill.nightconfig.core.Config> spellTables = config.get("spells");
                    if (spellTables != null) {
                        for (com.electronwill.nightconfig.core.Config table : spellTables) {
                            String focus = table.get("focus");
                            if (focus == null || focus.isEmpty())
                                continue;
                            int minLevel = getIntOr(table, "minLevel", 1);
                            int maxLevel = getIntOr(table, "maxLevel", 6);
                            boolean levelIncrease = getBoolOr(table, "levelIncrease", false);
                            String staff = table.get("upgradeStaff");
                            if (staff == null)
                                staff = "none";
                            int staffLevel = getIntOr(table, "upgradeStaffLevel", 0);
                            int weight = getIntOr(table, "weight", SorcererSpellEntry.DEFAULT_WEIGHT);
                            entries.add(new SorcererSpellEntry(focus, minLevel, maxLevel, levelIncrease, staff,
                                    staffLevel, weight));
                        }
                    }
                    config.close();
                } catch (Exception e) {
                    GoetyAwaken.LOGGER.error("Failed to load sorcerer spell config, using defaults", e);
                    entries = getDefaultEntries();
                }
            }
            resolveRuntimeFields(entries);
            for (int i = 0; i < entries.size(); i++) {
                entries.get(i).setIndex(i);
            }
            spellEntries = Collections.unmodifiableList(entries);
            needsGlobalReload = true;
        }
    }

    public static void saveToConfig(List<SorcererSpellEntry> entries) {
        synchronized (reloadLock) {
            saveToConfigInternal(entries);
            loadFromConfig();
        }
    }

    private static void saveToConfigInternal(List<SorcererSpellEntry> entries) {
        try {
            File parentDir = CONFIG_FILE.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            StringBuilder sb = new StringBuilder();
            sb.append("# Sorcerer Servant Spell Configuration\n");
            sb.append("# focus: Focus item registry name\n");
            sb.append("# minLevel: Minimum sorcerer level to cast (1-6)\n");
            sb.append("# maxLevel: Maximum sorcerer level to cast (1-6)\n");
            sb.append("# levelIncrease: Whether level increases spell potency\n");
            sb.append("# upgradeStaff: Special staff registry name, or \"none\"\n");
            sb.append("# upgradeStaffLevel: Level required for special staff\n");
            sb.append("# weight: Random selection weight (1-100, default 10)\n\n");
            for (SorcererSpellEntry entry : entries) {
                sb.append("[[spells]]\n");
                sb.append("focus = \"").append(entry.getFocusRegistryName()).append("\"\n");
                sb.append("minLevel = ").append(entry.getMinLevel()).append("\n");
                sb.append("maxLevel = ").append(entry.getMaxLevel()).append("\n");
                sb.append("levelIncrease = ").append(entry.isLevelIncrease()).append("\n");
                sb.append("upgradeStaff = \"").append(entry.getUpgradeStaffRegistryName()).append("\"\n");
                sb.append("upgradeStaffLevel = ").append(entry.getUpgradeStaffLevel()).append("\n");
                sb.append("weight = ").append(entry.getWeight()).append("\n\n");
            }
            java.nio.file.Files.writeString(CONFIG_FILE.toPath(), sb.toString());
        } catch (Exception e) {
            GoetyAwaken.LOGGER.error("Failed to save sorcerer spell config", e);
        }
    }

    private static List<SorcererSpellEntry> deduplicate(List<SorcererSpellEntry> entries) {
        Map<String, SorcererSpellEntry> map = new LinkedHashMap<>();
        for (SorcererSpellEntry entry : entries) {
            map.put(entry.getFocusRegistryName(), entry);
        }
        return new ArrayList<>(map.values());
    }

    public static void resolveRuntimeFields(List<SorcererSpellEntry> entries) {
        for (SorcererSpellEntry entry : entries) {
            Item focusItem = focusRegistry.get(entry.getFocusRegistryName());
            if (focusItem instanceof IFocus ifocus) {
                entry.setSpell((Spell) ifocus.getSpell());
                entry.setFocusStack(new ItemStack(focusItem));
            } else {
                ResourceLocation rl = ResourceLocation.tryParse(entry.getFocusRegistryName());
                if (rl != null) {
                    Item item = ForgeRegistries.ITEMS.getValue(rl);
                    if (item instanceof IFocus ifocus) {
                        entry.setSpell((Spell) ifocus.getSpell());
                        entry.setFocusStack(new ItemStack(item));
                        focusRegistry.putIfAbsent(entry.getFocusRegistryName(), item);
                    } else {
                        entry.setSpell(null);
                        entry.setFocusStack(ItemStack.EMPTY);
                        GoetyAwaken.LOGGER.warn("Unknown focus: {}", entry.getFocusRegistryName());
                    }
                } else {
                    entry.setSpell(null);
                    entry.setFocusStack(ItemStack.EMPTY);
                }
            }
            if (!"none".equals(entry.getUpgradeStaffRegistryName()) && !entry.getUpgradeStaffRegistryName().isEmpty()) {
                ResourceLocation rl = ResourceLocation.tryParse(entry.getUpgradeStaffRegistryName());
                if (rl != null) {
                    Item staffItem = ForgeRegistries.ITEMS.getValue(rl);
                    if (staffItem != null && staffItem != Items.AIR) {
                        entry.setUpgradeStaff(new ItemStack(staffItem));
                    }
                }
            }
            if (entry.getUpgradeStaff() == null) {
                entry.setUpgradeStaff(ItemStack.EMPTY);
            }
        }
    }

    public static List<SorcererSpellEntry> getSpellEntries() {
        return spellEntries;
    }

    public static Map<String, Item> getAvailableFocusItems() {
        return Collections.unmodifiableMap(focusRegistry);
    }

    public static Map<String, Item> getAvailableFocusItemsExcluding(List<SorcererSpellEntry> existing) {
        Map<String, Item> result = new LinkedHashMap<>(focusRegistry);
        for (SorcererSpellEntry entry : existing) {
            result.remove(entry.getFocusRegistryName());
        }
        return result;
    }

    public static void applySyncData(List<SorcererSpellEntry> entries) {
        resolveRuntimeFields(entries);
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).setIndex(i);
        }
        spellEntries = Collections.unmodifiableList(entries);
    }

    public static boolean consumeReloadFlag() {
        if (needsGlobalReload) {
            needsGlobalReload = false;
            return true;
        }
        return false;
    }

    private static int getIntOr(com.electronwill.nightconfig.core.Config config, String key, int defaultVal) {
        Object val = config.get(key);
        if (val instanceof Number n)
            return n.intValue();
        return defaultVal;
    }

    private static boolean getBoolOr(com.electronwill.nightconfig.core.Config config, String key, boolean defaultVal) {
        Object val = config.get(key);
        if (val instanceof Boolean b)
            return b;
        return defaultVal;
    }

    public static List<SorcererSpellEntry> getDefaultEntries() {
        List<SorcererSpellEntry> defaults = new ArrayList<>();
        defaults.add(new SorcererSpellEntry("goety:fire_breath_focus", 1, 3, false, "none", 0, 10));
        defaults.add(new SorcererSpellEntry("goety:iron_hide_focus", 1, 6, true, "none", 0, 10));
        defaults.add(new SorcererSpellEntry("goety:hunting_focus", 1, 1, false, "none", 0, 10));
        defaults.add(new SorcererSpellEntry("goety:soul_heal_focus", 1, 6, true, "none", 0, 10));
        defaults.add(new SorcererSpellEntry("goety:soul_heal_focus", 6, 6, true, "none", 0, 10));
        defaults.add(new SorcererSpellEntry("goety:frost_breath_focus", 2, 3, false, "none", 0, 10));
        defaults.add(new SorcererSpellEntry("goety:mauling_focus", 2, 6, true, "goety:wild_staff", 6, 10));
        defaults.add(new SorcererSpellEntry("goety:biting_focus", 2, 6, true, "goety:ominous_staff", 6, 10));
        defaults.add(new SorcererSpellEntry("goety:frostborn_focus", 3, 6, true, "goety:frost_staff", 6, 10));
        defaults.add(new SorcererSpellEntry("goety:ice_spike_focus", 3, 6, true, "goety:frost_staff", 6, 10));
        defaults.add(new SorcererSpellEntry("goety:thunderbolt_focus", 3, 6, true, "goety:storm_staff", 4, 10));
        defaults.add(new SorcererSpellEntry("goety:scatter_focus", 3, 5, true, "none", 0, 10));
        defaults.add(new SorcererSpellEntry("goety:ice_storm_focus", 4, 6, true, "goety:frost_staff", 6, 10));
        defaults.add(new SorcererSpellEntry("goety:bulwark_focus", 4, 6, true, "none", 0, 10));
        defaults.add(new SorcererSpellEntry("goety:electrocute_focus", 4, 6, true, "goety:storm_staff", 6, 10));
        defaults.add(new SorcererSpellEntry("goety:bouncy_bubble_focus", 4, 5, true, "none", 0, 10));
        defaults.add(new SorcererSpellEntry("goety:arrow_rain_focus", 4, 5, true, "none", 0, 10));
        defaults.add(new SorcererSpellEntry("goety:vexing_focus", 4, 6, true, "goety:ominous_staff", 6, 10));
        defaults.add(new SorcererSpellEntry("goety:cyclone_focus", 5, 6, true, "goety:wind_staff", 6, 10));
        defaults.add(new SorcererSpellEntry("goety:eruption_focus", 5, 6, true, "goety:geo_staff", 5, 10));
        defaults.add(new SorcererSpellEntry("goety:tidal_focus", 5, 6, true, "none", 0, 10));
        defaults.add(new SorcererSpellEntry("goety:biomine_focus", 5, 6, true, "goety:abyss_staff", 6, 10));
        defaults.add(new SorcererSpellEntry("goety:blossoming_focus", 6, 6, true, "goety:wild_staff", 6, 10));
        defaults.add(new SorcererSpellEntry("goety:magma_bomb_focus", 6, 6, true, "goety:nether_staff", 6, 10));
        defaults.add(new SorcererSpellEntry("goety:razor_wind_focus", 6, 6, true, "goety:wind_staff", 6, 10));
        defaults.add(new SorcererSpellEntry("goety:weakening_focus", 5, 6, true, "none", 0, 10));
        defaults.add(new SorcererSpellEntry("goety:magic_bolt_focus", 6, 6, false, "none", 0, 10));
        defaults.add(new SorcererSpellEntry("goety:entangling_focus", 5, 6, true, "goety:wild_staff", 6, 10));
        defaults.add(new SorcererSpellEntry("goety:poison_dart_focus", 1, 3, true, "none", 0, 10));
        defaults.add(new SorcererSpellEntry("goety:soul_bolt_focus", 1, 2, true, "none", 0, 10));
        defaults.add(new SorcererSpellEntry("goety:chilling_focus", 3, 6, true, "goety:frost_staff", 6, 10));
        defaults.add(new SorcererSpellEntry("goety:frost_nova_focus", 5, 6, true, "goety:frost_staff", 6, 10));
        defaults.add(new SorcererSpellEntry("goety:ghost_fire_focus", 4, 6, true, "goety:necro_staff", 6, 10));
        defaults.add(new SorcererSpellEntry("goety:trembling_focus", 3, 6, true, "goety:wind_staff", 6, 10));
        defaults.add(new SorcererSpellEntry("goety:discharge_focus", 6, 6, true, "goety:storm_staff", 6, 10));
        return defaults;
    }
}
