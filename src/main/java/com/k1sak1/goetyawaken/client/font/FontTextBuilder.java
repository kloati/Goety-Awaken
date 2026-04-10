package com.k1sak1.goetyawaken.client.font;

import com.k1sak1.goetyawaken.api.client.text.TextColorInterface;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;

import java.util.function.Predicate;

public class FontTextBuilder {
    static MyFormattedCharSink NORMAL = new MyFormattedCharSink((t) -> true);
    static MyFormattedCharSink EROSION = new MyFormattedCharSink(
            (t) -> t.getColor() != null && ((TextColorInterface) (Object) t.getColor()).goetyawaken$getCode() == '=');
    static MyFormattedCharSink NO_EROSION = new MyFormattedCharSink(EROSION.stylePredicate.negate());

    public static String formattedCharSequenceToString(FormattedCharSequence text) {
        text.accept(NORMAL);
        String s = NORMAL.getText();
        NORMAL.text = new StringBuilder();
        return s;
    }

    public static String[] formattedCharSequenceToStringErosion(FormattedCharSequence text) {
        String[] out = new String[2];
        text.accept(EROSION);
        out[0] = EROSION.getText();
        EROSION.text = new StringBuilder();
        text.accept(NO_EROSION);
        out[1] = NO_EROSION.getText();
        NO_EROSION.text = new StringBuilder();
        return out;
    }

    public static class MyFormattedCharSink implements FormattedCharSink {
        public static final char[] emptyChars = com.k1sak1.goetyawaken.utils.SafeClass.isModernUILoaded()
                ? new char[] { ' ', ' ', ' ', ' ' }
                : new char[] { ' ', ' ' };
        public Predicate<Style> stylePredicate;
        private StringBuilder text = new StringBuilder();

        public MyFormattedCharSink(Predicate<Style> stylePredicate) {
            this.stylePredicate = stylePredicate;
        }

        @Override
        public boolean accept(int p_13746_, Style p_13747_, int p_13748_) {
            text.append(stylePredicate.test(p_13747_) ? Character.toChars(p_13748_) : emptyChars);
            return true;
        }

        public String getText() {
            return text.toString();
        }
    }
}
