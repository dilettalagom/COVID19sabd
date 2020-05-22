package utility.customCombiner;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import utility.comparators.WeekyearContinentComparator;
import scala.Serializable;
import scala.Tuple2;


public class KeyAccumulator implements Serializable {


    static WeekyearContinentComparator customComparator = new WeekyearContinentComparator();


    public  Function<Tuple2<String, Double>, Double> createAccumulator() {
        Function<Tuple2<String, Double>, Double> createAcc = new Function<Tuple2<String, Double>, Double>() {
            @Override
            public Double call(Tuple2<String, Double> x) {
                return x._2();
            }
        };
        return createAcc;
    }


    public Function2<Double, Tuple2<String, Double>, Double> createMergeOneValueAcc() {
        Function2<Double, Tuple2<String, Double>, Double>
                mergeOneValueAcc = new Function2<Double, Tuple2<String, Double>, Double>() {
            @Override
            public Double call(Double v1, Tuple2<String, Double> v2) throws Exception {
                return v1 + v2._2();
            }
        };
        return mergeOneValueAcc;

    }


    public Function2<Double, Double, Double> createMergeObjectsAcc() {

        Function2<Double, Double, Double> mergeObjectsAcc
                = new Function2<Double, Double, Double>() {
            @Override
            public Double call(Double v1, Double v2) throws Exception {
                return v1 + v2;
            }
        };
        return mergeObjectsAcc;
    }




}