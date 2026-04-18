package com.k1sak1.goetyawaken.client.typography.effects;

import com.k1sak1.goetyawaken.client.typography.GARenderCommand;
import com.k1sak1.goetyawaken.client.typography.GATextEffectHandler;
import net.minecraft.network.chat.Style;
import org.joml.Vector3f;

public class GAErosionHandler implements GATextEffectHandler {
   public static final String EFFECT_ID = "goetyawaken:erosion";

   public String getEffectId() {
      return "goetyawaken:erosion";
   }

   public boolean shouldApply(Style style) {
      return true;
   }

   public GARenderCommand process(Character character, Style style, long timestamp) {
      return GARenderCommand.customRender(character, style, 10395294, 1.0F, new Vector3f(0.0F, 0.0F, 0.0F),
            new ErosionRenderData(timestamp, character));
   }

   public static record ErosionRenderData(long timestamp, char character) {
   }
}
