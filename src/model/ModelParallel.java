package model;

import java.awt.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ModelParallel extends Model {

    @Override
    public void step() {
        // Main step
        p.parallelStream().forEach(p -> p.interact(this));

        mergeParticles();

        p.parallelStream().forEach(p -> p.move(this));

        updateGraphicalRepresentation();
    }

    public void updateGraphicalRepresentation() {
        LinkedBlockingDeque<DrawableParticle> d = new LinkedBlockingDeque<>();

        Color c = Color.ORANGE;
        p.parallelStream().forEach(pa -> d.add(new DrawableParticle((int) pa.x, (int) pa.y, (int) Math.sqrt(pa.mass), c)));
        this.pDraw = d.stream().collect(Collectors.toList());
    }

    public void mergeParticles() {
        // Uses a lock so we're thread safe when adding to this
        LinkedBlockingDeque<Particle> deadPs = new LinkedBlockingDeque<>();

        p.parallelStream().forEach(pa -> {
            if (!pa.impacting.isEmpty()) {
                deadPs.add(pa);
            }
        });
        p.removeAll(deadPs);

        while (!deadPs.isEmpty()) {
            Particle finalCurrent = deadPs.getFirst();
            Set<Particle> ps;
            ps = getSingleChunck(finalCurrent);
            deadPs.removeAll(ps);
            this.p.add(mergeParticles(ps));
        }
    }

    private Set<Particle> getSingleChunck(Particle current) {
        Set<Particle> impacting = new HashSet<>();
        impacting.add(current);
        while (true) {
            Set<Particle> tmp = new HashSet<>();

            impacting.parallelStream().forEach(pi -> tmp.addAll(pi.impacting));

            boolean changed = impacting.addAll(tmp);
            if (!changed) {
                break;
            }
        }
        //now impacting have all the chunk of collapsing particles
        return impacting;
    }

    private Particle mergeParticles(Set<Particle> ps) {
        return new ModelSequential().mergeParticles(ps);
    }
}
