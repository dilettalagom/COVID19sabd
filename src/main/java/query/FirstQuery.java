package query;

import model.NationalStatisticsPojo;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.util.StatCounter;
import scala.Tuple2;
import utility.parser.General;

import java.io.File;
import java.io.IOException;


public class FirstQuery {


    public static void main(String[] args) {


        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("FirstQuery");
        JavaSparkContext context = new JavaSparkContext(conf);

        JavaRDD<String> csvData = context.textFile("hdfs://master:54310/dataset/covid19_national.csv");
        String csvHeader = csvData.first();
        JavaRDD<String> nonHeaderCSV = csvData.filter(row -> !row.equals(csvHeader));

        JavaPairRDD<String, NationalStatisticsPojo> nationalInfo = nonHeaderCSV.mapToPair(
                (String line) ->  {
                    String[] lineSplitted = line.split(",");
                    NationalStatisticsPojo pojo = new NationalStatisticsPojo(lineSplitted[0],lineSplitted[1],lineSplitted[2]);
                    String key = General.createKey(lineSplitted[0]);
                    return new Tuple2(key,pojo);
                }).cache();

        //media guariti
        JavaPairRDD<String, Double> rddMeanHealed = nationalInfo.aggregateByKey(
                new StatCounter(),
                (acc, x) -> acc.merge(x.getNumHealed()),
                (acc1, acc2) -> acc1.merge(acc2)
        )
                //Key = Tuple3<Country, year, month>, Value = Tuple4<mean, std, min, max>
                .mapToPair(x -> {
                    String key = x._1();
                    Double mean = x._2().mean();
                    return new Tuple2<>(key, mean);
                });

        //media tamponi
        JavaPairRDD<String, Double> rddMeanTamponi = nationalInfo.aggregateByKey(
                new StatCounter(),
                (acc, x) -> acc.merge(x.getNumTampons()),
                StatCounter::merge
        )
                //Key = Tuple3<Country, year, month>, Value = Tuple4<mean, std, min, max>
                .mapToPair(x -> {
                    String key = x._1();
                    Double mean = x._2().mean();
                    return new Tuple2<>(key, mean);
                });

        //join tra i due RDD
        JavaPairRDD<String, Tuple2<Double, Double>> resultRDD = rddMeanHealed.join(rddMeanTamponi).sortByKey();

        try {
            FileSystem hdfs = FileSystem.get(context.hadoopConfiguration());
            Path path = new Path("hdfs://master:54310/results/firstQuery");
            if(hdfs.exists(path)){
                hdfs.delete(path, true);
            }
            resultRDD.repartition(1).saveAsTextFile (path.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }










}







