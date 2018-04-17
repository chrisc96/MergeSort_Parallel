package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 17/04/2018.
 */
public abstract class Model {

    public static final double size = 900; // Size of Window (width and height)
    public static final double gravitationalConstant = 0.002;
    public static final double lightSpeed = 10; //the smaller, the larger is the chunk of universe we simulate
    public static final double timeFrame = 20; //the bigger, the shorter is the time of a step

    public List<Particle> p = new ArrayList<>(); // List of particles in model
    public volatile List<DrawableParticle> pDraw = new ArrayList<>();

    public abstract void step();

}
