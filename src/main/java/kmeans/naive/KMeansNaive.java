package kmeans.naive;

import model.ClassificationMonthPojo;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class KMeansNaive implements Serializable {

    private static final Random random = new Random();
    //number of clusters
    private int k;
    //distance to stop kmeans
    private int delta;



    //init centroids randomically
    private static JavaRDD<Cluster> initCentroids(JavaSparkContext sc, JavaRDD<ClassificationMonthPojo> dataset, int k) {

        List<Cluster> clusterList = new ArrayList<>();
        Random rand = new Random();
        List<Double> valuesToClassify = new ArrayList<>();

        for (ClassificationMonthPojo p: dataset.collect()) {
            valuesToClassify.add(p.getTrendMonth());
        }

        for (int i = 0; i < k; i++) {
            Double v = sc.parallelize(valuesToClassify).takeSample(false, 1, rand.nextLong()).get(0);
            clusterList.add(new Cluster(i, v);
        }

        return sc.parallelize(clusterList);
    }


    //find nearest centroid
    private static Cluster nearestCentroid(ClassificationMonthPojo record, JavaRDD<Cluster> clusters) {
        double minimumDistance = Double.MAX_VALUE;
        Cluster nearest = null;
        EuclideanDistance euclideanDistance = new EuclideanDistance();

        for (Cluster c : clusters.collect()) {
            double currentDistance = euclideanDistance.distance(record.getTrendMonth(),c.getCentroid());

            if (currentDistance < minimumDistance) {
                minimumDistance = currentDistance;
                nearest = c;
            }
        }
        return nearest;
    }


    //First off, given a Record, we should find the centroid nearest to it:
    private static void assignToCluster(Map<Centroid, List<Record>> clusters,
                                        Record record,
                                        Centroid centroid) {
        clusters.compute(centroid, (key, list) -> {
            if (list == null) {
                list = new ArrayList<>();
            }

            list.add(record);
            return list;
        });
    }


    //If, after one iteration, a centroid does not contain any assignments, then we won't relocate it.
    //Otherwise, we should relocate the centroid coordinate for each attribute to the average location of all assigned records:
    private static Centroid average(Centroid centroid, List<Record> records) {
        if (records == null || records.isEmpty()) {
            return centroid;
        }

        Map<String, Double> average = centroid.getCoordinates();
        records.stream().flatMap(e -> e.getFeatures().keySet().stream())
                .forEach(k -> average.put(k, 0.0));

        for (Record record : records) {
            record.getFeatures().forEach(
                    (k, v) -> average.compute(k, (k1, currentValue) -> v + currentValue)
            );
        }

        average.forEach((k, v) -> average.put(k, v / records.size()));

        return new Centroid(average);
    }


    //In each iteration, after assigning all records to their nearest centroid, first, we should compare the current assignments with the last iteration
    //If the assignments were identical, then the algorithm terminates. Otherwise, before jumping to the next iteration, we should relocate the centroids:
    public static JavaPairRDD<Cluster, ClassificationMonthPojo> startKMeansNaive(JavaSparkContext sc, JavaRDD<ClassificationMonthPojo> dataset, int k, int maxIterations) {

        JavaRDD<Cluster> clusters = initCentroids(sc,dataset, k);
        JavaPairRDD<Cluster, JavaRDD<ClassificationMonthPojo>> clustersRDD = clusters.mapToPair(
                x -> new Tuple2<>(x, dataset)
        );

        // iterate for a pre-defined number of times
        for (int i = 0; i < maxIterations; i++) {
            boolean isLastIteration = i == maxIterations - 1;

            // in each iteration we should find the nearest centroid for each record
            clustersRDD.foreach(
                    c -> {
                        c._2().foreach(
                                pojo -> {
                                    double centroid = nearestCentroid(, clusters);
                                }
                        );
                    }
            );

            dataset.foreach(
                    x -> {
                        double centroid = nearestCentroid(x, clusters);
                        }
                        assignToCluster(clusters, x, centroid);
                    }
            );


            // if the assignments do not change, then the algorithm terminates
            boolean shouldTerminate = isLastIteration || clusters.equals(lastState);
            lastState = clusters;
            if (shouldTerminate) {
                break;
            }

            // at the end of each iteration we should relocate the centroids
            centroids = relocateCentroids(clusters);
            clusters = new HashMap<>();
        }

        return JavaPairRDD<Cluster, ClassificationMonthPojo>;
    }


}
