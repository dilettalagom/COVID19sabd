package query.customCombiner;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import scala.Serializable;
import scala.Tuple2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class KeyAccumulator implements Serializable {


    static WeekyearContinentComparator customComparator = new WeekyearContinentComparator();


    public  Function<Tuple2<String, Double>, List<Tuple2<String, Double>>> createAccumulator() {
        Function<Tuple2<String, Double>, List<Tuple2<String, Double>>> createAcc = new Function<Tuple2<String, Double>, List<Tuple2<String, Double>>>() {
            @Override
            public List<Tuple2<String, Double>> call(Tuple2<String, Double> x) {
                List<Tuple2<String,Double>> list = new ArrayList<>();
                list.add(x);
                return list;
            }
        };
        return createAcc;
    }


    public Function2<List<Tuple2<String, Double>>, Tuple2<String, Double>, List<Tuple2<String, Double>>> createMergeOneValueAcc() {
        Function2<List<Tuple2<String, Double>>, Tuple2<String, Double>, List<Tuple2<String, Double>>>
                mergeOneValueAcc = new Function2<List<Tuple2<String, Double>>, Tuple2<String, Double>, List<Tuple2<String, Double>>>() {
            @Override
            public List<Tuple2<String, Double>> call(List<Tuple2<String, Double>> v1, Tuple2<String, Double> v2) throws Exception {
                v1.add(v2);
                Collections.sort(v1, customComparator.reversed());
                return v1;
            }
        };
        return mergeOneValueAcc;

    }


    public Function2<List<Tuple2<String, Double>>, List<Tuple2<String, Double>>, List<Tuple2<String, Double>>> createMergeObjectsAcc() {

        Function2<List<Tuple2<String, Double>>,List<Tuple2<String, Double>>,List<Tuple2<String, Double>>> mergeObjectsAcc
                = new Function2<List<Tuple2<String, Double>>, List<Tuple2<String, Double>>, List<Tuple2<String, Double>>>() {
            @Override
            public List<Tuple2<String, Double>> call(List<Tuple2<String, Double>> v1, List<Tuple2<String, Double>> v2) throws Exception {
                v1.addAll(v2);
                Collections.sort(v1,customComparator.reversed());
                return v1;
            }
        };
        return mergeObjectsAcc;
    }




}