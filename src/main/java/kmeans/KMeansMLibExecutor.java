package kmeans;

import model.ClassificationMonthPojo;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.clustering.KMeansModel;
import org.apache.spark.ml.evaluation.ClusteringEvaluator;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.ml.linalg.Vector;
//import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;

public class KMeansMLibExecutor {

    int K;
    int MAX_ITER;
    SQLContext sqc;

    public KMeansMLibExecutor (int K, int MAX_ITER, JavaSparkContext context)
    {
        this.K = K;
        this.MAX_ITER = MAX_ITER;
        this.sqc = new SQLContext(context);
    }


    public JavaRDD<Row> executeKmeansML(JavaRDD<ClassificationMonthPojo> top50ForMonthAndTrend){


        Dataset<Row> top50DF = sqc.createDataFrame(top50ForMonthAndTrend, ClassificationMonthPojo.class);

        String[] cols = new String[]{"trendMonth"};
        VectorAssembler assembler = new VectorAssembler().setInputCols(cols).setOutputCol("features");
        Dataset<Row> transformedRDD = assembler.transform(top50DF);

        KMeans kMeans = new KMeans().setK(4).setSeed(1L).setMaxIter(20);
        KMeansModel model = kMeans.fit(transformedRDD);

        // Make predictions
        Dataset<Row> predictions = model.transform(transformedRDD);
        //transformedRDD.show(false);

        ClusteringEvaluator evaluator = new ClusteringEvaluator();

//        double silhouette = evaluator.evaluate(predictions);
//        System.out.println("Silhouette with squared euclidean distance = " + silhouette);

        // Shows the result.
        org.apache.spark.ml.linalg.Vector[] centers = model.clusterCenters();
        System.out.println("Cluster Centers: ");
        for (Vector center: centers) {
            System.out.println(center);
        }
        return predictions.toJavaRDD();
    }



    public JavaPairRDD<Integer, Iterable<org.apache.spark.mllib.linalg.Vector>> executeKmeansMLib(JavaRDD<ClassificationMonthPojo> top50ForMonthAndTrend){

        JavaRDD<org.apache.spark.mllib.linalg.Vector> vector = top50ForMonthAndTrend.map(s -> Vectors.dense(s.getTrendMonth())).cache();

        org.apache.spark.mllib.clustering.KMeans kMeans = new org.apache.spark.mllib.clustering.KMeans().setK(4).setSeed(1L);
        // Cluster the data into four classes using KMeans
        org.apache.spark.mllib.clustering.KMeansModel clusters = kMeans.train(vector.rdd(), K, MAX_ITER, org.apache.spark.mllib.clustering.KMeans.K_MEANS_PARALLEL());

        System.out.println("Cluster centers:");
        for (org.apache.spark.mllib.linalg.Vector center : clusters.clusterCenters()) {
            System.out.println(" " + center);
        }
        double cost = clusters.computeCost(vector.rdd());
        System.out.println("Cost: " + cost);

        // Evaluate clustering by computing Within Set Sum of Squared Errors
        double WSSSE = clusters.computeCost(vector.rdd());
        System.out.println("Within Set Sum of Squared Errors = " + WSSSE);

        JavaPairRDD<Integer, Iterable<org.apache.spark.mllib.linalg.Vector>> resultClusters = getClustersAndPoints(vector, clusters);

        // Save and load model
        return resultClusters;

        }


    public static JavaPairRDD<Integer, Iterable<org.apache.spark.mllib.linalg.Vector>> getClustersAndPoints(JavaRDD<org.apache.spark.mllib.linalg.Vector> inputPoints, org.apache.spark.mllib.clustering.KMeansModel clusterModel ) {
        // Group the input points by their kmeans centroid index
        return inputPoints.groupBy(
                point -> clusterModel.predict(point)
                );
    }
    }





