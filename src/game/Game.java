package game;

//Necessary for input
import main.GamePanel;

//Graphics
import java.awt.Graphics2D;
import java.awt.Color;

//ArrayList used to store the indefinite number of balls
import java.util.ArrayList;

public class Game {
    //Allows access to input. Also GamePanel runs the Game class
    private GamePanel gp;

    //Total times update() has been run
    private long updates = 0;

    //How zoomed in, with greater values meaning greater zoom
    double scale = 10.0;
    //1.0 = full speed, decreasing slows time and makes the collisions less glitchy (>1 and it often just breaks)
    double deltaTime = 0.1;
    //Too weak gravity and it feels to slow, too much as the simulation goes too fast or just breaks
    double gravitationalConstant = 0.25;
    //Reccomended to have gravity so that the balls don't just spread out and do nothing. It can be fun to turn off gravity after a cluster has formed
    boolean gravityOn = true;
    //Balls bounce of the borders of the window, losing 10% speed each time
    boolean bordersOn = true;
    //Speed decreases for all balls by (naturalDeceleration*100)% every updates. 0.001 (0.1%) works well
    double naturalDeceleration = 0.000;
    //Instead of the user setting the center of gravity, it is found based on the position and masses of balls
    boolean realisticGravity = false;
    //Very inaccurate, but (subjectively) more fun gravity
    boolean funGravity = false;
    //Radius range
    double radiusMin = 0.3;
    double radiusMax = 1.0;
    //Maximum offset in both x and y individually when spawning balls. This avoids most of balls being created on top of each other, which can be buggy
    double spawnDistribution = 8.0;
    //Starting center of gravity
    Vec2 center = new Vec2(800/scale, 450/scale);

    ArrayList<Ball> balls = new ArrayList<Ball>();

    //gp, the GamePanel object is used to access the mouse and keyboard
    public Game(GamePanel gp) {
        this.gp = gp;
    }

    //Sums and returns the mass of the balls all together
    public double sumMass() {
        double r = 0.0;
        for (Ball ball : balls) {
            r += ball.mass;
        }
        return r;
    }

    //Find the point that all balls gravitate towards
    //This is not very accurate, but is a lot more efficient and works well once it has formed into a cluster
    public Vec2 findGravitationalCenter() {
        //Vector to be returned
        Vec2 r = new Vec2(0, 0);

        double totalMass = sumMass();

        //Sum the positions with the mass as a scalar (more mass = greater impact)
        for (Ball ball : balls) {
            r = r.add(ball.position.scalarMultiply(ball.mass));
        }

        //Divide by total mass to find weighted average, AKA our approximation of a universal center
        return r.scalarMultiply(1/totalMass);
    }

