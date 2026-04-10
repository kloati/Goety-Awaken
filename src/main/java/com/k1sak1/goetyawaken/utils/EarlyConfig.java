package com.k1sak1.goetyawaken.utils;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.List;
import java.util.Set;

/**
 * Inspired by
 * <a href=
 * "https://github.com/Mega32K/ending_library/?tab=readme-ov-file">ending_library</a>
 * project.
 * Original author: MegaDarkness
 * </p>
 */
public class EarlyConfig {
    public static final Set<String> MOD_IDS = new ObjectOpenHashSet<>();

    static {
        try {
            LoadingModList loadingModList = FMLLoader.getLoadingModList();
            if (loadingModList != null) {
                final List<List<IModInfo>> modInfos = loadingModList.getModFiles().stream()
                        .map(ModFileInfo::getFile)
                        .map(ModFile::getModInfos).toList();
                for (List<IModInfo> iModInfoList : modInfos) {
                    for (IModInfo modInfo : iModInfoList) {
                        MOD_IDS.add(modInfo.getModId());
                    }
                }
            }
        } catch (Exception e) {

        }
    }
}
