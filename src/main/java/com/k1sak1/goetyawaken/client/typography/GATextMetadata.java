package com.k1sak1.goetyawaken.client.typography;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.network.chat.TextColor;

public class GATextMetadata {
   private static final Map METADATA_MAP = new ConcurrentHashMap();

   public static void register(TextColor color, GATextPipeline.GATextEffectData metadata) {
      if (color == null) {

      } else {
         try {
            METADATA_MAP.put(color.getValue(), metadata);
            GATextPipeline.registerMetadata(color, metadata);
         } catch (Exception e) {

         }
      }
   }

   public static GATextPipeline.GATextEffectData get(TextColor color) {
      if (color == null) {
         return null;
      }
      try {
         return (GATextPipeline.GATextEffectData) METADATA_MAP.get(color.getValue());
      } catch (Exception e) {
         return null;
      }
   }

   public static boolean hasMetadata(TextColor color) {
      if (color == null) {
         return false;
      }
      try {
         return METADATA_MAP.containsKey(color.getValue());
      } catch (Exception e) {
         return false;
      }
   }

   public static void clear() {
      METADATA_MAP.clear();
   }
}
