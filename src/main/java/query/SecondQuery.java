package query;


import model.ClassificationKeyPojo;
import model.GlobalStatisticsPojo;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import scala.Tuple2;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.IntStream;


public class SecondQuery {


    private static String datasetPath = "hdfs://master:54310/dataset/covid19_global.csv";
    private static String resultSecondQueryPath = "hdfs://master:54310/results/secondQuery";
    private static DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yy");

    public static void main(String[] args) {

        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("SecondQuery");
        JavaSparkContext context = new JavaSparkContext(conf);

        JavaRDD<String> csvData = context.textFile(datasetPath);
        String csvHeader = csvData.first();
        String[] headerSplitted = csvHeader.split(",");
        System.out.println(headerSplitted[0]);
        JavaRDD<String> nonHeaderCSV = csvData.filter(row -> !row.equals(csvHeader));
        String[] dates = Arrays.copyOfRange(headerSplitted, 5, headerSplitted.length - 1);


        JavaPairRDD<ClassificationKeyPojo, GlobalStatisticsPojo> globalInfo = nonHeaderCSV.mapToPair(
                (String line) -> {
                    String[] lineSplitted = line.split(",");
                    String state = lineSplitted[0];
                    String country = lineSplitted[1];
                    String continent = lineSplitted[4];
                    String[] infectedString = Arrays.copyOfRange(lineSplitted, 5, lineSplitted.length);

                    //regression
                    TreeMap<DateTime, Integer> values = new TreeMap<>();
                    double[] infected = Arrays.stream(infectedString).mapToDouble(Double::parseDouble).toArray();
                    double[] x = Arrays.stream(IntStream.range(0, infected.length).toArray()).asDoubleStream().toArray();
                    SimpleRegression r = new SimpleRegression();
                    for (int i = 0; i < infected.length -1; i++) {
                        r.addData(x[i], infected[i]);
                        values.put(formatDate(dates[i]), (int) infected[i]);
                    }
                    double trendlineCoefficient = r.getSlope();
                    ClassificationKeyPojo key = new ClassificationKeyPojo(state, country, trendlineCoefficient);
                    GlobalStatisticsPojo pojo = new GlobalStatisticsPojo(state, country, continent, values, trendlineCoefficient);
                    return new Tuple2(key, pojo);
                }).cache();

        //Classification of top 100 for infected
        List<Tuple2<ClassificationKeyPojo, GlobalStatisticsPojo>> classification = globalInfo.sortByKey(false).take(100);
        JavaPairRDD<ClassificationKeyPojo, GlobalStatisticsPojo> classificationRDD = context.parallelizePairs(classification);

        /*JavaPairRDD<ContinentStatisticsKey, > = classificationRDD.flatMap(
                rddElement -> {
                    for(DateTime date: rddElement._2.getValues().navigableKeySet()) {
                        ContinentStatisticsKey key = new ContinentStatisticsKey(rddElement._2.getContinent(),);
                    }
                }
        )*/

        try {
            FileSystem hdfs = FileSystem.get(context.hadoopConfiguration());
            Path path = new Path(resultSecondQueryPath);
            if (hdfs.exists(path)) {
                hdfs.delete(path, true);
            }
            //resultRDD.repartition(1).saveAsTextFile(resultSecondQueryPath);
            context.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static DateTime formatDate(String date) {
        return DateTime.parse(date, formatter);
    }
}


