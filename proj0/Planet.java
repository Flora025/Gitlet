public class Planet{
    public double xxPos;
    public double yyPos;
    public double xxVel;
    public double yyVel;
    public double mass;
    public String imgFileName;
    private static final double G = 6.67e-11;

    public Planet(double xP, double yP, double xV, double yV, double m, String img){
        xxPos = xP;
        yyPos = yP;
        xxVel = xV;
        yyVel = yV;
        mass = m;
        imgFileName = img;
    }
    
    public Planet(Planet p){
        xxPos = p.xxPos;
        yyPos = p.yyPos;
        xxVel = p.xxVel;
        yyVel = p.yyVel;
        mass = p.mass;
        imgFileName = p.imgFileName;
    }

    /** calculates the distance between planets */
    public double calcDistance(Planet pOther){
        double distanceSqr = (this.xxPos - pOther.xxPos)* (this.xxPos - pOther.xxPos)
            + (this.yyPos - pOther.yyPos)*(this.yyPos - pOther.yyPos);
        return Math.sqrt(distanceSqr);
    }

    /** calculates force exerted on the planet */
    public double calcForceExertedBy(Planet p){
        double r = this.calcDistance(p);
        return (G * p.mass * this.mass) / (r * r);
    }

    /** calculates force exerted on the x axis of this planet by another planet*/
    public double calcForceExertedByX(Planet p){
        return calcForceExertedBy(p) * (p.xxPos - this.xxPos) / 
                this.calcDistance(p);
    }

    /** calculates force exerted on the y axis of this planet by another planet*/
    public double calcForceExertedByY(Planet p){
        return calcForceExertedBy(p) * (p.yyPos - this.yyPos) / 
                this.calcDistance(p);
    }

    /** calcs net forces exerted on x axis */
    public double calcNetForceExertedByX(Planet[] allPlanets){
        double sumX = 0;
        for (Planet p : allPlanets){
            if (this.equals(p)){
                continue;
            }
            sumX += calcForceExertedByX(p);
        }
        return sumX;
    }

    /** calcs net forces exerted on y axis */
    public double calcNetForceExertedByY(Planet[] allPlanets){
        double sumY = 0;
        for (Planet p : allPlanets){
            if (this.equals(p)){
                continue;
            }
            sumY += calcForceExertedByY(p);
        }
        return sumY;
    }
	
    /** updates the position of this planet */
    public void update(double dt, double fx, double fy){
        double ax = fx / mass;
        double ay = fy / mass;
        xxVel += dt * ax;
        yyVel += dt * ay;
        xxPos += dt * xxVel;
        yyPos += dt * yyVel;
    }

    public void draw(){
        StdDraw.picture(xxPos, yyPos, "images/"+imgFileName);
    }
}