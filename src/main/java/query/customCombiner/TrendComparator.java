package query.customCombiner;

import model.ClassificationKeyPojo;
import java.io.Serializable;
import java.util.Comparator;

public class TrendComparator implements Serializable, Comparator< ClassificationKeyPojo > {


    @Override
    public int compare(ClassificationKeyPojo o1, ClassificationKeyPojo o2) {
        return (new Double(o1.getTrendCoefficient()).compareTo(new Double(o2.getTrendCoefficient())));
    }

}


