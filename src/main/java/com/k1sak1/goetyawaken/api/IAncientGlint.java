package com.k1sak1.goetyawaken.api;

public interface IAncientGlint {
    boolean hasAncientGlint();

    void setAncientGlint(boolean hasGlint);

    default String getGlintTextureType() {
        return "ancient";
    }

    default void setGlintTextureType(String textureType) {
    }

    default int getAncientHuntNumber() {
        return 0;
    }

    default void setAncientHuntNumber(int huntNumber) {
    }
}
