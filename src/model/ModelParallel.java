package model;

import java.awt.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ModelParallel extends Model {

    ExecutorService pool = Executors.newWorkStealingPool();

    @Override
    public void step() {
        // long before = System.currentTimeMillis();
        p.parallelStream().forEach(p -> p.interact(this));
        // System.out.println("Time taken: " + (System.currentTimeMillis() - before));
        // How to parallise this?
        mergeParticles();

        p.parallelStream().forEach(p -> p.move(this));

        // How to parallise this?
        updateGraphicalRepresentation();
    }

    private void updateGraphicalRepresentation() {
        ArrayList<DrawableParticle> d = new ArrayList<>();
        Color c = Color.ORANGE;

        for (Particle p : this.p) {
            d.add(new DrawableParticle((int) p.x, (int) p.y, (int) Math.sqrt(p.mass), c));
        }
        this.pDraw = d; //atomic update
    }

    public void mergeParticles() {
        Stack<Particle> deadPs = new Stack<>();

        // Not worth the overheads of putting in parallel
        for (Particle p : this.p) {
            if (!p.impacting.isEmpty()) {
                deadPs.add(p);
            }
        }
        this.p.removeAll(deadPs);

        while (!deadPs.isEmpty()) {
            Particle current = deadPs.pop();
            try {
                Set<Particle> ps = pool.submit(() -> getSingleChunck(current)).get();

                deadPs.removeAll(ps);

                this.p.add(mergeParticles(ps));
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private Set<Particle> getSingleChunck(Particle current) {
        Set<Particle> impacting = new HashSet<>();
        impacting.add(current);
        while (true) {
            Set<Particle> tmp = new HashSet<>();

            synchronized (this) {
                for (Particle pi : impacting) {
                    // Write
                    tmp.addAll(pi.impacting);
                }
            }

            boolean changed = impacting.addAll(tmp);
            if (!changed) {
                break;
            }
        }
        //now impacting have all the chunk of collapsing particles
        return impacting;
    }

    public Particle mergeParticles(Set<Particle> ps) {
        double speedX = 0;
        double speedY = 0;
        double x = 0;
        double y = 0;
        double mass = 0;
        for (Particle p : ps) {
            mass += p.mass;
        }
        for (Particle p : ps) {
            x += p.x * p.mass;
            y += p.y * p.mass;
            speedX += p.speedX * p.mass;
            speedY += p.speedY * p.mass;
        }
        x /= mass;
        y /= mass;
        speedX /= mass;
        speedY /= mass;
        return new Particle(mass, speedX, speedY, x, y);
    }
}
