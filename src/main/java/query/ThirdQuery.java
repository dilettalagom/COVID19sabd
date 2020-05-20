package query;

import model.ClassificationMonthPojo;
import model.GlobalStatisticsPojo;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.*;
import query.customCombiner.KeyAccumulator;
import query.customCombiner.TrendMonthComparator;
import scala.Tuple2;
import utility.TrendCalculator;
import utility.parser.General;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ThirdQuery {

    private static String datasetPath = "hdfs://master:54310/dataset/covid19_global.csv";
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


        KeyAccumulator accumulator = new KeyAccumulator();
        Function<Tuple2<String,Double>,
                List<Tuple2<String,Double>>> createAccumulator = accumulator.createAccumulator();

        Function2< List< Tuple2<String, Double> >,
                Tuple2<String, Double>,
                List<Tuple2<String, Double>>> mergeOneValueAcc = accumulator.createMergeOneValueAcc();

        Function2< List<Tuple2<String, Double> >,
                List<Tuple2<String, Double> >,
                List<Tuple2<String, Double>> > mergeObjectsAcc = accumulator.createMergeObjectsAcc();


        //Key is ClassificationKeyPojo, value is List<weekYear, infected>
        JavaPairRDD<ClassificationMonthPojo, List<Tuple2<String, Double>>> combinedClassificationRDD =
                remappedRDD.combineByKey(createAccumulator, mergeOneValueAcc, mergeObjectsAcc);

        JavaRDD<ClassificationMonthPojo> remappedRDD2 =
                combinedClassificationRDD.flatMap(new FlatMapFunction<Tuple2<ClassificationMonthPojo, List<Tuple2<String, Double>>>, ClassificationMonthPojo>() {
                    @Override
                    public Iterator<ClassificationMonthPojo> call(Tuple2<ClassificationMonthPojo, List<Tuple2<String, Double>>> list) throws Exception {

                        ArrayList<ClassificationMonthPojo> result = new ArrayList<>();

                        double[] set = new double[list._2.size()];
                        for(int i = 0; i < list._2().size(); i++){
                            set[i] = list._2().get(i)._2;
                        }
                        double trend = trendCalculator.getTrendCoefficient(set);
                        ClassificationMonthPojo classificationMonthPojo = new ClassificationMonthPojo(list._1.getMonthYear(),list._1.getState(),list._1.getCountry(), trend);
                        result.add(classificationMonthPojo);
                        return result.iterator();
                    }
                });

        //List<ClassificationMonthPojo> take = remappedRDD2.
        //JavaRDD<ClassificationMonthPojo> top50RDD = context.parallelize(take);


        try {
            FileSystem hdfs = FileSystem.get(context.hadoopConfiguration());
            Path path = new Path(resultsThirdQueryPath);
            if (hdfs.exists(path)) {
                hdfs.delete(path, true);
            }
            //statisticsGlobalRDD.repartition(1).saveAsTextFile(resultSecondQueryPath+"/thirdQuery");
            remappedRDD2.repartition(1).saveAsTextFile(resultsThirdQueryPath+"/TOP50");
            context.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
