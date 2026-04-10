package com.k1sak1.goetyawaken.mixin;

import com.k1sak1.goetyawaken.utils.EarlyConfig;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GoetyAwakenMixinConfigPlugin implements IMixinConfigPlugin {

    private static final Set<String> LOADED_MODS;
    static {
        Set<String> tempSet;
        try {
            tempSet = new HashSet<>(EarlyConfig.MOD_IDS);
            System.out.println("[GoetyAwaken] Loaded mods from EarlyConfig: " + tempSet);
        } catch (Exception e) {
            System.out.println("[GoetyAwaken] Failed to load mod list: " + e.getMessage() + ", using empty set");
            tempSet = new HashSet<>();
        }
        LOADED_MODS = tempSet;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        ClassNode node;
        try {
            node = MixinService.getService().getBytecodeProvider().getClassNode(mixinClassName);
        } catch (Exception var9) {
            return true;
        }

        if (node.invisibleAnnotations == null) {
            return true;
        }

        for (AnnotationNode annotationNode : node.invisibleAnnotations) {
            if (annotationNode.desc.equals("Lcom/k1sak1/goetyawaken/util/annotation/ModDependsMixin;")) {
                Object modIdValue = annotationNode.values.get(1);
                if (modIdValue instanceof String) {
                    String modId = (String) modIdValue;
                    boolean modLoaded = LOADED_MODS.contains(modId);
                    System.out.println("[GoetyAwaken] Checking ModDependsMixin: " + modId + ", loaded=" + modLoaded
                            + ", mixin=" + mixinClassName);
                    if (!modLoaded) {
                        System.out.println("[GoetyAwaken] Skipping " + mixinClassName + " because mod " + modId
                                + " is NOT loaded");
                        return false;
                    }
                }
            }
            if (annotationNode.desc.equals("Lcom/k1sak1/goetyawaken/util/annotation/NoModDependsMixin;")) {
                Object modIdValue = annotationNode.values.get(1);
                if (modIdValue instanceof String) {
                    String modId = (String) modIdValue;
                    boolean modLoaded = LOADED_MODS.contains(modId);
                    System.out.println("[GoetyAwaken] Checking NoModDependsMixin: " + modId + ", loaded=" + modLoaded
                            + ", mixin=" + mixinClassName + ", allMods=" + LOADED_MODS);
                    if (modLoaded) {
                        System.out.println(
                                "[GoetyAwaken] Skipping " + mixinClassName + " because mod " + modId + " is loaded");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
