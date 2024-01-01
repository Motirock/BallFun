# Ball Fun

Spawn balls, watch them collide, and manipulate gravity!
Not serious, just a fun little "simulator" (not real physics)

## Controls

Hold space to turn off gravity

If realistic gravity is off, press right click to set the new center of gravity

Press left click to spawn balls (too many means lag, as you might expect)

## Constants

#### Located at top of `Game.java` (sorry that you have to recompile)

How zoomed in, with greater values meaning greater zoom
double scale = 10.0;

1.0 = full speed, decreasing slows time and makes the collisions less glitchy (>1 and it often just breaks)
double deltaTime = 0.1;

Too weak gravity and it feels to slow, too much as the simulation goes too fast or just breaks
double gravitationalConstant = 0.25;

Reccomended to have gravity so that the balls don't just spread out and do nothing. It can be fun to turn off gravity after a cluster has formed
boolean gravityOn = true;

Balls bounce of the borders of the window, losing 10% speed each time
boolean bordersOn = true;

Speed decreases for all balls by (naturalDeceleration*100)% every updates. 0.001 (0.1%) works well
double naturalDeceleration = 0.000;

Instead of the user setting the center of gravity, it is found based on the position and masses of balls
boolean realisticGravity = false;

Very inaccurate, but (subjectively) more fun gravity
boolean funGravity = false;

Radius range
double radiusMin = 0.3;
double radiusMax = 1.0;

Maximum offset in both x and y individually when spawning balls. This avoids most of balls being created on top of each other, which can be buggy
double spawnDistribution = 8.0;

Starting center of gravity
Vec2 center = new Vec2(800/scale, 450/scale);
