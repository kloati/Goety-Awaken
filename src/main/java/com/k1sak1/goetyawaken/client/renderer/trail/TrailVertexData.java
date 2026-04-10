package com.k1sak1.goetyawaken.client.renderer.trail;

import com.k1sak1.goetyawaken.client.renderer.TrailRenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec2;
import org.joml.Vector4f;

public class TrailVertexData {

    private final float xPositive;
    private final float xNegative;
    private final float yPositive;
    private final float yNegative;
    private final float zCoord;

    public TrailVertexData(float xPositive, float xNegative, float yPositive, float yNegative, float zCoord) {
        this.xPositive = xPositive;
        this.xNegative = xNegative;
        this.yPositive = yPositive;
        this.yNegative = yNegative;
        this.zCoord = zCoord;
    }

    public TrailVertexData(Vector4f position, Vec2 perpendicular) {
        this.xPositive = position.x() + perpendicular.x;
        this.xNegative = position.x() - perpendicular.x;
        this.yPositive = position.y() + perpendicular.y;
        this.yNegative = position.y() - perpendicular.y;
        this.zCoord = position.z();
    }

    public void renderStartSegment(VertexConsumer vertexConsumer, TrailRenderPipeline.TrailBufferBuilder builder,
            float uStart, float vStart, float uEnd, float vEnd, int overlay) {
        if (builder.getVertexPlacer() != null) {
            builder.getVertexPlacer().addVertex(vertexConsumer, builder,
                    this.xPositive, this.yPositive, this.zCoord, uStart, vStart, overlay);
            builder.getVertexPlacer().addVertex(vertexConsumer, builder,
                    this.xNegative, this.yNegative, this.zCoord, uEnd, vStart, overlay);
        }
    }

    public void renderEndSegment(VertexConsumer vertexConsumer, TrailRenderPipeline.TrailBufferBuilder builder,
            float uStart, float vStart, float uEnd, float vEnd, int overlay) {
        if (builder.getVertexPlacer() != null) {
            builder.getVertexPlacer().addVertex(vertexConsumer, builder,
                    this.xNegative, this.yNegative, this.zCoord, uEnd, vEnd, overlay);
            builder.getVertexPlacer().addVertex(vertexConsumer, builder,
                    this.xPositive, this.yPositive, this.zCoord, uStart, vEnd, overlay);
        }
    }

    public void renderMiddleSegment(VertexConsumer vertexConsumer, TrailRenderPipeline.TrailBufferBuilder builder,
            float uStart, float vStart, float uEnd, float vEnd, int overlay) {
        this.renderEndSegment(vertexConsumer, builder, uStart, vStart, uEnd, vEnd, overlay);
        this.renderStartSegment(vertexConsumer, builder, uStart, vStart, uEnd, vEnd, overlay);
    }

    public float getXPositive() {
        return this.xPositive;
    }

    public float getXNegative() {
        return this.xNegative;
    }

    public float getYPositive() {
        return this.yPositive;
    }

    public float getYNegative() {
        return this.yNegative;
    }

    public float getZCoord() {
        return this.zCoord;
    }
}
