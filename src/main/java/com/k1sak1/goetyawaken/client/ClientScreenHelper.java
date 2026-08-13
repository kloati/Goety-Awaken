package com.k1sak1.goetyawaken.client;

import com.k1sak1.goetyawaken.client.screen.ApostleProgressScreen;
import com.k1sak1.goetyawaken.client.screen.SorcererSpellConfigScreen;
import com.k1sak1.goetyawaken.common.network.client.SApostleProgressSyncPacket;
import net.minecraft.client.Minecraft;

public class ClientScreenHelper {
    public static void openSpellConfigScreen() {
        Minecraft.getInstance().setScreen(new SorcererSpellConfigScreen());
    }

    public static void openApostleProgressScreen(SApostleProgressSyncPacket packet) {
        if (Minecraft.getInstance().player == null) {
            return;
        }
        Minecraft.getInstance().setScreen(new ApostleProgressScreen(packet));
    }
}
