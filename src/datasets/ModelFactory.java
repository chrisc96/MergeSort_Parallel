package datasets;

import model.Model;
import model.ParModel;
import model.SeqModel;

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
            return new SeqModel();
        }
        else if (type.ordinal() == 1) {
            return new ParModel();
        }
        else {
            throw new IllegalArgumentException("Model parameter type invalid");
        }
    }
}
