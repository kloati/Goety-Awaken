package com.k1sak1.goetyawaken.init;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.glfw.GLFW;

public class ModKeybindings {
    public static KeyMapping[] keyBindings = new KeyMapping[18];

    public static void init() {
        keyBindings[15] = new KeyMapping("key.goetyawaken.wither.fly_up", GLFW.GLFW_KEY_SPACE,
                "key.goety.mount.category");
        keyBindings[16] = new KeyMapping("key.goetyawaken.wither.fly_down", GLFW.GLFW_KEY_LEFT_CONTROL,
                "key.goety.mount.category");
        keyBindings[17] = new KeyMapping("key.goetyawaken.open_access_focus", GLFW.GLFW_KEY_LEFT_BRACKET,
                "key.goety.category");
        for (KeyMapping keyBinding : keyBindings) {
            if (keyBinding != null) {
                Minecraft.getInstance().options.keyMappings = ArrayUtils
                        .add(Minecraft.getInstance().options.keyMappings, keyBinding);
            }
        }
    }
}