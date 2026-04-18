package com.k1sak1.goetyawaken.client.typography;

import net.minecraft.network.chat.Style;
import org.joml.Vector3f;

public class GARenderCommand {
   private final Type type;
   private final char character;
   private final Style style;
   private final Vector3f offset;
   private final int color;
   private final float alpha;
   private final Object customData;

   private GARenderCommand(Type type, char character, Style style, Vector3f offset, int color, float alpha,
         Object customData) {
      this.type = type;
      this.character = character;
      this.style = style;
      this.offset = offset;
      this.color = color;
      this.alpha = alpha;
      this.customData = customData;
   }

   public static GARenderCommand defaultRender(char character, Style style) {
      return new GARenderCommand(GARenderCommand.Type.DEFAULT, character, style, (Vector3f) null,
            style.getColor() != null ? style.getColor().getValue() : 16777215, 1.0F, (Object) null);
   }

   public static GARenderCommand customRender(char character, Style style, int color, float alpha, Vector3f offset,
         Object customData) {
      return new GARenderCommand(GARenderCommand.Type.CUSTOM, character, style, offset, color, alpha, customData);
   }

   public static GARenderCommand skip() {
      return new GARenderCommand(GARenderCommand.Type.SKIP, ' ', Style.EMPTY, (Vector3f) null, 0, 0.0F, (Object) null);
   }

   public Type getType() {
      return this.type;
   }

   public char getCharacter() {
      return this.character;
   }

   public Style getStyle() {
      return this.style;
   }

   public Vector3f getOffset() {
      return this.offset;
   }

   public int getColor() {
      return this.color;
   }

   public float getAlpha() {
      return this.alpha;
   }

   public Object getCustomData() {
      return this.customData;
   }

   public boolean isCustom() {
      return this.type == GARenderCommand.Type.CUSTOM;
   }

   public boolean shouldSkip() {
      return this.type == GARenderCommand.Type.SKIP;
   }

   public static enum Type {
      DEFAULT,
      CUSTOM,
      SKIP;

      private static Type[] $values() {
         return new Type[] { DEFAULT, CUSTOM, SKIP };
      }
   }
}
