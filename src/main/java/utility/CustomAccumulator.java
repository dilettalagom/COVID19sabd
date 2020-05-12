package utility;

import model.NationalStatisticsPojo;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.util.StatCounter;
import scala.Tuple2;
import java.io.Serializable;
import java.util.List;

public class CustomAccumulator implements Serializable {

//    private StatCounter healed;
//    private StatCounter tamponi;
//
//    public CustomAccumulator() {
//        healed = new StatCounter();
//        tamponi = new StatCounter();
//    }
//
//    public CustomAccumulator createAccumulator(){
//        return new CustomAccumulator();
//    };
//
//
//    public Function2<List<Tuple2<String, Double>>, NationalStatisticsPojo, List<Tuple2<String, Double>>> createMergeValue(){
//        healed.merge()
//    };
//
//    public  Function2<List<Tuple2<String, Double>>, List<Tuple2<String, Double>>, List<Tuple2<String, Double>>> createMergeCombiner(){
//
//    };


}
