package query.customCombiner;

import model.ClassificationKeyPojo;

import java.io.Serializable;
import java.util.Comparator;



public class FinalResultComparator implements Serializable, Comparator<ClassificationKeyPojo> {

    @Override
    public int compare(ClassificationKeyPojo o1, ClassificationKeyPojo o2) {
        int c = (new Double(o1.getTrendCoefficient()).compareTo(new Double(o2.getTrendCoefficient())));
        if (c == 0){
            c = o1.getWeekYear().compareTo(o2.getWeekYear());
        }
        if(c == 0){
            c = o1.getContinent().compareTo(o2.getContinent());
        }
        return c;

    }
/*

    @Override
    public int compare(ClassificationKeyPojo o1, ClassificationKeyPojo o2) {
        int c = o1.getWeekYear().compareTo(o2.getWeekYear());
        if (c == 0){
            c = new Double(o1.getTrendCoefficient()).compareTo(new Double(o2.getTrendCoefficient()));
        }
        if(c == 0){
            c = o1.getContinent().compareTo(o2.getContinent());
        }
        return c;

    }
*/
}
