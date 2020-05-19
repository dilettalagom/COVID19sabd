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


  /*  public Function<Tuple2<ClassificationKeyPojo, Tuple2<String, Double>>,
            List<Tuple2<ClassificationKeyPojo, Tuple2<String, Double>>>> createAccumulator() {
        Function< Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> ,
                List<Tuple2<ClassificationKeyPojo, Tuple2<String, Double>>>
                > createAcc =
                new Function<
                        Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> ,
                        List<Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> >
                        >() {
                    @Override
                    public List<Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> >
                    call(Tuple2<ClassificationKeyPojo,Tuple2<String, Double>> tuple) {
                        return new ArrayList<>();
                    }
                };

        return createAcc;
    }


    public Function2< List< Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> > ,
            Tuple2<ClassificationKeyPojo, Tuple2<String, Double>>,
            List<Tuple2<ClassificationKeyPojo, Tuple2<String, Double>>>
            >createMergeOneValueAcc() {

        Function2< List< Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> > ,
                Tuple2<ClassificationKeyPojo, Tuple2<String, Double>>,
                List<Tuple2<ClassificationKeyPojo, Tuple2<String, Double>>>
                >mergeOneValueAcc =
                new Function2< List< Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> > ,
                        Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> ,
                        List<Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> >
                        >(){
                    @Override
                    public List< Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> >
                    call(List< Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> > tuple1,
                         Tuple2<ClassificationKeyPojo, Tuple2<String, Double> > element_tuple2) throws Exception {

                        tuple1.add(element_tuple2);

                        //Using custom-combiner
                        Collections.sort(tuple1, customComparator.reversed());

                        return tuple1;
                    }
                };
        return mergeOneValueAcc;


    }


    public Function2< List<Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> >,
            List<Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> >,
            List<Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> >
            > createMergeObjectsAcc() {

        Function2< List<Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> >,
                List<Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> >,
                List<Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> >
                > mergeObjectsAcc =
                new Function2<List<Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> >,
                        List<Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> >,
                        List<Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> >
                        >() {
                    @Override
                    public List<Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> >
                    call(List<Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> > originTuple,
                         List<Tuple2<ClassificationKeyPojo, Tuple2<String, Double>>> newTuple) throws Exception {

                        originTuple.addAll(newTuple);

                        //Using custom-combiner
                        Collections.sort(originTuple, customComparator.reversed());

                        return originTuple;
                    }
                };
        return mergeObjectsAcc;

    }*/

    public  Function<Tuple2<String, Double>, List<Tuple2<String, Double>>> createAccumulator() {
        Function<Tuple2<String, Double>, List<Tuple2<String, Double>>> createAcc = new Function<Tuple2<String, Double>, List<Tuple2<String, Double>>>() {
            @Override
            public List<Tuple2<String, Double>> call(Tuple2<String, Double> x) {
                return new ArrayList<>();
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
                //Collections.sort(v1,customComparator.reversed());
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
                //Collections.sort(v1,customComparator.reversed());
                return v1;
            }
        };

        return mergeObjectsAcc;
    }




}