public class NBody{
    // In seems a deprecated class?
    public static double readRadius(String fileName){
        In inPut = new In(fileName);
        int N = inPut.readInt();
        double radius = inPut.readDouble();
        return radius;
    }

    /** reads all planets from a file */
    public static Planet[] readPlanets(String fileName){
        In inPut = new In(fileName);
        int N = inPut.readInt();
        double radius = inPut.readDouble();
        Planet[] planets = new Planet[N];
        for (int i = 0; i < N; i += 1){
            double xP = inPut.readDouble();
            double yP = inPut.readDouble();
            double xV = inPut.readDouble();
            double yV = inPut.readDouble();
            double mass = inPut.readDouble();
            String imgFile = inPut.readString();
            planets[i] = new Planet(xP, yP, xV, yV, mass, imgFile);
        }
        return planets;
    }

    /** main */
    public static void main(String[] args){
        double T = Double.valueOf(args[0]);
        double dt = Double.valueOf(args[1]);
        String filename = args[2];
        double radius = readRadius(filename);
        Planet[] planets = readPlanets(filename);

        /* draws the canvas */
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(-radius, radius);
		StdDraw.setYscale(-radius, radius);

        /* draws the planets */
        for (Planet p: planets){
            p.draw();
        }

        /* animation */
        double curTime = 0;
        int len = planets.length;
        while (curTime < T){
            double[] xForces = new double[len];
            double[] yForces = new double[len];
            
            // update each p's parameters
            for (int i = 0; i < len; i += 1){
                xForces[i] = planets[i].calcNetForceExertedByX(planets);
                yForces[i] = planets[i].calcNetForceExertedByY(planets);
                
            }

            // draw the planets
            StdDraw.clear();
            StdDraw.picture(0, 0, "images/starfield.jpg");
            for (int i = 0; i < len; i += 1){
                planets[i].update(dt, xForces[i], yForces[i]); 
                planets[i].draw();
            }
            StdDraw.show();
            StdDraw.pause(10);
            
            curTime += dt;
        }

        /* prints the results */
        StdOut.printf("%d\n", planets.length);
        StdOut.printf("%.2e\n", radius);
        for (int i = 0; i < planets.length; i++) {
            StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
                    planets[i].xxPos, planets[i].yyPos, planets[i].xxVel,
                    planets[i].yyVel, planets[i].mass, planets[i].imgFileName);   
        }
    }
}