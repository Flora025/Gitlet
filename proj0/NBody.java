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
}