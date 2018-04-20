package tests;

import model.Model;
import model.ModelParallel;
import model.ModelParallelOptimised;
import model.ModelSequential;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static tests.CompareExecutionTests.maxParticleValue;
import static tests.CompareExecutionTests.createModelFromDataSet;
import static tests.CompareExecutionTests.generateUniformDataSet;

public class CorrectnessTests {

    // Need to make sure each method does what it says it does for each model.



    /**
     * This test method creates an instance of each execution method
     * with a simulation size of a 100 in a 1024 x 1024 grid
     *
     * 'p' and 'pdraw' are constantly being modified. This method checks that
     * what is drawn and what is held inside the 'p' list is always consistent at the end
     * of the step method.
     *
     * This is run @runsToTest number of times to try find caching problems (more than one
     * execution would)
     */
    @Test
    public void checkCachingSharedData(){
        int simulationSize = 100;

        int stopSimulationAt = 25;
        int runsToTest = 5;
        int runCount = 0;

        // Do the inner loop runsToTest times to try be sure for caching issues
        while (runCount < runsToTest) {
            Model mPar = createModelFromDataSet(generateUniformDataSet(simulationSize, maxParticleValue), ModelParallel::new);
            Model mParOpt = createModelFromDataSet(generateUniformDataSet(simulationSize, maxParticleValue), ModelParallelOptimised::new);
            Model mSeq = createModelFromDataSet(generateUniformDataSet(simulationSize, maxParticleValue), ModelSequential::new);

            // Checks all the data sets are the same accross executions
            assertTrue(assertListsEqual(mSeq,mPar));
            assertTrue(assertListsEqual(mPar,mParOpt));

            // Loop until the size of the simulation is at stopSimulationAt (the last few particles take way
            // too long to merge - they just orbit the central one forever)...
            while (mPar.p.size() > stopSimulationAt) {

                mSeq.step();
                assertPdrawAndPEqual(mSeq);

                mPar.step();
                assertPdrawAndPEqual(mPar);

                mParOpt.step();
                assertPdrawAndPEqual(mParOpt);

            }
            runCount++;
        }
    }


    /**
     * Checks whether the list of particles in @param m1 are the
     * exact same as the list of particles in @param m2 at a given
     * instant.
     * @param m1
     * @param m2
     * @return
     */
    public boolean assertListsEqual(Model m1, Model m2){
        if(m1.p.size() != m2.p.size()) return false;
        for(int i = 0; i < m1.p.size(); i++){
            if(!m1.p.get(i).equals(m2.p.get(i))){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the list of drawable particles and
     * list of particles used in the models are the exact same at
     * a given instant.
     * @param m1 model to test on
     * @return
     */
    public boolean assertPdrawAndPEqual(Model m1) {
        if(m1.p.size() != m1.pDraw.size()) return false;
        for(int i = 0; i < m1.p.size(); i++){
            if (!(m1.p.get(i).x == m1.pDraw.get(i).x || m1.p.get(i).y == m1.pDraw.get(i).y)) {
                return false;
            }
        }
        return true;
    }
}