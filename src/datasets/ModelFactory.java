package datasets;

import model.Model;
import model.ParModel;
import model.SeqModel;

/**
 * Created by Chris on 17/04/2018.
 */
public class ModelFactory {

    public Model createModel(String type) {
        if (type.equalsIgnoreCase("Sequential")) {
            return new SeqModel();
        }
        else if (type.equalsIgnoreCase("Parallel")) {
            return new ParModel();
        }
        else {
            throw new IllegalArgumentException("Model parameter type invalid");
        }
    }
}
