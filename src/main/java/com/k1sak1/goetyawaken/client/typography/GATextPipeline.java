package com.k1sak1.goetyawaken.client.typography;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

public class GATextPipeline {
   private static final Map EFFECT_REGISTRY = new ConcurrentHashMap();
   private static final Map METADATA_CACHE = new ConcurrentHashMap();

   public static void registerHandler(GATextEffectHandler handler) {
      EFFECT_REGISTRY.put(handler.getEffectId(), handler);
   }

   public static @Nullable GATextEffectHandler getHandler(String effectId) {
      return (GATextEffectHandler) EFFECT_REGISTRY.get(effectId);
   }

   public static void registerMetadata(TextColor color, GATextEffectData metadata) {
      METADATA_CACHE.put(color, metadata);
   }

   @Nullable
   public static GATextEffectData getMetadata(TextColor color) {
      return (GATextEffectData) METADATA_CACHE.get(color);
   }

   public static List processText(FormattedCharSequence text, long timestamp) {
      List<GARenderCommand> instructions = new ArrayList();
      text.accept((index, style, codePoint) -> {
         TextColor color = style.getColor();
         char character = (char) codePoint;
         if (color != null) {
            GATextEffectData metadata = (GATextEffectData) METADATA_CACHE.get(color);
            if (metadata != null) {
               GATextEffectHandler handler = (GATextEffectHandler) EFFECT_REGISTRY.get(metadata.effectId());
               if (handler != null && handler.shouldApply(style)) {
                  GARenderCommand instruction = handler.process(character, style, timestamp);
                  if (instruction != null) {
                     instructions.add(instruction);
                     return true;
                  }
               }
            }
         }

         instructions.add(GARenderCommand.defaultRender(character, style));
         return true;
      });
      return instructions;
   }

   public static boolean hasEffect(FormattedCharSequence text, String effectId) {
      boolean[] found = new boolean[] { false };
      text.accept((index, style, codePoint) -> {
         TextColor color = style.getColor();
         if (color != null) {
            GATextEffectData metadata = (GATextEffectData) METADATA_CACHE.get(color);
            if (metadata != null && metadata.effectId().equals(effectId)) {
               found[0] = true;
               return false;
            }
         }

         return true;
      });
      return found[0];
   }

   public static boolean isCentered(FormattedCharSequence text) {
      boolean[] centered = new boolean[] { false };
      text.accept((index, style, codePoint) -> {
         com.k1sak1.goetyawaken.api.client.typography.GAStyleExtension accessor = (com.k1sak1.goetyawaken.api.client.typography.GAStyleExtension) style;
         Boolean centeredField = accessor.goetyawaken$getCentered();
         if (centeredField != null && centeredField) {
            centered[0] = true;
            return false;
         } else {
            return true;
         }
      });
      return centered[0];
   }

   public static record GATextEffectData(String effectId, Map properties) {
      public static GATextEffectData of(String effectId) {
         return new GATextEffectData(effectId, new HashMap());
      }

      public GATextEffectData withProperty(String key, Object value) {
         Map<String, Object> newProps = new HashMap(this.properties);
         newProps.put(key, value);
         return new GATextEffectData(this.effectId, newProps);
      }
   }
}
