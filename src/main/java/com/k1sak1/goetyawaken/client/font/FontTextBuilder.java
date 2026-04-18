package com.k1sak1.goetyawaken.client.font;

import com.k1sak1.goetyawaken.client.typography.GATextMetadata;
import com.k1sak1.goetyawaken.utils.SafeClass;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;

public class FontTextBuilder {

   public static class TextSegmentResult {
      private String effectText;
      private String normalText;

      public TextSegmentResult(String effectText, String normalText) {
         this.effectText = effectText;
         this.normalText = normalText;
      }

      public String getEffectText() {
         return effectText;
      }

      public String getNormalText() {
         return normalText;
      }

      public String getAt(int index) {
         return index == 0 ? effectText : normalText;
      }
   }

   private static class CharacterSequenceVisitor {
      private static final char[] MODERN_UI_SPACER = new char[] { ' ', ' ', ' ', ' ' };
      private static final char[] STANDARD_SPACER = new char[] { ' ', ' ' };
      private static final char[] ACTIVE_SPACER = SafeClass.isModernUILoaded()
            ? MODERN_UI_SPACER
            : STANDARD_SPACER;

      private final StyleFilter effectFilter;
      private final StringBuilder effectBuffer = new StringBuilder();
      private final StringBuilder normalBuffer = new StringBuilder();

      public CharacterSequenceVisitor(StyleFilter filter) {
         this.effectFilter = filter;
      }

      public boolean visitCharacter(int position, Style textStyle, int codePoint) {
         boolean isEffectChar = effectFilter.matchesStyle(textStyle);

         if (isEffectChar) {
            effectBuffer.append(Character.toChars(codePoint));
            normalBuffer.append(ACTIVE_SPACER);
         } else {
            normalBuffer.append(Character.toChars(codePoint));
            effectBuffer.append(ACTIVE_SPACER);
         }

         return true;
      }

      public TextSegmentResult buildResult() {
         return new TextSegmentResult(
               effectBuffer.toString(),
               normalBuffer.toString());
      }

      public void reset() {
         effectBuffer.setLength(0);
         normalBuffer.setLength(0);
      }
   }

   @FunctionalInterface
   public interface StyleFilter {

      boolean matchesStyle(Style style);
   }

   private static class ErosionStyleFilter implements StyleFilter {

      private static final String EROSION_EFFECT_ID = "goetyawaken:erosion";

      @Override
      public boolean matchesStyle(Style style) {
         TextColor textColor = style.getColor();
         if (textColor == null) {
            return false;
         }

         return GATextMetadata.hasMetadata(textColor)
               && GATextMetadata.get(textColor).effectId().equals(EROSION_EFFECT_ID);
      }
   }

   private static final ErosionStyleFilter EROSION_FILTER = new ErosionStyleFilter();

   public static String extractTextFromSequence(FormattedCharSequence sequence) {
      CharacterSequenceVisitor visitor = new CharacterSequenceVisitor(style -> true);
      sequence.accept(visitor::visitCharacter);
      return visitor.buildResult().getEffectText();
   }

   public static String[] splitErosionText(FormattedCharSequence sequence) {
      CharacterSequenceVisitor visitor = new CharacterSequenceVisitor(EROSION_FILTER);
      sequence.accept(visitor::visitCharacter);

      TextSegmentResult result = visitor.buildResult();

      return new String[] {
            result.getEffectText(),
            result.getNormalText()
      };
   }

   public static TextSegmentResult splitTextWithResult(FormattedCharSequence sequence) {
      CharacterSequenceVisitor visitor = new CharacterSequenceVisitor(EROSION_FILTER);
      sequence.accept(visitor::visitCharacter);
      return visitor.buildResult();
   }
}
