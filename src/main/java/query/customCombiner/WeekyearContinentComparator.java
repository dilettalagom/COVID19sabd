package query.customCombiner;

import model.ClassificationKeyPojo;
import scala.Serializable;
import scala.Tuple2;
import scala.Tuple4;

import java.util.Comparator;

public class WeekyearContinentComparator implements Serializable, Comparator<Tuple2<String, Double>>  {



//    @Override
//    public int compare(ClassificationKeyPojo o1, ClassificationKeyPojo o2) {
//        int c = (new Double(o1.getWeekYear()).compareTo(new Double(o2.getWeekYear())));
//        if(c == 0){
//            o1.getContinent().compareTo(o2.getContinent());
//        }
//        return c;
//    }

    @Override
    public int compare(Tuple2<String, Double> o1,
                       Tuple2<String, Double> o2) {

        int c = o1._1().compareTo(o2._1());

        if (c == 0)
            c = o1._2().compareTo(o2._2());

        return c;
    }



}

