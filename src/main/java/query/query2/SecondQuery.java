package query.query2;


import model.ClassificationKeyPojo;
import model.ContinentWeekKey;
import model.GlobalStatisticsPojo;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.util.StatCounter;
import query.customCombiner.KeyAccumulator;
import query.customCombiner.TrendComparator;
import scala.Tuple2;
import scala.Tuple4;
import utility.parser.General;

import java.io.IOException;
import java.util.*;


public class SecondQuery {


    private static String datasetPath = "hdfs://master:54310/dataset/covid19_global.csv";
    private static String resultSecondQueryPath = "hdfs://master:54310/results";

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
        String[] dates = Arrays.copyOfRange(headerSplitted, 5, headerSplitted.length);


        /*TODO: potrebbe diventare un solo RDD perche GlobalStatisticsPojo e ClassificationKeyPojo sono praticamente uguali*/
        JavaPairRDD splittedRDD =  nonHeaderCSV.mapToPair(
                (String line) -> {
                    String[] lineSplitted = line.split(",");
                    String state = lineSplitted[0];
                    String country = lineSplitted[1];
                    String continent = lineSplitted[4];
                    String[] infectedString = Arrays.copyOfRange(lineSplitted, 5, lineSplitted.length);

                    GlobalStatisticsPojo pojo = new GlobalStatisticsPojo(state, country, continent, infectedString, dates);
                    ClassificationKeyPojo key = new ClassificationKeyPojo(pojo.getTrendCoefficient(), state, country, continent);


                    //Query2Key key = new Query2Key(pojo.getContinent(), pojo.getTrendCoefficient());
                    return new Tuple2(key,pojo);
                }).cache();

        List top100List = splittedRDD.sortByKey(new TrendComparator(), false).take(100);
        JavaPairRDD top100RDD = context.parallelizePairs(top100List);



        // < ClassificationKeyPojo, Tuple2< data, infected> >
        JavaPairRDD < ClassificationKeyPojo, Tuple2<String,Double>> remappedRDD =
                top100RDD.flatMapToPair(new PairFlatMapFunction <
                        Tuple2<ClassificationKeyPojo, GlobalStatisticsPojo>,
                        ClassificationKeyPojo,Tuple2<String,Double>
                        >(){
                    @Override
                    public Iterator< Tuple2< ClassificationKeyPojo, Tuple2<String,Double> >>
                    call(Tuple2<ClassificationKeyPojo, GlobalStatisticsPojo> tuplaRDD) throws Exception {

                        ArrayList<  Tuple2<ClassificationKeyPojo, Tuple2<String,Double>> >  tupleList = new ArrayList<>();

                        double[] allInfected = tuplaRDD._2().getInfectedPerDay();

                        for (int i=0; i<allInfected.length;i++ ) {
                            String dateString  = tuplaRDD._2().getInfectedDates()[i];

                            //update weekYear in Key
                            String weekYear = General.createKeyYearMonth(dateString);
                            //tuplaRDD._1().setWeekYear(weekYear);

                            ClassificationKeyPojo newOne = new ClassificationKeyPojo(tuplaRDD._1().getTrendCoefficient(), tuplaRDD._1().getState(), tuplaRDD._1().getCountry(),tuplaRDD._1().getContinent(),weekYear);
                            //refactor RDD elements
                            Tuple2<ClassificationKeyPojo, Tuple2<String,Double>> temp =
                                    new Tuple2<>(newOne,
                                            new Tuple2<>( dateString, allInfected[i] ));
                            tupleList.add(temp);
                        }
                        return tupleList.iterator();
                    }
                });


               //Create custom-accumulator instance and its methods.
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
        JavaPairRDD<ClassificationKeyPojo, List<Tuple2<String, Double>>> combinedClassificationRDD =
                remappedRDD.combineByKey(createAccumulator, mergeOneValueAcc, mergeObjectsAcc);



        JavaPairRDD<ContinentWeekKey, Tuple2<String, Double>> classificationKeyPojoTuple2JavaPairRDD = combinedClassificationRDD.flatMapToPair(new PairFlatMapFunction<
                Tuple2<ClassificationKeyPojo, List<Tuple2<String, Double>>>,
                ContinentWeekKey, Tuple2<String, Double>
                >() {
            @Override
            public Iterator<Tuple2<ContinentWeekKey, Tuple2<String, Double>>>

            call(Tuple2<ClassificationKeyPojo, List<Tuple2<String, Double>>> tuplaRDD) throws Exception {

                ArrayList<Tuple2<ContinentWeekKey, Tuple2<String, Double>>> tupleList = new ArrayList<>();

                for (Tuple2<String, Double> tupla : tuplaRDD._2()) {

                    ContinentWeekKey newKey = new ContinentWeekKey(tuplaRDD._1().getContinent(),tuplaRDD._1().getWeekYear());
                    Tuple2<ContinentWeekKey, Tuple2<String, Double>> exploted = new Tuple2<>(newKey, tupla);
                    tupleList.add(exploted);
                }

                return tupleList.iterator();
            }
        });

        JavaPairRDD statisticsGlobalRDD = classificationKeyPojoTuple2JavaPairRDD

                //Value = infected x weekYear x continent
                .aggregateByKey(
                        new StatCounter(),
                        (acc, x) -> acc.merge(x._2()),
                        StatCounter::merge
                )

                //Key = ClassificationKeyPojo, Value = Tuple4<mean, std, min, max>
                .mapToPair(x -> {
                    ContinentWeekKey key = x._1();
                    Double mean = x._2().mean();
                    Double dev = x._2().stdev();
                    Double min = x._2().min();
                    Double max = x._2().max();
                    return new Tuple2<>(key, new Tuple4<>(mean, dev, min, max));
                })
                .sortByKey();



        try {
            FileSystem hdfs = FileSystem.get(context.hadoopConfiguration());
            Path path = new Path(resultSecondQueryPath);
            if (hdfs.exists(path)) {
                hdfs.delete(path, true);
            }

            statisticsGlobalRDD.repartition(1).saveAsTextFile(resultSecondQueryPath+"/secondQuery");
            top100RDD.repartition(1).saveAsTextFile(resultSecondQueryPath+"/TOP100");
            context.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}


