package kmeans.kmeansnaive;

import lombok.Getter;
import model.ClassificationMonthPojo;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import scala.Serializable;
import scala.Tuple2;
import java.util.*;


public class Iteration implements Serializable {

    @Getter
    public List<Cluster> clusters;
    @Getter
    public boolean atLeastOneCentroidHasMoved;


    public Iteration(Map<Integer,Double> newValuesCluster) {
        this.clusters = initClusters(newValuesCluster);
    }



    private List<Cluster> initClusters(Map<Integer,Double> newValuesCluster)
    {
        List<Cluster> clusters = new ArrayList<>();
        newValuesCluster.forEach((clusterId, clusterCentroid) -> clusters.add(new Cluster(clusterId,clusterCentroid)));
        return clusters;
    }


    //fase 1: assegno i punti ai cluster
    public void assignPointsToClusters(JavaRDD<ClassificationMonthPojo> pointsToAssign)
    {
        JavaPairRDD<Cluster, ClassificationMonthPojo> clusterPoint = pointsToAssign.mapToPair(
                point -> {
                    Cluster c = findClusterToAssing(point.getTrendMonth());
                    return new Tuple2(c, point);
                }
        ).groupByKey().flatMapValues(v -> v);

        clusters.forEach(
                cluster -> {
                    JavaRDD<ClassificationMonthPojo> values = clusterPoint.filter(c -> c._1.equals(cluster)).values();
                    cluster.setPointsOfCluster(values);
                }
        );
    }



    //fase 2
    public void recomputeCentroids(){

        clusters.forEach(
                c -> {
                    c.relocateCentroid();
                    this.atLeastOneCentroidHasMoved = (this.atLeastOneCentroidHasMoved || c.isCentroidMoved());
                }
        );
    }



    public Map<Integer,Double> getFinalClustersMap()
    {
        Map<Integer, Double> finalClusters = new HashMap<>();
        clusters.forEach(
                c -> finalClusters.put(c.id, c.getFinalCentroid())
        );
        return finalClusters;
    }



    private Cluster findClusterToAssing(Double point)
    {
        double minDistance = Double.MAX_VALUE;
        Cluster clusterToAssing = null;
        for (Cluster c: clusters) {

            double actualDistance = c.getDistanceFromCentroid(point);
            if(actualDistance < minDistance){
                minDistance = actualDistance;
                clusterToAssing = c;
            }
        }
        return clusterToAssing;
    }

}
