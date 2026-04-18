package com.k1sak1.goetyawaken.mixin;

import com.k1sak1.goetyawaken.utils.ModRuntimeScanner;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.util.List;
import java.util.Set;

public class ConditionalMixinLoader implements IMixinConfigPlugin {

    private static final String REQUIRES_ANNOTATION = "Lcom/k1sak1/goetyawaken/utils/annotation/RequiresModPresent;";
    private static final String EXCLUDE_ANNOTATION = "Lcom/k1sak1/goetyawaken/utils/annotation/ExcludeIfModPresent;";

    @Override
    public void onLoad(String mixinPackage) {
        ModRuntimeScanner.initialize();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        ClassNode mixinClass;
        try {
            mixinClass = MixinService.getService().getBytecodeProvider().getClassNode(mixinClassName);
        } catch (Exception e) {
            return true;
        }

        if (mixinClass.invisibleAnnotations == null || mixinClass.invisibleAnnotations.isEmpty()) {
            return true;
        }

        for (AnnotationNode annotation : mixinClass.invisibleAnnotations) {
            String descriptor = annotation.desc;
            if (REQUIRES_ANNOTATION.equals(descriptor)) {
                return handleRequiresAnnotation(annotation, mixinClassName);
            }

            if (EXCLUDE_ANNOTATION.equals(descriptor)) {
                return handleExcludeAnnotation(annotation, mixinClassName);
            }
        }

        return true;
    }

    private boolean handleRequiresAnnotation(AnnotationNode annotation, String mixinClassName) {
        String requiredModId = extractModId(annotation);
        if (requiredModId == null) {
            return true;
        }

        boolean isLoaded = ModRuntimeScanner.isModLoaded(requiredModId);

        if (!isLoaded) {
            return false;
        }

        return true;
    }

    private boolean handleExcludeAnnotation(AnnotationNode annotation, String mixinClassName) {
        String excludedModId = extractModId(annotation);
        if (excludedModId == null) {
            return true;
        }

        boolean isLoaded = ModRuntimeScanner.isModLoaded(excludedModId);

        if (isLoaded) {
            return false;
        }

        return true;
    }

    private String extractModId(AnnotationNode annotation) {
        if (annotation.values == null || annotation.values.size() < 2) {
            return null;
        }

        for (int i = 0; i < annotation.values.size(); i += 2) {
            String key = (String) annotation.values.get(i);
            if ("value".equals(key)) {
                Object modIdObj = annotation.values.get(i + 1);
                if (modIdObj instanceof String) {
                    return (String) modIdObj;
                }
            }
        }

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
