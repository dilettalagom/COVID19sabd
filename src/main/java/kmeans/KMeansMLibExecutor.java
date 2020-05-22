package kmeans;

import model.ClassificationMonthPojo;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.clustering.KMeansModel;
import org.apache.spark.ml.evaluation.ClusteringEvaluator;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;

import java.util.List;

public class KMeansMLibExecutor {

    int numClusters;
    int numIterations;

    public KMeansMLibExecutor (int numClusters, int numIterations)
    {
        this.numClusters = numClusters;
        this.numIterations = numIterations;
    }

    public JavaRDD<Row> executeKmeansMLib(JavaSparkContext context, JavaRDD<ClassificationMonthPojo> top50ForMonthAndTrend){

       // JavaRDD<Vector> parsedData = top50ForMonthAndTrend.map(s -> Vectors.dense(s.getTrendMonth()));
       // parsedData.cache();
        SQLContext sqc = new SQLContext(context);

        //Dataset<Row> top50DF = sqc.createDataFrame(top50ForMonthAndTrend, ClassificationMonthPojo.class).;
        //top50DF.orderBy("monthYear").groupBy("monthYear");

        Dataset<Row> top50DF = sqc.createDataFrame(top50ForMonthAndTrend, ClassificationMonthPojo.class);
        top50DF.orderBy("monthYear").groupBy("monthYear");


        String[] cols = new String[]{"index","trendMonth"};
        VectorAssembler assembler = new VectorAssembler().setInputCols(cols).setOutputCol("features");
        Dataset<Row> transformedRDD = assembler.transform(top50DF);

        KMeans kMeans = new KMeans().setK(4).setSeed(1L);
        KMeansModel model = kMeans.fit(transformedRDD);

        // Make predictions
        Dataset<Row> predictions = model.transform(transformedRDD);
        transformedRDD.show(false);


//            Files.write(
//                    Paths.get("some_file.txt"),
//                    model.(a -> Arrays.toString(a.toArray()))
//                            .collect(Collectors.toList())
//            );
        // Evaluate clustering by computing Silhouette score
        ClusteringEvaluator evaluator = new ClusteringEvaluator();

        double silhouette = evaluator.evaluate(predictions);
        System.out.println("Silhouette with squared euclidean distance = " + silhouette);

        // Shows the result.
        org.apache.spark.ml.linalg.Vector[] centers = model.clusterCenters();
        System.out.println("Cluster Centers: ");
        for (Vector center: centers) {
            System.out.println(center);
        }
        return predictions.toJavaRDD();

        // Cluster the data into four classes using KMeans
        //KMeansModel clusters = KMeans.train(parsedData.rdd(), numClusters, numIterations);

        //System.out.println("Cluster centers:");
        //for (Vector center: clusters.clusterCenters()) {
          //  System.out.println(" " + center);
       // }
       // double cost = clusters.computeCost(parsedData.rdd());
        //System.out.println("Cost: " + cost);

        // Evaluate clustering by computing Within Set Sum of Squared Errors
       // double WSSSE = clusters.computeCost(parsedData.rdd());
        //System.out.println("Within Set Sum of Squared Errors = " + WSSSE);

        // Save and load model
        //return clusters;

//        .save(jsc.sc(), "target/org/apache/spark/JavaKMeansExample/KMeansModel");
//        KMeansModel sameModel = KMeansModel.load(jsc.sc(),
//                "target/org/apache/spark/JavaKMeansExample/KMeansModel");
    }





}
