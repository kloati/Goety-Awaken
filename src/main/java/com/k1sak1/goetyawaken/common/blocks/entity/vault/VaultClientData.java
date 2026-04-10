package com.k1sak1.goetyawaken.common.blocks.entity.vault;

import net.minecraft.util.Mth;

public class VaultClientData {
    public static final float DISPLAY_ROTATION_SPEED = 10.0F;
    private float displayRotation;
    private float prevDisplayRotation;

    public float getDisplayRotation() {
        return this.displayRotation;
    }

    public float getPreviousDisplayRotation() {
        return this.prevDisplayRotation;
    }

    public void rotateDisplay() {
        this.prevDisplayRotation = this.displayRotation;
        this.displayRotation = Mth.wrapDegrees(this.displayRotation + 10.0F);
    }
}
