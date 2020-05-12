package query;

import model.NationalStatisticsPojo;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.joda.time.DateTime;
import scala.Tuple3;
import utility.parser.NationalStatisticsParser;


public class FirstQuery {

    SparkConf conf = new SparkConf()
            .setMaster("local")
            .setAppName("FirstQuery");
    JavaSparkContext context = new JavaSparkContext(conf);

    JavaRDD<String> csvData = context.textFile("hdfs://master:54310/dataset/covid19_national.csv");
    //String csvHeader = csvData.first();
    //JavaRDD<String> nonHeaderCSV = csvData.filter(row -> !row.equals(csvHeader));


    JavaRDD<NationalStatisticsPojo> natStat = csvData.map(line -> NationalStatisticsParser.parseCSV(line));
    JavaRDD<Tuple3<DateTime, Integer, Integer>> result = natStat.map(x -> new Tuple3<DateTime, Integer, Integer>
            (x.getDate(), x.getNumHealed(), x.getNumTampons()));

}



giorno1 giorno2 giorno 3 giorno 4 giorno 5

week1 -> {giorno1 = {data, guariti, malati}, giorno2, giorno 3 giorno 4 giorno 5}

