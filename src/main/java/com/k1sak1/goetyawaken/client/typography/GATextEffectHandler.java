package com.k1sak1.goetyawaken.client.typography;

import net.minecraft.network.chat.Style;

public interface GATextEffectHandler {
   String getEffectId();

   boolean shouldApply(Style var1);

   GARenderCommand process(Character var1, Style var2, long var3);
}
