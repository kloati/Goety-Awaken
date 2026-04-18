package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.utils.SafeClass;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class GAModRenderTypes extends RenderType {

    public GAModRenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize,
            boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }

    private static final ResourceLocation WHITE_TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/particle/white.png");

    public static final Function<ResourceLocation, RenderType> GLOWING_TRAIL_EFFECT = Util.memoize(
            pLocation -> {
                CompositeState compositeState = CompositeState.builder()
                        .setShaderState(RENDERTYPE_EYES_SHADER)
                        .setTextureState(new TextureStateShard(pLocation, false, false))
                        .setTransparencyState(ADDITIVE_TRANSPARENCY)
                        .setCullState(NO_CULL)
                        .setLightmapState(LIGHTMAP)
                        .setOverlayState(OVERLAY)
                        .setWriteMaskState(COLOR_WRITE)
                        .createCompositeState(true);
                return create("glowing_trail_effect", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536,
                        true, true, compositeState);
            });

    public static final Function<ResourceLocation, RenderType> SHADER_COMPAT_TRAIL_EFFECT = Util.memoize(
            pLocation -> {
                CompositeState compositeState = CompositeState.builder()
                        .setShaderState(RENDERTYPE_EYES_SHADER)
                        .setTextureState(new TextureStateShard(pLocation, false, false))
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setCullState(NO_CULL)
                        .setLightmapState(LIGHTMAP)
                        .setOverlayState(OVERLAY)
                        .setWriteMaskState(COLOR_WRITE)
                        .setDepthTestState(LEQUAL_DEPTH_TEST)
                        .createCompositeState(false);
                return create("shader_compat_trail_effect", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS,
                        1536, false, false, compositeState);
            });

    public static final Function<ResourceLocation, RenderType> TRANSLUCENT_TRAIL_EFFECT = Util.memoize(
            pLocation -> {
                CompositeState compositeState = CompositeState.builder()
                        .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER)
                        .setTextureState(new TextureStateShard(pLocation, false, false))
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setCullState(NO_CULL)
                        .setLightmapState(LIGHTMAP)
                        .setOverlayState(OVERLAY)
                        .setWriteMaskState(COLOR_WRITE)
                        .setDepthTestState(LEQUAL_DEPTH_TEST)
                        .createCompositeState(false);
                return create("translucent_trail_effect", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536,
                        false, false, compositeState);
            });

    public static final Function<ResourceLocation, RenderType> GLOWING_DARK_CORE_EFFECT = Util.memoize(
            pLocation -> {
                CompositeState compositeState = CompositeState.builder()
                        .setShaderState(RENDERTYPE_EYES_SHADER)
                        .setTextureState(new TextureStateShard(pLocation, false, false))
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setCullState(NO_CULL)
                        .setLightmapState(LIGHTMAP)
                        .setOverlayState(OVERLAY)
                        .setWriteMaskState(COLOR_WRITE)
                        .setDepthTestState(LEQUAL_DEPTH_TEST)
                        .createCompositeState(false);
                return create("glowing_dark_core_effect", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS,
                        1536, false, false, compositeState);
            });

    public static final Function<ResourceLocation, RenderType> SHADER_COMPAT_DARK_CORE_EFFECT = Util.memoize(
            pLocation -> {
                CompositeState compositeState = CompositeState.builder()
                        .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER)
                        .setTextureState(new TextureStateShard(pLocation, false, false))
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setCullState(NO_CULL)
                        .setLightmapState(LIGHTMAP)
                        .setOverlayState(OVERLAY)
                        .setWriteMaskState(COLOR_WRITE)
                        .setDepthTestState(LEQUAL_DEPTH_TEST)
                        .createCompositeState(false);
                return create("shader_compat_dark_core_effect", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS,
                        1536, false, false, compositeState);
            });

    public static final Function<ResourceLocation, RenderType> ENTITY_GLOWING_EFFECT = Util.memoize(
            pLocation -> {
                CompositeState compositeState = CompositeState.builder()
                        .setShaderState(RENDERTYPE_EYES_SHADER)
                        .setTextureState(new TextureStateShard(pLocation, false, false))
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setCullState(NO_CULL)
                        .setLightmapState(LIGHTMAP)
                        .setOverlayState(OVERLAY)
                        .setWriteMaskState(COLOR_WRITE)
                        .setDepthTestState(LEQUAL_DEPTH_TEST)
                        .createCompositeState(false);
                return create("entity_glowing_effect", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS,
                        256, false, true, compositeState);
            });

    public static RenderType getGlowingTrailEffect(ResourceLocation location) {
        return GLOWING_TRAIL_EFFECT.apply(location);
    }

    public static RenderType getTranslucentTrailEffect(ResourceLocation location) {
        return TRANSLUCENT_TRAIL_EFFECT.apply(location);
    }

    public static RenderType getGlowingDarkCoreEffect(ResourceLocation location) {
        return GLOWING_DARK_CORE_EFFECT.apply(location);
    }

    public static RenderType getEntityGlowingType(ResourceLocation location) {
        return SafeClass.usingShaderPack()
                ? ENTITY_GLOWING_EFFECT.apply(location)
                : RenderType.entityTranslucent(location);
    }

    public static RenderType getEmissiveGlowType(ResourceLocation location) {
        return SafeClass.usingShaderPack()
                ? ENTITY_GLOWING_EFFECT.apply(location)
                : ModRenderTypes.brightEmissive(location);
    }

    public static RenderType getTrailRenderType(ResourceLocation location) {
        return SafeClass.usingShaderPack()
                ? SHADER_COMPAT_TRAIL_EFFECT.apply(location)
                : GLOWING_TRAIL_EFFECT.apply(location);
    }

    public static RenderType getDarkCoreRenderType(ResourceLocation location) {
        return SafeClass.usingShaderPack()
                ? SHADER_COMPAT_DARK_CORE_EFFECT.apply(location)
                : GLOWING_DARK_CORE_EFFECT.apply(location);
    }
}
