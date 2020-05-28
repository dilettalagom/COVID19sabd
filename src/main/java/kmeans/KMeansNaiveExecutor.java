package kmeans;

import kmeans.kmeansnaive.Cluster;
import kmeans.kmeansnaive.Iteration;
import model.ClassificationMonthPojo;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class KMeansNaiveExecutor extends KMeansExecutor {

    private Iteration currentIteration;

    public KMeansNaiveExecutor(int K, int MAX_ITER, double EPSILON, JavaSparkContext context, String pathFile) {
        super(K, MAX_ITER, EPSILON, context, pathFile);
    }



    private Map<Integer,Double> generateClusterMapRandom(JavaRDD<ClassificationMonthPojo> dataset) {
        Map<Integer,Double> initValuesClusters = new HashMap<>();
        Random rand = new Random();

        for(int i = 0; i < this.K; i++) {
            ClassificationMonthPojo v = dataset.takeSample(false, 1, rand.nextLong()).get(0);
            initValuesClusters.put(i, v.getTrendMonth());
        }
        return initValuesClusters;
    }


    public List<Cluster> executeAlgorithm(JavaRDD<ClassificationMonthPojo> dataset) {

        //init centroids
        Map<Integer, Double> initialClusters = generateClusterMapRandom(dataset);

        int numIter = 0;
        while (numIter < this.MAX_ITER) {

            this.currentIteration = new Iteration(initialClusters);

            //fase 1: assegnazione dei punti ai cluster secondo distanza euclidea
            this.currentIteration.assignPointsToClusters(dataset);

            //fase 2: ricalcolo dei baricentri + check condizione di stop
            this.currentIteration.recomputeCentroids();
            //if (currentIteration.isAtLeastOneCentroidHasMoved() == false || checkBreakCondition(initialClusters, currentIteration.getFinalClustersMap())) {
            if (this.currentIteration.isAtLeastOneCentroidHasMoved() == false ) {
                break;
            }
            initialClusters = currentIteration.getFinalClustersMap();
            numIter++;
        }
        return currentIteration.getClusters();
    }


    @Override
    public void starter(Map<String, JavaPairRDD<Tuple2<String,Double>, ClassificationMonthPojo>> monthMap){
        monthMap.forEach((s, javaRDD) -> {

            long startTime = System.nanoTime();
            List<Cluster> clusters = executeAlgorithm(javaRDD.values());
            long endTime = System.nanoTime();
            long convert = TimeUnit.MILLISECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS);
            System.out.println("Time elapsed : " + convert + "Iter " + s);
            printResults(clusters, s);

        });

    }


    public void printResults(List<Cluster> clusters, String s) {
        String outputPath = this.pathOutputFile + "/thirdQuery/NAIVE";
        clusters.forEach(
                c -> {
                    try {
                        FileSystem hdfs = FileSystem.get(this.jsc.hadoopConfiguration());
                        Path path = new Path(outputPath + "_" + s + "_" + c.getId());
                        if (hdfs.exists(path)) {
                                hdfs.delete(path, true);
                        }
                        c.getPointsOfCluster().mapToPair(
                                x -> new Tuple2(c.getId(), x)
                        ).repartition(1).saveAsTextFile(outputPath + "_" + s + "_" + c.getId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }



}


