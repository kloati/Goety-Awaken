package com.k1sak1.goetyawaken.client.model.ally.Integration;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.MaidFairyServant;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.List;

public class ModelAdapterFactory {

    private static boolean isTouhouLittleMaidLoaded = false;
    private static Class<?> touhouEntityFairyModelClass = null;
    private static Constructor<?> touhouModelConstructor = null;

    static {
        try {
            touhouEntityFairyModelClass = Class
                    .forName("com.github.tartaricacid.touhoulittlemaid.client.model.EntityFairyModel");

            if (touhouEntityFairyModelClass != null) {
                for (Constructor<?> constructor : touhouEntityFairyModelClass.getDeclaredConstructors()) {
                    Class<?>[] paramTypes = constructor.getParameterTypes();
                    if (paramTypes.length == 1 && paramTypes[0].getSimpleName().equals("InputStream")) {
                        touhouModelConstructor = constructor;
                        isTouhouLittleMaidLoaded = true;
                        break;
                    }
                }

                if (!isTouhouLittleMaidLoaded) {
                    for (Constructor<?> constructor : touhouEntityFairyModelClass.getDeclaredConstructors()) {
                        Class<?>[] paramTypes = constructor.getParameterTypes();
                        if (paramTypes.length == 1 && paramTypes[0].getSimpleName().contains("Stream")) {
                            touhouModelConstructor = constructor;
                            isTouhouLittleMaidLoaded = true;
                            break;
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            isTouhouLittleMaidLoaded = false;
        }
    }

    public static Object createMaidFairyModel(ModelPart root) {
        if (isTouhouLittleMaidLoaded) {
            try {
                java.io.InputStream inputStream = ModelAdapterFactory.class.getClassLoader()
                        .getResourceAsStream("assets/touhou_little_maid/models/bedrock/entity/new_maid_fairy.json");

                if (inputStream == null) {
                    inputStream = Thread.currentThread().getContextClassLoader()
                            .getResourceAsStream("assets/touhou_little_maid/models/bedrock/entity/new_maid_fairy.json");
                }

                if (inputStream != null) {
                    Object touhouModel = touhouModelConstructor.newInstance(inputStream);
                    inputStream.close();
                    return touhouModel;
                } else {
                    inputStream = ModelAdapterFactory.class.getClassLoader()
                            .getResourceAsStream("assets/touhou_little_maid/models/bedrock/entity/new_maid_fairy.json");

                    if (inputStream == null) {
                        inputStream = Thread.currentThread().getContextClassLoader()
                                .getResourceAsStream(
                                        "assets/touhou_little_maid/models/bedrock/entity/new_maid_fairy.json");
                    }

                    if (inputStream != null) {
                        Object touhouModel = touhouModelConstructor.newInstance(inputStream);
                        inputStream.close();
                        return touhouModel;
                    } else {
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new MaidFairyServantModel<>(root);
    }

    public static void setupAnim(Object model, MaidFairyServant entity, float limbSwing, float limbSwingAmount,
            float ageInTicks, float netHeadYaw, float headPitch) {
        if (model instanceof MaidFairyServantModel) {
            MaidFairyServantModel<MaidFairyServant> maidFairyModel = (MaidFairyServantModel<MaidFairyServant>) model;
            maidFairyModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        } else {
            if (isTouhouLittleMaidLoaded && model != null && touhouEntityFairyModelClass != null &&
                    touhouEntityFairyModelClass.isInstance(model)) {
                applyAnimationToBedrockParts(model, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw,
                        headPitch);
            }
        }
    }

    private static void applyAnimationToBedrockParts(Object model, MaidFairyServant entity, float limbSwing,
            float limbSwingAmount,
            float ageInTicks, float netHeadYaw, float headPitch) {
        try {
            Class<?> bedrockPartClass = Class
                    .forName("com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart");

            Field headField = model.getClass().getDeclaredField("head");
            Field armRightField = model.getClass().getDeclaredField("armRight");
            Field armLeftField = model.getClass().getDeclaredField("armLeft");
            Field legLeftField = model.getClass().getDeclaredField("legLeft");
            Field legRightField = model.getClass().getDeclaredField("legRight");
            Field wingLeftField = model.getClass().getDeclaredField("wingLeft");
            Field wingRightField = model.getClass().getDeclaredField("wingRight");
            Field blinkField = model.getClass().getDeclaredField("blink");

            headField.setAccessible(true);
            armRightField.setAccessible(true);
            armLeftField.setAccessible(true);
            legLeftField.setAccessible(true);
            legRightField.setAccessible(true);
            wingLeftField.setAccessible(true);
            wingRightField.setAccessible(true);
            blinkField.setAccessible(true);

            Object head = headField.get(model);
            Object armRight = armRightField.get(model);
            Object armLeft = armLeftField.get(model);
            Object legLeft = legLeftField.get(model);
            Object legRight = legRightField.get(model);
            Object wingLeft = wingLeftField.get(model);
            Object wingRight = wingRightField.get(model);
            Object blink = blinkField.get(model);
            Field xRotField = bedrockPartClass.getField("xRot");
            Field yRotField = bedrockPartClass.getField("yRot");
            Field zRotField = bedrockPartClass.getField("zRot");
            Field visibleField = bedrockPartClass.getField("visible");

            xRotField.set(head, headPitch * 0.017453292F);
            yRotField.set(head, netHeadYaw * 0.017453292F);

            zRotField.set(armLeft, net.minecraft.util.Mth.cos(ageInTicks * 0.05f) * 0.05f - 0.4f);
            zRotField.set(armRight, -net.minecraft.util.Mth.cos(ageInTicks * 0.05f) * 0.05f + 0.4f);

            if (entity.onGround()) {
                xRotField.set(legLeft, net.minecraft.util.Mth.cos(limbSwing * 0.67f) * 0.3f * limbSwingAmount);
                xRotField.set(legRight, -net.minecraft.util.Mth.cos(limbSwing * 0.67f) * 0.3f * limbSwingAmount);
                xRotField.set(armLeft, -net.minecraft.util.Mth.cos(limbSwing * 0.67f) * 0.7F * limbSwingAmount);
                xRotField.set(armRight, net.minecraft.util.Mth.cos(limbSwing * 0.67f) * 0.7F * limbSwingAmount);

                yRotField.set(wingLeft, -net.minecraft.util.Mth.cos(ageInTicks * 0.3f) * 0.2f + 1.0f);
                yRotField.set(wingRight, net.minecraft.util.Mth.cos(ageInTicks * 0.3f) * 0.2f - 1.0f);
            } else {
                xRotField.set(legLeft, 0f);
                xRotField.set(legRight, 0f);
                xRotField.set(armLeft, -0.17453292F);
                xRotField.set(armRight, -0.17453292F);
                float currentHeadXRot = (float) xRotField.get(head);
                xRotField.set(head, currentHeadXRot - 8 * 0.017453292F);

                yRotField.set(wingLeft, -net.minecraft.util.Mth.cos(ageInTicks * 0.5f) * 0.4f + 1.2f);
                yRotField.set(wingRight, net.minecraft.util.Mth.cos(ageInTicks * 0.5f) * 0.4f - 1.2f);
            }

            float remainder = ageInTicks % 60;
            visibleField.set(blink, (55 < remainder && remainder < 60));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void renderToBuffer(Object model, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
            int packedOverlay, float red, float green, float blue, float alpha) {
        if (model instanceof MaidFairyServantModel) {
            MaidFairyServantModel<MaidFairyServant> maidFairyModel = (MaidFairyServantModel<MaidFairyServant>) model;
            maidFairyModel.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue,
                    alpha);
        } else {
            if (isTouhouLittleMaidLoaded && model != null && touhouEntityFairyModelClass != null &&
                    touhouEntityFairyModelClass.isInstance(model)) {
                try {
                    renderBedrockParts(model, poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue,
                            alpha);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void renderBedrockParts(Object model, PoseStack poseStack, VertexConsumer vertexConsumer,
            int packedLight,
            int packedOverlay, float red, float green, float blue, float alpha) {
        try {
            Class<?> bedrockPartClass = Class
                    .forName("com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart");
            Class<?> abstractBedrockEntityModelClass = Class
                    .forName("com.github.tartaricacid.simplebedrockmodel.client.bedrock.AbstractBedrockEntityModel");

            Field shouldRenderField = abstractBedrockEntityModelClass.getDeclaredField("shouldRender");
            shouldRenderField.setAccessible(true);
            List<Object> shouldRenderList = (List<Object>) shouldRenderField.get(model);
            for (Object bedrockPart : shouldRenderList) {
                renderBedrockPart(bedrockPart, poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue,
                        alpha);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void renderBedrockPart(Object bedrockPart, PoseStack poseStack, VertexConsumer vertexConsumer,
            int packedLight,
            int packedOverlay, float red, float green, float blue, float alpha) {
        try {
            Class<?> bedrockPartClass = Class
                    .forName("com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart");
            Method renderMethod = bedrockPartClass.getMethod("render", PoseStack.class, VertexConsumer.class, int.class,
                    int.class);
            renderMethod.invoke(bedrockPart, poseStack, vertexConsumer, packedLight, packedOverlay);
        } catch (Exception e) {
        }
    }
}