    //Logical updates
    public void update() {
        //Spawning balls
        if (gp.mouseLeftPressed && gp.mouseX != 0 && updates % 1 == 0) {
            //Random radius based on radiusMin and radiusMax
            double radius = Math.random()*(radiusMax-radiusMin)+radiusMin;
            //Spawn-distribution: maximum offset in both x and y individually. This avoids most of balls spawning on top of each other, which can be buggy
            balls.add(new Ball(new Vec2(gp.mouseX/scale-spawnDistribution+2*Math.random()*spawnDistribution, gp.mouseY/scale-spawnDistribution+2*Math.random()*spawnDistribution), new Vec2(0.0, 0.0), radius, Math.PI*radius*radius));
        }
        //Changing gravity
        if (gp.mouseRightPressed && !realisticGravity)
            center = new Vec2(gp.mouseX/scale, gp.mouseY/scale);
        //Holding space = keeping gravity off
        if (gp.keyH.spacePressed)
            gravityOn = false;
        //Gravity on by default/when space is not pressed
        else
            gravityOn = true;

        //Finding the gravitional center. This is just a heuristic; Newton's law of universal gravitation is too slow
        if (realisticGravity && balls.size() > 0)//updates % (int) (Math.pow(balls.size(), 0.5)) == 0) //<-- this skips finding the center most updates to be more efficient
            center = findGravitationalCenter();

        //Updating ball. None of this requires considering other balls
        for (Ball ball : balls) {
            if (ball.position.isNaN()) {
                balls.clear();
                break;
            }

            //Gravity
            if (gravityOn) {
                Vec2 fromCenter = center.subtract(ball.position).normalize();
                double distanceFromCenter = center.subtract(ball.position).magnitude();

                //Silly gravity that is more fun IMO
                if (funGravity) {
                    double gravityScalar = 1/Math.pow(distanceFromCenter+1, 0.5);
                    ball.velocity = ball.velocity.add(fromCenter.scalarMultiply(gravitationalConstant/10.0*gravityScalar));
                }
                //Realistic gravity, but a bit softened so that it doesn't go crazy as distanceFromCenter approaches 0
                else {
                    double gravityScalar = 1/Math.pow(distanceFromCenter+1, 2);
                    ball.velocity = ball.velocity.add(fromCenter.scalarMultiply(gravitationalConstant*gravityScalar));
                }
            }
            
            ball.position.x += ball.velocity.x*deltaTime;
            ball.position.y += ball.velocity.y*deltaTime;
            
            //Border collisions
            if (bordersOn) {
                //Up
                if (ball.position.y-ball.radius <= 0) {
                    ball.position.y = ball.radius;
                    ball.velocity.y *= -0.9; 
                }
                //Down
                else if (ball.position.y+ball.radius >= 900/scale) {
                    ball.position.y = 900/scale-ball.radius;
                    ball.velocity.y *= -0.9; 
                }
                //Left
                if (ball.position.x-ball.radius <= 0) {
                    ball.position.x = ball.radius;
                    ball.velocity.x *= -0.9; 
                }
                //Right
                else if (ball.position.x+ball.radius >= 1600/scale) {
                    ball.position.x = 1600/scale-ball.radius;
                    ball.velocity.x *= -0.9; 
                }
            }

            //Decelerating over time (does nothing if nautralDeceleration is 0)
            ball.velocity = ball.velocity.scalarMultiply(1.0-naturalDeceleration);
        }

        //Collision checks
        for (int i = 0; i < balls.size(); i++) {
            Ball ball = balls.get(i);

            //Starts at i+1 so as to not repeat checks
            for (int o = i+1; o < balls.size(); o++) {
                Ball other = balls.get(o);
                //If overlapping
                if (ball.getDistance(other) < ball.radius+other.radius) {
                    //Vector representing difference between ball centers
                    Vec2 difference = ball.position.subtract(other.position);

                    //Exception if position is the same
                    if (difference.magnitude() == 0.0)
                        difference = new Vec2(0.01, 0.01);

                    //Updating velocity (this is not real physics btw)
                    //TODO make mass play a rôle
                    ball.velocity = ball.velocity.add(difference.scalarMultiply(((ball.radius+other.radius)-difference.magnitude())*1.0/2.0));
                    other.velocity = other.velocity.add(difference.scalarMultiply(((ball.radius+other.radius)-difference.magnitude())*-1.0/2.0));
                    
                    //Updating position
                    ball.position = ball.position.add(difference.scalarMultiply(((ball.radius+other.radius)-difference.magnitude())/2.0));
                    other.position = other.position.subtract(difference.scalarMultiply(((ball.radius+other.radius)-difference.magnitude())/2.0));
                }
            }
        }

        //Keeps track of total times update() was called
        updates++;
    }
    
    //Graphical updates
    //GS = graphics scaling. Works assuming the window dimensions are 16:9
    public void draw(Graphics2D g2, double GS) {
        //Background
        g2.setColor(new Color(100, 100, 100));
        g2.fillRect(0, 0, (int) (1600*GS), (int) (900*GS));

        //Drawing each ball
        for (int i = 0; i < balls.size(); i++) {
            Ball ball = balls.get(i);

            //Color based on radius
            //Converted radius: how large it is compared to the other possible radiuses
            double convertedRadius = (ball.radius-radiusMin)/(radiusMax-radiusMin);
            //Shifts from orange to purple to blue as radius increases
            g2.setColor(new Color((int) (255-255*convertedRadius), (int) (100-100*convertedRadius), (int) (255*convertedRadius*convertedRadius)));

            g2.fillOval((int) ((ball.position.x-ball.radius)*GS*scale), (int) ((ball.position.y-ball.radius)*GS*scale), (int) (ball.radius*2*GS*scale), (int) (ball.radius*2*GS*scale));
        }
    }
}