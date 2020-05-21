package query.customCombiner;

import scala.Tuple2;
import java.io.Serializable;
import java.util.Comparator;

public class MonthYearTrendComparator implements Serializable, Comparator<Tuple2<String, Double>> {

    @Override
    public int compare(Tuple2<String, Double> o1, Tuple2<String, Double> o2) {
        int c = o1._1.compareTo(o2._1);
        if( c == 0){
            c = o1._2.compareTo(o2._2);
        }
        return c;
    }
}
