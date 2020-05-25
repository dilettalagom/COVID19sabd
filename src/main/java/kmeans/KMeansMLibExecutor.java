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

public class KMeansMLibExecutor extends KMeansExecutor{


    public KMeansMLibExecutor(int K, int MAX_ITER, double EPSILON, JavaSparkContext context, String pathFile) {
        super(K, MAX_ITER, EPSILON, context, pathFile);
    }



    public static JavaPairRDD<Integer, Iterable<Vector>> getClustersAndPoints(JavaRDD<Vector> inputPoints, KMeansModel clusterModel ) {
        // Group the input points by their kmeans centroid index
        return inputPoints.groupBy(
                point -> clusterModel.predict(point)
        );
    }


    public JavaPairRDD<Integer, Iterable<Vector>> executeAlgorithm(JavaRDD<ClassificationMonthPojo> top50ForMonthAndTrend){

        JavaRDD<org.apache.spark.mllib.linalg.Vector> vector = top50ForMonthAndTrend
                .map(s -> Vectors.dense(s.getTrendMonth()))
                .cache();

        KMeans kMeans = new KMeans()
                .setEpsilon(this.EPSILON);
                //.setSeed(1L);

        System.out.println("FUFFA");
        // Cluster the data into four classes using KMeans
        KMeansModel clusters = kMeans.train(vector.rdd(), this.K, this.MAX_ITER, KMeans.K_MEANS_PARALLEL());

        System.out.println("SEED "+ kMeans.getSeed());

        System.out.println("Cluster centers:");
        for (Vector center : clusters.clusterCenters()) {
            System.out.println(" " + center);
        }
        double cost = clusters.computeCost(vector.rdd());
        System.out.println("Cost: " + cost);

        // Evaluate clustering by computing Within Set Sum of Squared Errors
        double WSSSE = clusters.computeCost(vector.rdd());
        System.out.println("Within Set Sum of Squared Errors = " + WSSSE);

        JavaPairRDD<Integer, Iterable<Vector>> resultClusters = getClustersAndPoints(vector, clusters);

        // Save and load model
        return resultClusters;

    }


    @Override
    public void starter(Map<String, JavaPairRDD<Tuple2<String,Double>, ClassificationMonthPojo>> monthMap){
        monthMap.forEach((s, javaRDD) -> {

            //KMEANS MLIB
            long startTime = System.nanoTime();
            JavaPairRDD<Integer, Iterable<Vector>> kmeansRDD = executeAlgorithm(javaRDD.values());

            long endTime = System.nanoTime();

            long convert = TimeUnit.MILLISECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS);
            System.out.println("Time elapsed : " + convert);

            String mllibOutputPath = this.pathOutputFile +"/MLLIB/MLLIB_"+s;

            printResults(kmeansRDD, mllibOutputPath);
        });
    }

    public void printResults(JavaPairRDD<Integer, Iterable<Vector>> resultRDD, String outputPath) {
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





