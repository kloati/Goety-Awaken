package com.k1sak1.goetyawaken.api.client.text;

import com.k1sak1.goetyawaken.client.typography.GATextPipeline;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

public class GATextStyleUtils {
   public static ChatFormatting MIDDLE;
   public static ChatFormatting EROSION;
   private static final ThreadLocal CENTERED_WIDTH_STACK = ThreadLocal.withInitial(ConcurrentLinkedDeque::new);

   public static boolean hasEffect(FormattedCharSequence fcs, String effectId) {
      return GATextPipeline.hasEffect(fcs, effectId);
   }

   public static boolean isCentered(FormattedCharSequence fcs) {
      return GATextPipeline.isCentered(fcs);
   }

   public static int getMaxLineWidth(List<ClientTooltipComponent> components, Font font, int minWidth) {
      int textWidth = minWidth;

      for (ClientTooltipComponent component : components) {
         int componentWidth = component.getWidth(font);
         if (componentWidth > textWidth) {
            textWidth = componentWidth;
         }
      }

      return textWidth;
   }

   public static void pushCentered(int width) {
      ((Deque) CENTERED_WIDTH_STACK.get()).push(width);
   }

   public static void popCentered() {
      Deque<Integer> stack = (Deque) CENTERED_WIDTH_STACK.get();
      if (!stack.isEmpty()) {
         stack.pop();
      }

   }

   public static int getCenteredTooltipWidth() {
      Deque<Integer> stack = (Deque) CENTERED_WIDTH_STACK.get();
      return stack.isEmpty() ? -1 : (Integer) stack.peek();
   }

   public static Component component() {
      MutableComponent component = Component.literal("A");
      component.setStyle(component.getStyle().applyFormat(MIDDLE));
      return component;
   }

   public static Font font() {
      return Minecraft.getInstance().font;
   }

   public static Vector3f rgbToHsv(int r, int g, int b) {
      if (r >= 0 && r <= 255 && g >= 0 && g <= 255 && b >= 0 && b <= 255) {
         float rf = (float) r / 255.0F;
         float gf = (float) g / 255.0F;
         float bf = (float) b / 255.0F;
         float max = Math.max(rf, Math.max(gf, bf));
         float min = Math.min(rf, Math.min(gf, bf));
         float delta = max - min;
         float s = max == 0.0F ? 0.0F : delta / max;
         float h;
         if (delta == 0.0F) {
            h = 0.0F;
         } else {
            if (max == rf) {
               h = 60.0F * ((gf - bf) / delta % 6.0F);
            } else if (max == gf) {
               h = 60.0F * ((bf - rf) / delta + 2.0F);
            } else {
               h = 60.0F * ((rf - gf) / delta + 4.0F);
            }

            if (h < 0.0F) {
               h += 360.0F;
            }
         }

         return new Vector3f(h, s, max);
      } else {
         throw new IllegalArgumentException("RGB values must be in range [0, 255]");
      }
   }

   public static Vector3f rgbToHsv(float rf, float gf, float bf) {
      rf = Mth.clamp(rf, 0.0F, 1.0F);
      gf = Mth.clamp(gf, 0.0F, 1.0F);
      bf = Mth.clamp(bf, 0.0F, 1.0F);
      float max = Math.max(rf, Math.max(gf, bf));
      float min = Math.min(rf, Math.min(gf, bf));
      float delta = max - min;
      float s = max == 0.0F ? 0.0F : delta / max;
      float h;
      if (delta == 0.0F) {
         h = 0.0F;
      } else {
         if (max == rf) {
            h = 60.0F * ((gf - bf) / delta % 6.0F);
         } else if (max == gf) {
            h = 60.0F * ((bf - rf) / delta + 2.0F);
         } else {
            h = 60.0F * ((rf - gf) / delta + 4.0F);
         }

         if (h < 0.0F) {
            h += 360.0F;
         }
      }

      return new Vector3f(h, s, max);
   }

   public static Vector3f hsvToRgb(float h, float s, float v) {
      h = Mth.clamp(h, 0.0F, 360.0F);
      s = Mth.clamp(s, 0.0F, 1.0F);
      v = Mth.clamp(v, 0.0F, 1.0F);
      if (s == 0.0F) {
         return new Vector3f(v, v, v);
      } else {
         float hPrime = h / 60.0F;
         int sector = (int) Math.floor((double) hPrime);
         float f = hPrime - (float) sector;
         float p = v * (1.0F - s);
         float q = v * (1.0F - s * f);
         float t = v * (1.0F - s * (1.0F - f));
         float rf;
         float gf;
         float bf;
         switch (sector) {
            case 0:
               rf = v;
               gf = t;
               bf = p;
               break;
            case 1:
               rf = q;
               gf = v;
               bf = p;
               break;
            case 2:
               rf = p;
               gf = v;
               bf = t;
               break;
            case 3:
               rf = p;
               gf = q;
               bf = v;
               break;
            case 4:
               rf = t;
               gf = p;
               bf = q;
               break;
            case 5:
               rf = v;
               gf = p;
               bf = q;
               break;
            default:
               throw new IllegalStateException("Unexpected sector: " + sector);
         }

         return new Vector3f(rf, gf, bf);
      }
   }

   public static int hsvToRgbInt(float h, float s, float v) {
      h = Mth.clamp(h, 0.0F, 360.0F);
      s = Mth.clamp(s, 0.0F, 1.0F);
      v = Mth.clamp(v, 0.0F, 1.0F);
      if (s == 0.0F) {
         return (int) (v * 1.6777215E7F);
      } else {
         float hPrime = h / 60.0F;
         int sector = (int) Math.floor((double) hPrime);
         float f = hPrime - (float) sector;
         float p = v * (1.0F - s);
         float q = v * (1.0F - s * f);
         float t = v * (1.0F - s * (1.0F - f));
         float rf;
         float gf;
         float bf;
         switch (sector) {
            case 0:
               rf = v;
               gf = t;
               bf = p;
               break;
            case 1:
               rf = q;
               gf = v;
               bf = p;
               break;
            case 2:
               rf = p;
               gf = v;
               bf = t;
               break;
            case 3:
               rf = p;
               gf = q;
               bf = v;
               break;
            case 4:
               rf = t;
               gf = p;
               bf = q;
               break;
            case 5:
               rf = v;
               gf = p;
               bf = q;
               break;
            default:
               throw new IllegalStateException("Unexpected sector: " + sector);
         }

         return Mth.clamp((int) (rf * 255.0F * 255.0F * 255.0F), 0, 16711425)
               + Mth.clamp((int) (gf * 255.0F * 255.0F), 0, 65535) + Mth.clamp((int) (bf * 255.0F), 0, 255);
      }
   }
}
