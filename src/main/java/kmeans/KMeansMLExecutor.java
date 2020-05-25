package kmeans;

import model.ClassificationMonthPojo;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.ml.clustering.KMeansModel;
import org.apache.spark.ml.evaluation.ClusteringEvaluator;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import scala.Tuple2;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class KMeansMLExecutor extends KMeansExecutor{

    private SQLContext sqc;

    public KMeansMLExecutor(int K, int MAX_ITER, double EPSILON, JavaSparkContext context, String pathFile) {
        super(K, MAX_ITER, EPSILON, context, pathFile);
        this.sqc = new SQLContext(this.jsc);

    }

    public JavaRDD<Row> executeAlgorithm(JavaRDD<ClassificationMonthPojo> javaRDD){


        Dataset<Row> top50DF = this.sqc.createDataFrame(javaRDD, ClassificationMonthPojo.class);

        String[] cols = new String[]{"trendMonth"};
        VectorAssembler assembler = new VectorAssembler().setInputCols(cols).setOutputCol("features");
        Dataset<Row> transformedRDD = assembler.transform(top50DF);

        KMeans kMeans = new KMeans()
                .setK(this.K)
                .setMaxIter(this.MAX_ITER)
                .setSeed(1L);
        KMeansModel model = kMeans.fit(transformedRDD);

        // Make predictions
        Dataset<Row> predictions = model.transform(transformedRDD);
        //transformedRDD.show(false);

        ClusteringEvaluator evaluator = new ClusteringEvaluator();

//        double silhouette = evaluator.evaluate(predictions);
//        System.out.println("Silhouette with squared euclidean distance = " + silhouette);

        // Shows the result.
        Vector[] centers = model.clusterCenters();
        System.out.println("Cluster Centers: ");
        for (Vector center: centers) {
            System.out.println(center);
        }
        return predictions.toJavaRDD();
    }

    @Override
    public void starter(Map<String, JavaPairRDD<Tuple2<String, Double>, ClassificationMonthPojo>> monthMap) {

        monthMap.forEach((s, javaRDD) -> {

            long startTime = System.nanoTime();
            JavaRDD<Row> kmeansRDD = executeAlgorithm(javaRDD.values());

            long endTime = System.nanoTime();

            long convert = TimeUnit.MILLISECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS);
            System.out.println("Time elapsed : " + convert);

            String mlOutputPath = this.pathOutputFile +"/ML/ML_"+s;
           /* try{
                FileSystem hdfs = FileSystem.get(this.jsc.hadoopConfiguration());
                Path path = new Path(mlOutputPath);
                if (hdfs.exists(path)) {
                    hdfs.delete(path, true);
                }
                kmeansRDD.repartition(1).saveAsTextFile(mlOutputPath);
            }catch (IOException e){
                e.printStackTrace();
            }*/
           printResults(kmeansRDD, mlOutputPath);
        });
    }

    public void printResults(JavaRDD resultRDD, String outputPath) {
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
