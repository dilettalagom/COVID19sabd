package utility.comparators;

import model.ClassificationMonthPojo;
import java.io.Serializable;
import java.util.Comparator;

public class TrendMonthComparator implements Serializable, Comparator<ClassificationMonthPojo> {


    @Override
    public int compare(ClassificationMonthPojo o1, ClassificationMonthPojo o2) {
        int c = (new Double(o1.getTrendMonth())).compareTo(new Double(o2.getTrendMonth()));
        if(c == 0){
            c = o1.getMonthYear().compareTo(o2.getMonthYear());
        }
        return c;
    }


}
