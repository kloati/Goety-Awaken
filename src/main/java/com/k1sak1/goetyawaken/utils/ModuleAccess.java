package com.k1sak1.goetyawaken.utils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class ModuleAccess {
    public static final MethodHandle MODULE;
    public static final MethodHandles.Lookup LOOKUP;

    static {
        try {
            LOOKUP = MethodHandles.publicLookup();

            Class<?> moduleClass = Class.forName("java.lang.Module");
            MODULE = LOOKUP.findVirtual(Class.class, "getModule", MethodType.methodType(moduleClass));
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize ModuleAccess", e);
        }
    }

    public static Object module(Class<?> clazz) {
        if (MODULE != null) {
            try {
                return MODULE.invoke(clazz);
            } catch (Throwable t) {
                handleException(t);
            }
        }
        return null;
    }

    public static void addOpens(Object sourceModule, String packageName, Object targetModule) {
        try {
            java.lang.Module sourceMod = (java.lang.Module) sourceModule;
            java.lang.Module targetMod = (java.lang.Module) targetModule;
            sourceMod.addOpens(packageName, targetMod);
        } catch (Exception e) {
        }
    }

    public static void addReads(Object sourceModule, Object targetModule) {
        try {
            java.lang.Module sourceMod = (java.lang.Module) sourceModule;
            java.lang.Module targetMod = (java.lang.Module) targetModule;
            sourceMod.addReads(targetMod);
        } catch (Exception e) {
        }
    }

    public static void addExports(Object sourceModule, String packageName, Object targetModule) {
        try {
            java.lang.Module sourceMod = (java.lang.Module) sourceModule;
            java.lang.Module targetMod = (java.lang.Module) targetModule;
            sourceMod.addExports(packageName, targetMod);
        } catch (Exception e) {
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Throwable> void handleException(Throwable t) throws T {
        throw (T) t;
    }
}