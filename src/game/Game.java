package game;

import main.GamePanel;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.io.IOException;
import java.awt.Color;

import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Game {
    private GamePanel gp;

    private long updates = 0; //Total times update() has been run

    double scale = 10.0;

    //A BufferedImage which does not store anything yet
    public BufferedImage particleImage = null;
    ArrayList<Particle> particles = new ArrayList<Particle>();

    public Game(GamePanel gp) {
        this.gp = gp;
        
        //Loads the chest image from the res folder to image
        try {
            particleImage = ImageIO.read(getClass().getResourceAsStream("/res/tennis_ball.png"));
        } catch (IOException e) {
            //Prints the error and the stack trace if it fails to load the image from res
            e.printStackTrace();
        }
    }

    public double sumMass() {
        double r = 0.0;
        for (Particle particle : particles) {
            r += particle.mass;
        }
        return r;
    }

    public Vec2 findGravitationalCenter() {
        Vec2 r = new Vec2(0, 0);
        double totalMass = sumMass();
        for (Particle particle : particles) {
            r = r.add(particle.position.scalarMultiply(particle.mass));
        }
        return r.scalarMultiply(1/totalMass);
    }

    double deltaTime = 0.1;
    double gravitationalConstant = 0.25;
    boolean gravityOn = true;
    boolean bordersOn = true;
    double naturalDeceleration = 0.000;
    boolean realisticGravity = false;
    boolean funGravity = false;
    double radiusMin = 0.3;
    double radiusMax = 1.0;
    double spawnDistribution = 8.0;
    Vec2 center = new Vec2(800/scale, 450/scale);
    public void update() {

        if (gp.mouseLeftPressed && gp.mouseX != 0 && updates % 1 == 0) {
            double radius = Math.random()*(radiusMax-radiusMin)+radiusMin;
            particles.add(new Particle(new Vec2(gp.mouseX/scale-spawnDistribution+2*Math.random()*spawnDistribution, gp.mouseY/scale-spawnDistribution+2*Math.random()*spawnDistribution), new Vec2(0.0, 0.0), radius, Math.PI*radius*radius));
        }
        if (gp.mouseRightPressed) {
            if (!realisticGravity)
                center = new Vec2(gp.mouseX/scale, gp.mouseY/scale);
        }
        if (gp.keyH.spacePressed) {
            gravityOn = false;
        }
        else {
            gravityOn = true;
        }

        if (realisticGravity && particles.size() > 0)//updates % (int) (Math.pow(particles.size(), 0.5)) == 0)
            center = findGravitationalCenter();

        for (Particle particle : particles) {
            if (particle.position.isNaN()) {
                particles.clear();
                break;
            }

            //Gravity
            if (gravityOn) {
                Vec2 fromCenter = center.subtract(particle.position).normalize();
                double distanceFromCenter = center.subtract(particle.position).magnitude();
                if (funGravity) {
                    double gravityScalar = 1/Math.pow(distanceFromCenter+1, 0.5);
                    particle.velocity = particle.velocity.add(fromCenter.scalarMultiply(gravitationalConstant/10.0*gravityScalar));
                }
                else {
                    double gravityScalar = 1/Math.pow(distanceFromCenter+1, 2);
                    particle.velocity = particle.velocity.add(fromCenter.scalarMultiply(gravitationalConstant*gravityScalar));
                }
            }
            
            particle.position.x += particle.velocity.x*deltaTime;
            particle.position.y += particle.velocity.y*deltaTime;

            if (bordersOn) {
                if (particle.position.x-particle.radius <= 0) {
                    particle.position.x = particle.radius;
                    particle.velocity.x *= -0.9; 
                }
                else if (particle.position.x+particle.radius >= 1600/scale) {
                    particle.position.x = 1600/scale-particle.radius;
                    particle.velocity.x *= -0.9; 
                }
                if (particle.position.y-particle.radius <= 0) {
                    particle.position.y = particle.radius;
                    particle.velocity.y *= -0.9; 
                }
                else if (particle.position.y+particle.radius >= 900/scale) {
                    particle.position.y = 900/scale-particle.radius;
                    particle.velocity.y *= -0.9; 
                }
            }

            //Decelerating over time
            particle.velocity = particle.velocity.scalarMultiply(1.0-naturalDeceleration);
        }

        for (int i = 0; i < particles.size(); i++) {
            Particle particle = particles.get(i);

            for (int o = i+1; o < particles.size(); o++) {
                Particle other = particles.get(o);
                if (particle.getDistance(other) < particle.radius+other.radius) {
                    Vec2 difference = particle.position.subtract(other.position);
                    if (difference.magnitude() == 0.0)
                        difference = new Vec2(0.01, 0.01);
                    particle.velocity = particle.velocity.add(difference.scalarMultiply(((particle.radius+other.radius)-difference.magnitude())*1.0/2.0));
                    other.velocity = other.velocity.add(difference.scalarMultiply(((particle.radius+other.radius)-difference.magnitude())*-1.0/2.0));
                    // if (particle.mass >= other.mass) {
                    //     particle.velocity = particle.velocity.add(difference.scalarMultiply(((particle.radius+other.radius)-difference.magnitude())*1.0/(particle.mass/other.mass)));
                    //     other.velocity = other.velocity.add(difference.scalarMultiply(((particle.radius+other.radius)-difference.magnitude())*-1.0));
                    // }
                    // else {
                    //     particle.velocity = particle.velocity.add(difference.scalarMultiply(((particle.radius+other.radius)-difference.magnitude())*1.0));
                    //     other.velocity = other.velocity.add(difference.scalarMultiply(((particle.radius+other.radius)-difference.magnitude())*-1.0/(other.mass/particle.mass)));
                    // }
                    // double dotProduct = Math.abs(particle.velocity.dotProduct(other.velocity));
                    // double convertedDot = 1.0-dotProduct/(particle.velocity.abs().add(other.velocity.abs()).magnitude());
                    //particle.velocity = particle.velocity.add(other.velocity.scalarMultiply(convertedDot));
                    //other.velocity = other.velocity.add(particle.velocity.scalarMultiply(convertedDot));
                    //particle.velocity = particle.velocity.add(difference.scalarMultiply(((particle.radius+other.radius)-difference.magnitude())*1.0/particle.mass*other.mass));
                    //other.velocity = other.velocity.add(difference.scalarMultiply(((particle.radius+other.radius)-difference.magnitude())*-1.0/other.mass*particle.mass));
                    particle.position = particle.position.add(difference.scalarMultiply(((particle.radius+other.radius)-difference.magnitude())/2.0));
                    other.position = other.position.subtract(difference.scalarMultiply(((particle.radius+other.radius)-difference.magnitude())/2.0));
                }
            }
        }

        //Keeps track of total times update() was called
        updates++;
    }
    
    public void draw(Graphics2D g2, double GS) {
        g2.setColor(new Color(100, 100, 100));
        g2.fillRect(0, 0, (int) (1600*GS), (int) (900*GS));
        for (int i = 0; i < particles.size(); i++) {
            Particle particle = particles.get(i);
            //g2.setColor(new Color(255*i, 255-255*i, 0));
            double convertedRadius = (particle.radius-radiusMin)/(radiusMax-radiusMin);
            g2.setColor(new Color((int) (255-255*convertedRadius), (int) (100-100*convertedRadius), (int) (255*convertedRadius*convertedRadius)));
            g2.fillOval((int) ((particle.position.x-particle.radius)*GS*scale), (int) ((particle.position.y-particle.radius)*GS*scale), (int) (particle.radius*2*GS*scale), (int) (particle.radius*2*GS*scale));
            //g2.drawImage(particleImage, (int) ((particle.position.x-particle.radius)*GS*scale), (int) ((particle.position.y-particle.radius)*GS*scale), (int) (particle.radius*2*GS*scale), (int) (particle.radius*2*GS*scale), null);
            //g2.drawLine((int) ((particle.position.x)*GS*scale), (int) ((particle.position.y)*GS*scale), (int) ((particle.position.x+particle.velocity.x*10)*GS*scale), (int) ((particle.position.y+particle.velocity.y*10)*GS*scale));
        }
    }
}