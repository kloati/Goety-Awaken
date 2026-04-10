package com.k1sak1.goetyawaken.utils;

public class RenderHelper {
    public static int colorf(float pRed, float pGreen, float pBlue, float pAlpha) {
        return color255((int) (255 * pRed), (int) (255 * pGreen), (int) (255 * pBlue), (int) (255 * pAlpha));
    }

    public static int colorf(float pRed, float pGreen, float pBlue) {
        return colorf(pRed, pGreen, pBlue, 1f);
    }

    public static int color255(int pRed, int pGreen, int pBlue, int pAlpha) {
        return pAlpha << 24 | pRed << 16 | pGreen << 8 | pBlue;
    }

    public static int color255(int pRed, int pGreen, int pBlue) {
        return color255(pRed, pGreen, pBlue, 255);
    }
}