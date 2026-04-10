package com.k1sak1.goetyawaken.client.renderer.trail;

import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

/**
 * Inspired by perception
 * 
 * @author SSKirillSS(Original Author)
 * @see <a href=
 *       "https://github.com/Octo-Studios/perception/tree/master">perception
 *             Repository</a>  
 */
public class TrailPosition {

    private final double x;
    private final double y;
    private final double z;
    private int age;

    public TrailPosition(double x, double y, double z, int age) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.age = age;
    }

    public TrailPosition(Vec3 position) {
        this(position.x, position.y, position.z, 0);
    }

    public TrailPosition(Vec3 position, int age) {
        this(position.x, position.y, position.z, age);
    }

    public Vec3 getPosition() {
        return new Vec3(this.x, this.y, this.z);
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    public double z() {
        return this.z;
    }

    public int getAge() {
        return this.age;
    }

    public void tick() {
        ++this.age;
    }

    public Vector4f toVector4f() {
        return new Vector4f((float) this.x, (float) this.y, (float) this.z, 1.0F);
    }

    public Vector4f transform(Matrix4f matrix) {
        Vector4f vector = this.toVector4f();
        return vector.mul(matrix);
    }

    public TrailPosition interpolate(TrailPosition target, float delta) {
        double interpX = net.minecraft.util.Mth.lerp(delta, this.x, target.x);
        double interpY = net.minecraft.util.Mth.lerp(delta, this.y, target.y);
        double interpZ = net.minecraft.util.Mth.lerp(delta, this.z, target.z);
        return new TrailPosition(interpX, interpY, interpZ, this.age);
    }

    public TrailPosition interpolate(Vec3 target, float delta) {
        double interpX = net.minecraft.util.Mth.lerp(delta, this.x, target.x);
        double interpY = net.minecraft.util.Mth.lerp(delta, this.y, target.y);
        double interpZ = net.minecraft.util.Mth.lerp(delta, this.z, target.z);
        return new TrailPosition(interpX, interpY, interpZ, this.age);
    }

    public double distanceToSqr(TrailPosition other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dz = this.z - other.z;
        return dx * dx + dy * dy + dz * dz;
    }

    @Override
    public String toString() {
        return "TrailPosition{x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", age=" + this.age + "}";
    }
}
