package kmeans.kmeansnaive;

import lombok.extern.slf4j.Slf4j;
import model.ClassificationMonthPojo;
import org.apache.spark.api.java.JavaRDD;

import javax.naming.InitialContext;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
public class KMeansSimulation implements Serializable {

    public static double CENTROID_EPSILON = 0.01;
    JavaRDD<ClassificationMonthPojo> dataset;
    Iteration currentIteration;
    public int K = 4;
    public int MAX_ITERATIONS = 10;



    public KMeansSimulation(JavaRDD<ClassificationMonthPojo> dataset) {
        this.dataset = dataset.cache();
    }



    public List<Cluster> startKMeansSimulation() {
        //init centroids
        Map<Integer, Double> initialClusters = generateClusterMapRandom();

        int numIter = 0;
        while (numIter < MAX_ITERATIONS) {

            currentIteration = new Iteration(initialClusters);

            //fase 1: assegnazione dei punti ai cluster secondo distanza euclidea
            currentIteration.assignPointsToClusters(this.dataset);

            //fase 2: ricalcolo dei baricentri + check condizione di stop
            currentIteration.recomputeCentroids();
            if (currentIteration.isAtLeastOneCentroidHasMoved() == false || checkBreakCondition(initialClusters, currentIteration.getFinalClustersMap())) {
                break;
            }
            initialClusters = currentIteration.getFinalClustersMap();
            numIter++;
            System.out.println("CURRENT ITERATION " + numIter);
        }
        return currentIteration.getClusters();
    }




    private Map<Integer,Double> generateClusterMapRandom() {
        Map<Integer,Double> initValuesClusters = new HashMap<>();
        Random rand = new Random();

        for(int i = 0; i < K; i++)
        {
            ClassificationMonthPojo v = dataset.takeSample(false, 1, rand.nextLong()).get(0);
            initValuesClusters.put(i, v.getTrendMonth());
        }
        return initValuesClusters;
    }


    private boolean checkBreakCondition(Map<Integer,Double> oldMap, Map<Integer,Double> newMap)
    {
        final boolean[] overDelta = {false};
        oldMap.forEach(
                (id, centroidOld) ->  {
                    double delta = Math.abs(newMap.get(id) - oldMap.get(id));
                    overDelta[0] = (overDelta[0] || delta < CENTROID_EPSILON);
                }
        );
        return overDelta[0];
    }

}
