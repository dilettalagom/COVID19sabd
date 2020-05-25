package query;

import model.NationalStatisticsPojo;
import model.keys.NationalWeekKey;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.util.StatCounter;
import scala.Tuple2;
import java.io.IOException;


public class FirstQuery {


    private static String datasetPath = "hdfs://master:54310/dataset/covid19_national.csv";
    private static String resultFirstQueryPath =  "hdfs://master:54310/results/firstQuery";


    public static void main(String[] args) {


        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("FirstQuery");
        JavaSparkContext context = new JavaSparkContext(conf);

        JavaRDD<String> csvData = context.textFile(datasetPath);
        String csvHeader = csvData.first();
        JavaRDD<String> nonHeaderCSV = csvData.filter(row -> !row.equals(csvHeader));

        JavaPairRDD<NationalWeekKey, NationalStatisticsPojo> nationalInfo = nonHeaderCSV.mapToPair(
                (String line) ->  {
                    String[] lineSplitted = line.split(",");
                    NationalStatisticsPojo pojo = new NationalStatisticsPojo(lineSplitted[0],lineSplitted[1],lineSplitted[2]);
                    NationalWeekKey key = new NationalWeekKey(lineSplitted[0]);
                    return new Tuple2(key,pojo);
                }).cache();

        //media guariti
        JavaPairRDD<NationalWeekKey, Double> rddMeanHealed = nationalInfo.aggregateByKey(
                new StatCounter(),
                (acc, x) -> acc.merge(x.getNumHealed()),
                StatCounter::merge
        )
                .mapToPair(x -> {
                    NationalWeekKey nationalWeekKey = x._1();
                    Double mean = x._2().mean();
                    return new Tuple2<>(nationalWeekKey, mean);
                });

        //media tamponi
        JavaPairRDD<NationalWeekKey, Double> rddMeanTamponi = nationalInfo.aggregateByKey(
                new StatCounter(),
                (acc, x) -> acc.merge(x.getNumTampons()),
                StatCounter::merge
        )
                .mapToPair(x -> {
                    NationalWeekKey nationalWeekKey = x._1();
                    Double mean = x._2().mean();
                    return new Tuple2<>(nationalWeekKey, mean);
                });

        //join tra i due RDD
        JavaPairRDD<NationalWeekKey, Tuple2<Double, Double>> resultRDD = rddMeanHealed.join(rddMeanTamponi).sortByKey();

        try {
            FileSystem hdfs = FileSystem.get(context.hadoopConfiguration());
            Path path = new Path(resultFirstQueryPath);
            if(hdfs.exists(path)){
                hdfs.delete(path, true);
            }
            resultRDD.repartition(1).saveAsTextFile (resultFirstQueryPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}







