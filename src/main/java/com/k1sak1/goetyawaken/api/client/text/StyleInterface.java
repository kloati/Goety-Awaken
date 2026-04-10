package com.k1sak1.goetyawaken.api.client.text;

import net.minecraft.network.chat.Style;

public interface StyleInterface {
    static StyleInterface of(Style style) {
        return (StyleInterface) style;
    }

    boolean goetyawaken$isCentered();

    void goetyawaken$withCentered(boolean is);
}
