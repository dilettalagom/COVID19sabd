package query;

import model.GlobalStatisticsPojo;
import model.NationalStatisticsPojo;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.util.StatCounter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import scala.Int;
import scala.Tuple2;
import utility.parser.CalendarUtility;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.ml.regression.LinearRegressionModel;
import org.apache.spark.ml.regression.LinearRegressionTrainingSummary;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import static javafx.animation.PathTransition.interpolate;


public class SecondQuery {

    private static String datasetPath = "hdfs://master:54310/dataset/covid19_global.csv";
    private static String resultSecondQueryPath =  "hdfs://master:54310/results/secondQuery";
    private static DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyyMM");

    public static void main(String[] args) {


        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("FirstQuery");
        JavaSparkContext context = new JavaSparkContext(conf);

        JavaRDD<String> csvData = context.textFile(datasetPath);
        String csvHeader = csvData.first();
        JavaRDD<String> nonHeaderCSV = csvData.filter(row -> !row.equals(csvHeader));

        //find outliers

        //compute regression

        //make classific of top 100

        //group by month, group by continent and compute min, max, std, mean

//        JavaRDD<GlobalStatisticsPojo> globallInfo = nonHeaderCSV.mapToPair(
//                (String line) ->  {
//                    String[] lineSplitted = line.split(",");
//                    String[] valuesToCheck = (Arrays.copyOfRange(lineSplitted, 1, lineSplitted.length));
//                    TreeMap<DateTime,Integer> statistics = new TreeMap<DateTime, Integer>();
//                    for (int i = 0;  i < valuesToCheck.length - 2 ; i++){
//                        int actualValue = Integer.parseInt(valuesToCheck[i]);
//                        if(actualValue > Integer.parseInt(valuesToCheck[i+1]){
//                            //interpolate
//                        }
//                        statistics.put(actualValue)
//
//                    }
//                    GlobalStatisticsPojo pojo = new GlobalStatisticsPojo(lineSplitted[0],lineSplitted[1],lineSplitted[lineSplitted.length-1],statistics);
//                    return pojo;
//                }).cache();

//        //media guariti
//        JavaPairRDD<String, Double> rddMeanHealed = nationalInfo.aggregateByKey(
//                new StatCounter(),
//                (acc, x) -> acc.merge(x.getNumHealed()),
//                (acc1, acc2) -> acc1.merge(acc2)
//        )
//
//                //Key = Tuple3<Country, year, month>, Value = Tuple4<mean, std, min, max>
//                .mapToPair(x -> {
//                    String key = x._1();
//                    Double mean = x._2().mean();
//                    return new Tuple2<>(key, mean);
//                });
//
//        //media tamponi
//        JavaPairRDD<String, Double> rddMeanTamponi = nationalInfo.aggregateByKey(
//                new StatCounter(),
//                (acc, x) -> acc.merge(x.getNumTampons()),
//                StatCounter::merge
//        )
//                //Key = Tuple3<Country, year, month>, Value = Tuple4<mean, std, min, max>
//                .mapToPair(x -> {
//                    String key = x._1();
//                    Double mean = x._2().mean();
//                    return new Tuple2<>(key, mean);
//                });
//
//        //join tra i due RDD
//        JavaPairRDD<String, Tuple2<Double, Double>> resultRDD = rddMeanHealed.join(rddMeanTamponi).sortByKey();
//
//        try {
//            FileSystem hdfs = FileSystem.get(context.hadoopConfiguration());
//            Path path = new Path(resultSecondQueryPath);
//            if(hdfs.exists(path)){
//                hdfs.delete(path, true);
//            }
//            resultRDD.repartition(1).saveAsTextFile (resultSecondQueryPath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static DateTime formatDate(String date){
        return DateTime.parse(date,formatter);
    }

}
