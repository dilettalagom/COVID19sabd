package query;

import com.google.common.collect.Iterables;
import kmeans.KMeansMLExecutor;
import kmeans.KMeansMLibExecutor;
import kmeans.KMeansNaiveExecutor;
import model.ClassificationMonthPojo;
import model.GlobalStatisticsPojo;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.*;
import utility.comparators.MonthYearTrendComparator;
import scala.Tuple2;
import utility.regression.TrendCalculator;
import utility.parser.CalendarUtility;
import java.util.*;


public class ThirdQuery {

    private static String datasetPath = "hdfs://master:54310/dataset/covid19_global.csv";
    private static String resultsThirdQueryPath = "hdfs://master:54310/results";

    public static void main(String[] args) {

        String kmeansType = args[0]; //{naive, mllib, ml}

        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("SecondQuery");
        JavaSparkContext context = new JavaSparkContext(conf);


        JavaRDD<String> csvData = context.textFile(datasetPath);
        String csvHeader = csvData.first();
        String[] headerSplitted = csvHeader.split(",");
        JavaRDD<String> nonHeaderCSV = csvData.filter(row -> !row.equals(csvHeader));
        String[] dates = Arrays.copyOfRange(headerSplitted, 5, headerSplitted.length);


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
                            String monthYear = CalendarUtility.createKeyYearMonth(dateString);
                            ClassificationMonthPojo newOne = new ClassificationMonthPojo(tuplaRDD._1().getState(), tuplaRDD._1().getCountry(),monthYear);
                            //refactor RDD elements
                            Tuple2<ClassificationMonthPojo, Tuple2<String,Double>> temp = new Tuple2<>(newOne, new Tuple2<>( dateString, allInfected[i] ));
                            tupleList.add(temp);
                        }
                        return tupleList.iterator();
                    }
                });


        //<<mese,trend>,pojo>
        JavaPairRDD<Tuple2<String,Double>, ClassificationMonthPojo> trendRDD =
                remappedRDD.groupByKey().mapToPair(
                        x -> {
                            int size = Iterables.size(x._2());
                            double[] y = new double[size];

                            for (int i = 0; i < size; i++) {
                                y[i] = (Iterables.get(x._2, i))._2();
                            }

                            double trendCoefficient = TrendCalculator.getInstance().getTrendCoefficient(y);
                            ClassificationMonthPojo pojo = new ClassificationMonthPojo(x._1.getMonthYear(), x._1.getState(), x._1.getCountry(), trendCoefficient);
                            return new Tuple2(new Tuple2(pojo.getMonthYear(),trendCoefficient),pojo);
                        }
                );

        List<String> listKeys = trendRDD.keyBy(x -> x._1._1).keys().distinct().collect();

        //<<month_year,trend>,pojo>
        Map<String, JavaPairRDD<Tuple2<String,Double>, ClassificationMonthPojo>> monthMap = new HashMap<>();
        listKeys.forEach(key ->{
            monthMap.computeIfAbsent(key, key2 -> trendRDD.filter(x -> x._1._1.equals(key2)));
        });

        monthMap.forEach((monthKey, javaRDD ) -> {
            List<Tuple2<Tuple2<String, Double>, ClassificationMonthPojo>> top50List =
                    javaRDD.sortByKey(new MonthYearTrendComparator().reversed())
                            .take(50);
            monthMap.put(monthKey, context.parallelizePairs(top50List).sortByKey(new MonthYearTrendComparator().reversed()));
        });


        switch (kmeansType){
            case "naive":
                KMeansNaiveExecutor naive = new KMeansNaiveExecutor(4,20,1e-4, context, resultsThirdQueryPath);
                naive.starter(monthMap);
                return;

            case "mllib":
                KMeansMLibExecutor mllib = new KMeansMLibExecutor(4,20,1e-4, context, resultsThirdQueryPath);
                mllib.starter(monthMap);
                return;

            case "ml":
                KMeansMLExecutor ml = new KMeansMLExecutor(4,20,1e-4, context, resultsThirdQueryPath);
                ml.starter(monthMap);
                return;

        }
        context.stop();
    }



}
