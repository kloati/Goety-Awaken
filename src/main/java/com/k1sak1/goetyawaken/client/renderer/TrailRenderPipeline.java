package com.k1sak1.goetyawaken.client.renderer;

import com.google.common.collect.ImmutableList;
import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.renderer.trail.TrailPosition;
import com.k1sak1.goetyawaken.client.renderer.trail.TrailVertexData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class TrailRenderPipeline {

    public static final ResourceLocation WHITE_TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/particle/white.png");
    public static RenderType TRAIL_RENDER_TYPE = RenderType.entityTranslucent(WHITE_TEXTURE);
    public static RenderType TRAIL_ENERGY_SWIRL_TYPE = RenderType.energySwirl(WHITE_TEXTURE, 1.0F, 1.0F);

    public static RenderType GLOWING_TRAIL_RENDER_TYPE = GAModRenderTypes.getGlowingTrailEffect(WHITE_TEXTURE);

    public static RenderType TRANSLUCENT_TRAIL_RENDER_TYPE = GAModRenderTypes.getTranslucentTrailEffect(WHITE_TEXTURE);

    public static RenderType GLOWING_DARK_CORE_RENDER_TYPE = GAModRenderTypes
            .getGlowingDarkCoreEffect(WHITE_TEXTURE);

    public static RenderType getGlowingTrailRenderType() {
        return GAModRenderTypes.getTrailRenderType(WHITE_TEXTURE);
    }

    public static RenderType getDarkCoreRenderType() {
        return GAModRenderTypes.getDarkCoreRenderType(WHITE_TEXTURE);
    }

    public TrailRenderPipeline() {
    }

    public static TrailBufferBuilder createWorldBuilder() {
        return new TrailBufferBuilder();
    }

    public static Vec2 computePerpendicularOffset(Vector4f previous, Vector4f current, Vector4f next, float width) {
        float deltaX = next.x() - previous.x();
        float deltaY = next.y() - previous.y();
        float deltaZ = next.z() - previous.z();
        float perpX = -current.x();
        float perpY = -current.y();
        float depthFactor;

        if (Math.abs(current.z()) > 0.001F) {
            depthFactor = next.z() / current.z();
            perpX = next.x() + perpX * depthFactor;
            perpY = next.y() + perpY * depthFactor;
        } else if (Math.abs(next.z()) <= 0.001F) {
            perpX += next.x();
            perpY += next.y();
        }

        if (current.z() > 0.0F) {
            perpX = -perpX;
            perpY = -perpY;
        }

        float distSqr = perpX * perpX + perpY * perpY;
        if (distSqr > 0.001F) {
            float normalize = width * 0.5F / Mth.sqrt(distSqr);
            perpX *= normalize;
            perpY *= normalize;
        }

        return new Vec2(-perpY, perpX);
    }

    public static class TrailBufferBuilder {

        private static final Object2ObjectOpenHashMap<VertexFormatElement, VertexHandler> ELEMENT_HANDLERS = new Object2ObjectOpenHashMap<>();

        static {
            ELEMENT_HANDLERS.put(DefaultVertexFormat.ELEMENT_POSITION,
                    (vertexConsumer, x, y, z, u, v, light, overlay, red, green, blue, alpha) -> {
                        vertexConsumer.vertex(x, y, z);
                    });

            ELEMENT_HANDLERS.put(DefaultVertexFormat.ELEMENT_COLOR,
                    (vertexConsumer, x, y, z, u, v, light, overlay, red, green, blue, alpha) -> {
                        vertexConsumer.color(red, green, blue, alpha);
                    });

            ELEMENT_HANDLERS.put(DefaultVertexFormat.ELEMENT_UV0,
                    (vertexConsumer, x, y, z, u, v, light, overlay, red, green, blue, alpha) -> {
                        vertexConsumer.uv(u, v);
                    });

            ELEMENT_HANDLERS.put(DefaultVertexFormat.ELEMENT_UV1,
                    (vertexConsumer, x, y, z, u, v, light, overlay, red, green, blue, alpha) -> {
                        vertexConsumer.overlayCoords(overlay);
                    });

            ELEMENT_HANDLERS.put(DefaultVertexFormat.ELEMENT_UV2,
                    (vertexConsumer, x, y, z, u, v, light, overlay, red, green, blue, alpha) -> {
                        vertexConsumer.uv2(light);
                    });

            ELEMENT_HANDLERS.put(DefaultVertexFormat.ELEMENT_NORMAL,
                    (vertexConsumer, x, y, z, u, v, light, overlay, red, green, blue, alpha) -> {
                        vertexConsumer.normal(0.0F, 1.0F, 0.0F);
                    });

            ELEMENT_HANDLERS.put(DefaultVertexFormat.ELEMENT_PADDING,
                    (vertexConsumer, x, y, z, u, v, light, overlay, red, green, blue, alpha) -> {
                    });
        }

        private float red = 1.0F;
        private float green = 1.0F;
        private float blue = 1.0F;
        private float alpha = 1.0F;
        private int lightMap = 15728880;
        private int overlay = OverlayTexture.NO_OVERLAY;

        private MultiBufferSource bufferSource;
        private RenderType renderType;
        private VertexConsumer vertexConsumer;
        private VertexFormat vertexFormat;
        private VertexPlacer vertexPlacer;

        private Object2ObjectOpenHashMap<Object, Consumer<TrailBufferBuilder>> modularHandlers = new Object2ObjectOpenHashMap<>();
        private int handlerAddIndex;
        private int handlerGetIndex;

        public TrailBufferBuilder() {
            this.bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        }

        public TrailBufferBuilder withBufferSource(MultiBufferSource source) {
            this.bufferSource = source;
            return this;
        }

        public TrailBufferBuilder withRenderType(RenderType type) {
            this.renderType = type;
            this.vertexFormat = type.format();
            this.vertexConsumer = this.bufferSource.getBuffer(type);
            this.setupVertexPlacer();
            return this;
        }

        public TrailBufferBuilder withVertexConsumer(VertexConsumer consumer) {
            this.vertexConsumer = consumer;
            return this;
        }

        public VertexConsumer getVertexConsumer() {
            if (this.vertexConsumer == null && this.renderType != null) {
                this.vertexConsumer = this.bufferSource.getBuffer(this.renderType);
            }
            return this.vertexConsumer;
        }

        private void setupVertexPlacer() {
            if (this.vertexFormat == null) {
                return;
            }

            ImmutableList<VertexFormatElement> elements = this.vertexFormat.getElements();
            this.vertexPlacer = (vertexConsumer, builder, x, y, z, u, v, overlay) -> {
                for (VertexFormatElement element : elements) {
                    VertexHandler handler = ELEMENT_HANDLERS.get(element);
                    if (handler != null) {
                        handler.processVertex(vertexConsumer, x, y, z, u, v,
                                builder.lightMap, overlay, builder.red, builder.green, builder.blue, builder.alpha);
                    }
                }
                vertexConsumer.endVertex();
            };
        }

        public VertexPlacer getVertexPlacer() {
            return this.vertexPlacer;
        }

        public TrailBufferBuilder addModularHandler(Consumer<TrailBufferBuilder> handler) {
            return this.addModularHandler(this.handlerAddIndex++, handler);
        }

        public TrailBufferBuilder addModularHandler(Object key, Consumer<TrailBufferBuilder> handler) {
            if (this.modularHandlers == null) {
                this.modularHandlers = new Object2ObjectOpenHashMap<>();
            }
            this.modularHandlers.put(key, handler);
            return this;
        }

        public Optional<Consumer<TrailBufferBuilder>> getNextModularHandler() {
            return Optional.ofNullable(this.modularHandlers).map(m -> m.get(this.handlerGetIndex++));
        }

        public TrailBufferBuilder withColor(float r, float g, float b, float a) {
            this.red = r;
            this.green = g;
            this.blue = b;
            this.alpha = a;
            return this;
        }

        public TrailBufferBuilder withColorRGB(float r, float g, float b) {
            this.red = r;
            this.green = g;
            this.blue = b;
            return this;
        }

        public TrailBufferBuilder withAlpha(float a) {
            this.alpha = a;
            return this;
        }

        public TrailBufferBuilder withLight(int light) {
            this.lightMap = light;
            return this;
        }

        public TrailBufferBuilder withUV(float u0, float v0, float u1, float v1) {
            return this;
        }

        public TrailBufferBuilder withOverlay(int overlay) {
            this.overlay = overlay;
            return this;
        }

        public TrailBufferBuilder renderTrailPath(PoseStack poseStack, List<TrailPosition> trailPoints,
                Function<Float, Float> widthFunction,
                Consumer<Float> vfxOperator) {
            if (trailPoints.size() < 2) {
                return this;
            }

            Matrix4f pose = poseStack.last().pose();

            List<Vector4f> transformedPositions = new ArrayList<>();
            for (TrailPosition point : trailPoints) {
                transformedPositions.add(point.transform(pose));
            }

            int segmentCount = trailPoints.size() - 1;
            TrailVertexData[] vertexDataArray = new TrailVertexData[trailPoints.size()];

            float stepSize = 1.0F / segmentCount;
            for (int i = 1; i < segmentCount; i++) {
                float progress = stepSize * i;
                float width = widthFunction.apply(progress);

                Vector4f prev = transformedPositions.get(i - 1);
                Vector4f curr = transformedPositions.get(i);
                Vector4f next = transformedPositions.get(i + 1);

                Vec2 perpOffset = computePerpendicularOffset(prev, curr, next, width);
                vertexDataArray[i] = new TrailVertexData(curr, perpOffset);
            }

            float startWidth = widthFunction.apply(0.0F);
            Vec2 startPerp = computePerpendicularOffset(
                    transformedPositions.get(0),
                    transformedPositions.get(0),
                    transformedPositions.get(1),
                    startWidth);
            vertexDataArray[0] = new TrailVertexData(transformedPositions.get(0), startPerp);

            float endWidth = widthFunction.apply(1.0F);
            Vec2 endPerp = computePerpendicularOffset(
                    transformedPositions.get(segmentCount - 1),
                    transformedPositions.get(segmentCount),
                    transformedPositions.get(segmentCount),
                    endWidth);
            vertexDataArray[segmentCount] = new TrailVertexData(transformedPositions.get(segmentCount), endPerp);

            return this.renderVertexChain(vertexDataArray, vfxOperator);
        }

        private TrailBufferBuilder renderVertexChain(TrailVertexData[] vertices, Consumer<Float> vfxOperator) {
            int count = vertices.length - 1;
            float stepIncrement = 1.0F / count;

            vfxOperator.accept(0.0F);
            float startV = Mth.lerp(stepIncrement, 0.0F, 1.0F);
            vertices[0].renderStartSegment(this.getVertexConsumer(), this,
                    0.0F, 0.0F, 1.0F, startV, this.overlay);

            for (int i = 1; i < count; i++) {
                float currentV = Mth.lerp(i * stepIncrement, 0.0F, 1.0F);
                vfxOperator.accept(currentV);
                vertices[i].renderMiddleSegment(this.getVertexConsumer(), this,
                        0.0F, currentV, 1.0F, currentV, this.overlay);
            }

            vfxOperator.accept(1.0F);
            vertices[count].renderEndSegment(this.getVertexConsumer(), this,
                    0.0F, 1.0F, 1.0F, 1.0F, this.overlay);

            return this;
        }

        public void flushBuffers() {
            RenderSystem.disableDepthTest();
            if (this.bufferSource instanceof MultiBufferSource.BufferSource bufferSourceImpl) {
                bufferSourceImpl.endBatch();
            }
            RenderSystem.enableDepthTest();
        }

        public interface VertexPlacer {
            void addVertex(VertexConsumer vertexConsumer, TrailBufferBuilder builder,
                    float x, float y, float z, float u, float v, int overlay);
        }

        private interface VertexHandler {
            void processVertex(VertexConsumer vertexConsumer, float x, float y, float z,
                    float u, float v, int light, int overlay,
                    float red, float green, float blue, float alpha);
        }
    }

    public static class TrailTaskManager {
        public final List<TrailRenderTask> pendingTasks = Collections.synchronizedList(new ArrayList<>());

        TrailTaskManager() {
            this.bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            this.withRenderType(TRAIL_RENDER_TYPE);
        }

        private MultiBufferSource bufferSource;
        private RenderType renderType;
        private VertexConsumer vertexConsumer;

        public static TrailTaskManager create() {
            return new TrailTaskManager();
        }

        public RenderType getRenderType() {
            return TRAIL_RENDER_TYPE;
        }

        public TrailBufferBuilder withRenderType(RenderType renderType) {
            RenderType actualType = renderType != null ? renderType : this.getRenderType();
            return TrailRenderPipeline.createWorldBuilder().withRenderType(actualType);
        }

        public MultiBufferSource getBufferSource() {
            return Minecraft.getInstance().renderBuffers().bufferSource();
        }

        public VertexConsumer getVertexConsumer() {
            if (this.vertexConsumer == null) {
                this.vertexConsumer = this.bufferSource.getBuffer(this.getRenderType());
            }
            return this.vertexConsumer;
        }

        public void queueTrailTask(TrailRenderTask task) {
            this.pendingTasks.add(task);
        }

        public void executeTrailRendering(PoseStack poseStack) {
            if (!this.pendingTasks.isEmpty()) {
                synchronized (this.pendingTasks) {
                    if (!this.pendingTasks.isEmpty()) {
                        if (this.renderType == null) {
                            this.renderType = TRAIL_RENDER_TYPE;
                        }

                        TrailBufferBuilder builder = TrailRenderPipeline.createWorldBuilder()
                                .withRenderType(this.renderType);

                        for (TrailRenderTask task : this.pendingTasks) {
                            task.executeTask(poseStack, builder);
                        }

                        builder.flushBuffers();
                        this.clearTasks();
                    }
                }
            }
        }

        public void clearTasks() {
            this.pendingTasks.clear();
        }

        public interface TrailRenderTask {
            void executeTask(PoseStack poseStack, TrailBufferBuilder builder);

            default void onTick() {
            }
        }
    }
}
