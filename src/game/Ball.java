package game;

public class Ball {
    Vec2 position;
    Vec2 velocity;
    double radius;
    //m = πr^2 (area)
    double mass;

    public Ball(Vec2 position, Vec2 velocity, double radius, double mass) {
        this.position = position;
        this.velocity = velocity;
        this.radius = radius;
        this.mass = mass;
    }

    //Returns the distance between two balls centers
    public double getDistance(Ball other) {
        return this.position.subtract(other.position).magnitude();
    }
}
