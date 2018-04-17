package model;

import java.util.HashSet;
import java.util.Set;

public class Particle {
    public Particle(double mass, double speedX, double speedY, double x, double y) {
        this.mass = mass;
        this.speedX = speedX;
        this.speedY = speedY;
        this.x = x;
        this.y = y;
    }

    public Set<Particle> impacting = new HashSet<>();
    public double mass;
    public double speedX;
    public double speedY;
    public double x;
    public double y;

    public void move(SeqModel m) {
        x += speedX / (SeqModel.timeFrame);
        y += speedY / (SeqModel.timeFrame);
        //uncomment the following to have particle bouncing on the boundary
        if(this.x<0){this.speedX*=-1;}
        if(this.y<0){this.speedY*=-1;}
        if(this.x> SeqModel.size){this.speedX*=-1;}
        if(this.y> SeqModel.size){this.speedY*=-1;}
    }

    public boolean isImpact(double dist, double otherMass) {
        if (Double.isNaN(dist)) {
            return true;
        }
        double distMass = Math.sqrt(mass) + Math.sqrt(otherMass);
        if (dist < distMass * distMass) {
            return true;
        }
        return false;
    }

    public boolean isImpact(Iterable<Particle> ps) {
        for (Particle p : ps) {
            if (this == p) {
                continue;
            }
            double dist = distance2(p);
            if (isImpact(dist, p.mass)) {
                return true;
            }
        }
        return false;
    }

    public double distance2(Particle p) {
        double distX = this.x - p.x;
        double distY = this.y - p.y;
        return distX * distX + distY * distY;
    }

    public void interact(SeqModel m) {
        for (Particle p : m.p) {
            if (p == this) continue;
            double dirX = -Math.signum(this.x - p.x);
            double dirY = -Math.signum(this.y - p.y);
            double dist = distance2(p);
            if (isImpact(dist, p.mass)) {
                this.impacting.add(p);
                continue;
            }
            dirX = p.mass * SeqModel.gravitationalConstant * dirX / dist;
            dirY = p.mass * SeqModel.gravitationalConstant * dirY / dist;
            assert this.speedX <= SeqModel.lightSpeed : this.speedX;
            assert this.speedY <= SeqModel.lightSpeed : this.speedY;
            double newSpeedX = this.speedX + dirX;
            newSpeedX /= (1 + (this.speedX * dirX) / SeqModel.lightSpeed);
            double newSpeedY = this.speedY + dirY;
            newSpeedY /= (1 + (this.speedY * dirY) / SeqModel.lightSpeed);
            if (!Double.isNaN(dirX)) {
                this.speedX = newSpeedX;
            }
            if (!Double.isNaN(dirY)) {
                this.speedY = newSpeedY;
            }
        }
    }
}
