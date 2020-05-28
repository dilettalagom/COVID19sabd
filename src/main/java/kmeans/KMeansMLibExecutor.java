package kmeans;

import model.ClassificationMonthPojo;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.linalg.Vector;
import scala.Tuple2;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;


public class KMeansMLibExecutor extends KMeansExecutor{


    public KMeansMLibExecutor(int K, int MAX_ITER, double EPSILON, JavaSparkContext context, String pathFile) {
        super(K, MAX_ITER, EPSILON, context, pathFile);
    }



    public static JavaPairRDD<Integer, ClassificationMonthPojo> getClustersAndPoints(JavaRDD<Vector> inputPoints, KMeansModel clusterModel, JavaRDD<ClassificationMonthPojo> originalData ) {
        // Group the input points by their kmeans centroid index
        JavaPairRDD<Integer,Double> javaPairRDD = inputPoints.groupBy(
                point -> clusterModel.predict(point)
        ).flatMapValues(
                x -> x
        ).mapToPair(
                x -> new Tuple2(x._1(), DoubleStream.of(x._2().toArray()).boxed().collect(Collectors.toList()))
        ).flatMapValues(
                x -> x
        );
        JavaPairRDD<Double,Integer> trendCluster = javaPairRDD.mapToPair(x -> new Tuple2(x._2(), x._1()));
        JavaPairRDD<Double, ClassificationMonthPojo> remappedPojo = originalData.mapToPair(
                pojo -> new Tuple2<>(pojo.getTrendMonth(), pojo)
        );
        JavaPairRDD<Double, Tuple2<Integer, ClassificationMonthPojo>> join = trendCluster.join(remappedPojo);
        return join.mapToPair(x -> new Tuple2(x._2._1,x._2._2));
    }


    public JavaPairRDD<Integer, ClassificationMonthPojo> executeAlgorithm(JavaRDD<ClassificationMonthPojo> top50ForMonthAndTrend){

        JavaRDD<org.apache.spark.mllib.linalg.Vector> vector = top50ForMonthAndTrend
                .map(s -> Vectors.dense(s.getTrendMonth()))
                .cache();

        KMeans kMeans = new KMeans()
                .setEpsilon(this.EPSILON)
                .setSeed(-7520460576894506511L);

        // Cluster the data into four classes using KMeans
        KMeansModel clusters = kMeans.train(vector.rdd(), this.K, this.MAX_ITER, KMeans.K_MEANS_PARALLEL());

        System.out.println("Cluster centers:");
        for (Vector center : clusters.clusterCenters()) {
            System.out.println(" " + center);
        }

        JavaPairRDD<Integer, ClassificationMonthPojo> resultClusters = getClustersAndPoints(vector, clusters, top50ForMonthAndTrend).distinct();

        return resultClusters;

    }


    @Override
    public void starter(Map<String, JavaPairRDD<Tuple2<String,Double>, ClassificationMonthPojo>> monthMap){
        monthMap.forEach((s, javaRDD) -> {

            long startTime = System.nanoTime();
            JavaPairRDD<Integer, ClassificationMonthPojo> kmeansRDD = executeAlgorithm(javaRDD.values());
            long endTime = System.nanoTime();
            long convert = TimeUnit.MILLISECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS);
            System.out.println("Time elapsed : " + convert);
            String mllibOutputPath = this.pathOutputFile +"/MLLIB/MLLIB_"+s;
            printResults(kmeansRDD, mllibOutputPath);
        });
    }


    public void printResults(JavaPairRDD<Integer, ClassificationMonthPojo> resultRDD, String outputPath) {
        try{
            FileSystem hdfs = FileSystem.get(this.jsc.hadoopConfiguration());
            Path path = new Path(outputPath);
            if (hdfs.exists(path)) {
                hdfs.delete(path, true);
            }
            resultRDD.repartition(1).saveAsTextFile(outputPath);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}





