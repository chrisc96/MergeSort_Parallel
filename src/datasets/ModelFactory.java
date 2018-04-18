package datasets;

import model.Model;
import model.ModelParallel_1;
import model.ModelSequential;

/**
 * Created by Chris on 17/04/2018.
 */
public class ModelFactory {

    public enum ModelType {
        SEQUENTIAL,
        PARALLEL
    }

    public Model createModel(ModelType type) {
        if (type.ordinal() == 0) {
            return new ModelSequential();
        }
        else if (type.ordinal() == 1) {
            return new ModelParallel_1();
        }
        else {
            throw new IllegalArgumentException("Model parameter type invalid");
        }
    }
}
