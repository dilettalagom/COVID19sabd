package query;

import com.google.common.collect.Iterables;
import model.ClassificationMonthPojo;
import model.GlobalStatisticsPojo;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.HashPartitioner;
import org.apache.spark.Partition;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.*;
import org.apache.spark.sql.Encoders;
import query.customCombiner.MonthYearTrendComparator;
import scala.Tuple2;
import utility.ClassificMonthPartitioner;
import utility.TrendCalculator;
import utility.parser.General;
import java.io.IOException;
import java.util.*;


public class ThirdQuery {

    private static String datasetPath = "hdfs://master:54310/dataset/global_nifi_clean.csv";
    private static String resultsThirdQueryPath = "hdfs://master:54310/results";
    private static TrendCalculator trendCalculator = new TrendCalculator();

    public static void main(String[] args) {

        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("SecondQuery");
        JavaSparkContext context = new JavaSparkContext(conf);

        JavaRDD<String> csvData = context.textFile(datasetPath);
        String csvHeader = csvData.first();
        String[] headerSplitted = csvHeader.split(",");
        JavaRDD<String> nonHeaderCSV = csvData.filter(row -> !row.equals(csvHeader));
        String[] dates = Arrays.copyOfRange(headerSplitted, 5, headerSplitted.length);


        /*TODO: potrebbe diventare un solo RDD perche GlobalStatisticsPojo e ClassificationKeyPojo sono praticamente uguali*/
        JavaPairRDD splittedRDD = nonHeaderCSV.mapToPair(
                (String line) -> {
                    String[] lineSplitted = line.split(",");
                    String state = lineSplitted[0];
                    String country = lineSplitted[1];
                    String continent = lineSplitted[4];
                    String[] infectedString = Arrays.copyOfRange(lineSplitted, 5, lineSplitted.length);

                    GlobalStatisticsPojo pojo = new GlobalStatisticsPojo(state, country, continent, infectedString, dates);
                    ClassificationMonthPojo key = new ClassificationMonthPojo(state, country);

                    return new Tuple2(key, pojo);
                }).cache();


        // < ClassificationMonthPojo, Tuple2< data, infected> >
        JavaPairRDD <ClassificationMonthPojo, Tuple2<String,Double>> remappedRDD =
                splittedRDD.flatMapToPair(new PairFlatMapFunction <
                        Tuple2<ClassificationMonthPojo, GlobalStatisticsPojo>,
                        ClassificationMonthPojo,Tuple2<String,Double>
                        >(){
                    @Override
                    public Iterator< Tuple2<ClassificationMonthPojo, Tuple2<String,Double> >>
                    call(Tuple2<ClassificationMonthPojo, GlobalStatisticsPojo> tuplaRDD) throws Exception {

                        ArrayList<  Tuple2<ClassificationMonthPojo, Tuple2<String,Double>> >  tupleList = new ArrayList<>();

                        double[] allInfected = tuplaRDD._2().getInfectedPerDay();

                        for (int i=0; i<allInfected.length;i++ ) {
                            String dateString  = tuplaRDD._2().getInfectedDates()[i];
                            String monthYear = General.createKeyYearMonth(dateString);

                            ClassificationMonthPojo newOne = new ClassificationMonthPojo(tuplaRDD._1().getState(), tuplaRDD._1().getCountry(),monthYear);
                            //refactor RDD elements
                            Tuple2<ClassificationMonthPojo, Tuple2<String,Double>> temp =
                                    new Tuple2<>(newOne, new Tuple2<>( dateString, allInfected[i] ));
                            tupleList.add(temp);
                        }
                        return tupleList.iterator();
                    }
                });



        JavaPairRDD<Tuple2<String,Double>, ClassificationMonthPojo> trendRDD = remappedRDD.groupByKey().mapToPair(
                x -> {
                    int size = Iterables.size(x._2());
                    TrendCalculator trend = new TrendCalculator();
                    double[] y = new double[size];
                    for (int i = 0; i < size; i++) {
                        y[i] = (Iterables.get(x._2, i))._2();
                    }
                    double trendCoefficient = trend.getTrendCoefficient(y);
                    ClassificationMonthPojo pojo = new ClassificationMonthPojo(x._1.getMonthYear(), x._1.getState(), x._1.getCountry(), trendCoefficient);
                    return new Tuple2(new Tuple2(pojo.getMonthYear(),trendCoefficient),pojo);
                }
        );

        List<String> listKeys = trendRDD.keyBy(x -> x._1._1).keys().distinct().collect();
        int numPart = (int) listKeys.stream().count();

        //JavaRDD<Tuple2<Tuple2<String, Double>, ClassificationMonthPojo>> top50RDD = trendRDD.repartitionAndSortWithinPartitions(new ClassificMonthPartitioner(listKeys, numPart), new MonthYearTrendComparator().reversed())
        //il metodo sopra è equivalente a quello attuale

        JavaRDD<Tuple2<Tuple2<String, Double>, ClassificationMonthPojo>> top50RDD = trendRDD.sortByKey(new MonthYearTrendComparator(),false).partitionBy(new ClassificMonthPartitioner(listKeys, numPart))
                .mapPartitions(new FlatMapFunction<Iterator<Tuple2<Tuple2<String, Double>, ClassificationMonthPojo>>, Tuple2<Tuple2<String, Double>, ClassificationMonthPojo>>() {
                    @Override
                    public Iterator<Tuple2<Tuple2<String, Double>, ClassificationMonthPojo>> call(Iterator<Tuple2<Tuple2<String, Double>, ClassificationMonthPojo>> it) throws Exception {
                        List<Tuple2<Tuple2<String, Double>, ClassificationMonthPojo>> filteredResult = new ArrayList<>();
                        int count = 0;
                        while (it.hasNext() && count < 50) {
                            filteredResult.add(it.next());
                            count++;
                        }
                        return filteredResult.iterator();
                    }
                });


        try {
            FileSystem hdfs = FileSystem.get(context.hadoopConfiguration());
            Path path = new Path(resultsThirdQueryPath);
            if (hdfs.exists(path)) {
                hdfs.delete(path, true);
            }
            top50RDD.repartition(1).saveAsTextFile(resultsThirdQueryPath+"/TOP50");
            context.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
