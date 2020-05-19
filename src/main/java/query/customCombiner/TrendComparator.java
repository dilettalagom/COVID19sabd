package query.customCombiner;

import model.ClassificationKeyPojo;
import scala.Tuple2;

import java.io.Serializable;
import java.util.Comparator;

public class TrendComparator implements Serializable, Comparator< ClassificationKeyPojo > {


    @Override
    public int compare(ClassificationKeyPojo o1, ClassificationKeyPojo o2) {
        Double d1 = o1.getTrendCoefficient();
        Double d2 = o2.getTrendCoefficient();
        return d1.compareTo(d2);
    }


   /* @Override
    public int compare(Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> o1,
                       Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> o2) {

        Double d1 = o1._1().getTrendCoefficient();
        Double d2 = o2._1().getTrendCoefficient();

        return d1.compareTo(d2);
    }*/
}


