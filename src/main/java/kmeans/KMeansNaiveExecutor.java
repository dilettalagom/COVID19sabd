package kmeans;

import kmeans.kmeansnaive.Cluster;
import kmeans.kmeansnaive.Iteration;
import lombok.Getter;
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

            this.currentIteration = new Iteration(initialClusters, this.EPSILON);

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
            System.out.println("CURRENT ITERATION " + numIter);
        }
        return currentIteration.getClusters();
    }


    @Override
    public void starter(Map<String, JavaPairRDD<Tuple2<String,Double>, ClassificationMonthPojo>> monthMap){
        monthMap.forEach((s, javaRDD) -> {

            System.out.println("MESE CORRENTE =" + s);

            long startTime = System.nanoTime();

            List<Cluster> clusters = executeAlgorithm(javaRDD.values());
            long endTime = System.nanoTime();
            long convert = TimeUnit.MILLISECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS);
            System.out.println("Time elapsed : " + convert + "Iter " + s);

            printResults(clusters, s);

        });

    }


    public void printResults(List<Cluster> clusters, String s) {
        String outputPath = this.pathOutputFile + "/NAIVE/NAIVE_" +s;
        try {
            FileSystem hdfs = FileSystem.get(this.jsc.hadoopConfiguration());
            Path path = new Path(outputPath);
            if (hdfs.exists(path)) {
                hdfs.delete(path, true);
            }
            clusters.forEach(
                    c -> c.getPointsOfCluster().repartition(1).saveAsTextFile(outputPath + "_" + c.getId())
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private boolean checkBreakCondition(Map<Integer,Double> oldMap, Map<Integer,Double> newMap){
        final boolean[] overDelta = {false};
        oldMap.forEach(
                (id, centroidOld) ->  {
                    double delta = Math.abs(newMap.get(id) - oldMap.get(id));
                    overDelta[0] = (overDelta[0] || delta < this.EPSILON);
                }
        );
        return overDelta[0];
    }

}


//NOTA: senza early-stopping, max_iter = 10, naive == mlib circa. Dipende dal run. A SEED fissato sono differenti