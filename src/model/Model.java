package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 17/04/2018.
 */
public abstract class Model {

    public List<Particle> p = new ArrayList<>(); // List of particles in model
    public volatile List<DrawableParticle> pDraw = new ArrayList<>(); //

    public abstract void step();

}
