package query;

import model.NationalSituationPojo;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class FirstQuery {

    SparkConf conf = new SparkConf()
            .setMaster("local")
            .setAppName("FirstQuery");
    JavaSparkContext context = new JavaSparkContext(conf);


    JavaRDD<String> csvData = context.textFile("hdfs://master:54310/dataset/covid19_national.csv");
    String csvHeader = csvData.first();
    JavaRDD<String> nonHeaderCSV = csvData.filter(row -> !row.equals(csvHeader));

    JavaRDD<NationalSituationPojo> result = nonHeaderCSV.flatMap(
            new WeatherDescriptionParserFlatMap(csvHeader).setCitiesMap(cities)
    );

        return result;

}
