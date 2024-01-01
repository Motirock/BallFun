package game;

public class Particle {
    //Vec2 previousPosition;
    Vec2 position;
    Vec2 velocity;
    double radius;
    double mass;

    public Particle(Vec2 position, Vec2 velocity, double radius, double mass) {
        this.position = position;
        this.velocity = velocity;
        this.radius = radius;
        this.mass = mass;
    }

    //public void updatePreviousPosition() {
    //    this.previousPosition = position;
    //}

    public double getDistance(Particle other) {
        return this.position.subtract(other.position).magnitude();
    }


}
