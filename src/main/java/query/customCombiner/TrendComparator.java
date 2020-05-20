package query.customCombiner;

import model.ClassificationWeekYearPojo;
import java.io.Serializable;
import java.util.Comparator;

public class TrendComparator implements Serializable, Comparator<ClassificationWeekYearPojo> {


    @Override
    public int compare(ClassificationWeekYearPojo o1, ClassificationWeekYearPojo o2) {
        return (new Double(o1.getTrendCoefficient()).compareTo(new Double(o2.getTrendCoefficient())));
    }

}


