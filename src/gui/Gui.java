package gui;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.*;

import datasets.DataSetLoader;
import datasets.ModelFactory;
import model.Model;

public class Gui extends JFrame implements Runnable {
    private static int frameTime = 5; //use a bigger or smaller number for faster/slower simulation
    private static int stepsForFrame = 60; //use a bigger or smaller number for faster/slower simulation
    //it will attempt to do 4 steps every 20 milliseconds (less if the machine is too slow)

    // One thread for the GUI and one for the main loop
    public static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    Model m;

    Gui(Model m) {
        this.m = m;
    }

    public void run() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getRootPane().setLayout(new BorderLayout());
        JPanel p = new Canvas(m);
        getRootPane().add(p, BorderLayout.CENTER);
        pack();
        setVisible(true);
        scheduler.scheduleAtFixedRate(
                () -> SwingUtilities.invokeLater(this::repaint),
                20, 1, TimeUnit.MILLISECONDS
        );
    }

    private static final class MainLoop implements Runnable {
        Model m;

        MainLoop(Model m) {
            this.m = m;
        }

        public void run() {
            try {
                while (true) {
                    long ut = System.currentTimeMillis();
                    // 60 frames. step() is like the tick() method
                    for (int i = 0; i < stepsForFrame; i++) {
                        m.step();
                    }
                    ut = System.currentTimeMillis() - ut; // used time for n stepsForFrame

                    System.out.println("Particles: "+m.p.size()+" time:"+ut+"ms"); //if you want to have an idea of the time consumption

                    long sleepTime = frameTime - ut;
                    if (sleepTime > 1) {
                        Thread.sleep(sleepTime); // Wait until readyToRefresh...
                    }
                } //if the step was short enough, it wait to make it at least frameTime long.
            }
            catch (Throwable t) { //not a perfect solution, but
                t.printStackTrace();//makes sure you see the error and the program dies.
                System.exit(0);//the "right" solution is much more involved
            }//and would require storing and passing the exception between different objects.
        }
    }

    public static void main(String[] args) {
        // Sequential Models

        // Model m = DataSetLoader.getRegularGrid(100, 800, 40, ModelFactory.ModelType.SEQUENTIAL);
        // Model m = DataSetLoader.getRandomRotatingGrid(100, 800, 40, ModelFactory.ModelType.SEQUENTIAL);

        Model m = DataSetLoader.getRandomSet(100, 800, 1000, ModelFactory.ModelType.SEQUENTIAL);

        // Model m = DataSetLoader.getRandomSet(100, 800, 100, ModelFactory.ModelType.SEQUENTIAL);
        // Model m = DataSetLoader.getRandomGrid(100, 800, 30, ModelFactory.ModelType.SEQUENTIAL);

        // Parallel Models

        // Model m = DataSetLoader.getRegularGrid(100, 800, 40, ModelFactory.ModelType.PARALLEL);
        // Model m = DataSetLoader.getRandomRotatingGrid(100, 800, 40, ModelFactory.ModelType.PARALLEL);

        // Model m = DataSetLoader.getRandomSet(100, 800, 1000, ModelFactory.ModelType.PARALLEL);

        // Model m = DataSetLoader.getRandomSet(100, 800, 100, ModelFactory.ModelType.PARALLEL);
        // Model m = DataSetLoader.getRandomGrid(100, 800, 30, ModelFactory.ModelType.PARALLEL);

        scheduler.schedule(new MainLoop(m), 500, TimeUnit.MILLISECONDS);
        SwingUtilities.invokeLater(new Gui(m));
    }
}