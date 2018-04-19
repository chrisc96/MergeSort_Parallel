package tests;


import model.Model;
import model.ModelParallel;
import model.ModelSequential;
import org.junit.Test;

import static datasets.DataSetLoader.getRegularGrid;

public class CompareExecutionTests {

    long timeOf(Runnable r, int warmUp, int runs) {
        System.gc();
        for (int i = 0; i < warmUp; i++) {
            r.run();
        }
        long time0 = System.currentTimeMillis();
        for (int i = 0; i < runs; i++) {
            r.run();
        }
        long time1 = System.currentTimeMillis();
        return time1 - time0;
    }

    @Test
    public void Compare_01() {
        Model mPar = getRegularGrid(100, 800, 40, ModelParallel::new);
        Model mSeq = getRegularGrid(100, 800, 40, ModelSequential::new);


    }

}
