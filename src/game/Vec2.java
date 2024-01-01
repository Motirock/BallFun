package game;

public class Vec2 {
    double x, y;

    public Vec2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double magnitude() {
        return Math.sqrt(x*x+y*y);
    }

    public Vec2 add(Vec2 other) {
        return new Vec2(this.x+other.x, this.y+other.y);
    }

    public Vec2 subtract(Vec2 other) {
        return new Vec2(this.x-other.x, this.y-other.y);
    }

    public Vec2 scalarMultiply(double scalar) {
        return new Vec2(scalar*this.x, scalar*this.y);
    }

    public double dotProduct(Vec2 other) {
        return this.x*other.x+this.y*other.y;
    }

    public Vec2 abs() {
        return new Vec2(Math.abs(this.x), Math.abs(this.y));
    }

    public Vec2 normalize() {
        return new Vec2(this.x/this.magnitude(), this.y/this.magnitude());
    }

    public String toString() {
        return "〈"+x+", "+y+"〉";
    }

    public boolean isNaN() {
        return Double.isNaN(x) || Double.isNaN(y);
    }
}
