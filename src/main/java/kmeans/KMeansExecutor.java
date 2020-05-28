package kmeans;

import model.ClassificationMonthPojo;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import java.io.Serializable;
import java.util.Map;


public abstract class KMeansExecutor  implements Serializable {

    int K;
    int MAX_ITER;
    double EPSILON;
    JavaSparkContext jsc;
    String pathOutputFile;


    public KMeansExecutor (int K, int MAX_ITER, double EPSILON, JavaSparkContext context, String pathOutputFile){
        this.K = K;
        this.MAX_ITER = MAX_ITER;
        this.EPSILON = EPSILON;
        this.jsc = context;
        this.pathOutputFile = pathOutputFile;
    }

    public abstract void starter(Map<String, JavaPairRDD<Tuple2<String,Double>, ClassificationMonthPojo>> monthMap);


}
