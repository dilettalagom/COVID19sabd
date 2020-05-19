package query.query2;


import model.ClassificationKeyPojo;
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
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import query.customCombiner.KeyAccumulator;
import query.customCombiner.TrendComparator;
import query.customCombiner.WeekyearContinentComparator;
import scala.Tuple2;
import scala.Tuple4;
import utility.parser.General;

import java.io.IOException;
import java.util.*;


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
        String[] dates = Arrays.copyOfRange(headerSplitted, 5, headerSplitted.length);


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


       /* JavaPairRDD remappedRDD =
                splittedRDD.flatMapToPair(new PairFlatMapFunction < Tuple2<ClassificationKeyPojo, GlobalStatisticsPojo>, ClassificationKeyPojo, Tuple2<GlobalStatisticsPojo, Tuple2<String,Double>> >(){
                    @Override
                    public Iterator< Tuple2<ClassificationKeyPojo, Tuple2<GlobalStatisticsPojo, Tuple2<String,Double>>> >
                    call(Tuple2<ClassificationKeyPojo, GlobalStatisticsPojo> tuplaRDD) throws Exception {

                        ArrayList<  Tuple2<ClassificationKeyPojo, Tuple2<GlobalStatisticsPojo, Tuple2<String,Double>>> >  tupleList = new ArrayList<>();

                        double[] allInfected = tuplaRDD._2().getInfectedPerDay();

                        for (int i=0; i<allInfected.length;i++ ) {
                            String dateString  = tuplaRDD._2().getInfectedDates()[i];
                            String weekYear = General.createKeyYearMonth(dateString);

                            tuplaRDD._1().setWeekYear(weekYear);
                            Tuple2<ClassificationKeyPojo, Tuple2<GlobalStatisticsPojo, Tuple2<String,Double>>> temp =
                                    new Tuple2<>(tuplaRDD._1(),
                                            new Tuple2<>(tuplaRDD._2(),
                                                    new Tuple2<>(weekYear, allInfected[i] )));
                            tupleList.add(temp);
                        }
                        return tupleList.iterator();
                    }
                });
                */

        // < ClassificationKeyPojo, Tuple2< data, infected> >
        JavaPairRDD < ClassificationKeyPojo, Tuple2<String,Double>> remappedRDD =
                splittedRDD.flatMapToPair(new PairFlatMapFunction <
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
                            tuplaRDD._1().setWeekYear(weekYear);

                            //refactor RDD elements
                            Tuple2<ClassificationKeyPojo, Tuple2<String,Double>> temp =
                                    new Tuple2<>(tuplaRDD._1(),
                                            new Tuple2<>( dateString, allInfected[i] ));
                            tupleList.add(temp);
                        }
                        return tupleList.iterator();
                    }
                });


        //List orderedByTrendRDD = remappedRDD.sortByKey(new TrendComparator(), false).top(100);

        // orderedByTrendRD
            JavaPairRDD<ClassificationKeyPojo, Tuple2<String, Double>> classificationKeyPojoTuple2JavaPairRDD = remappedRDD.sortByKey(new TrendComparator(), false)

        JavaPairRDD  <ClassificationKeyPojo, Tuple2<String,Double>> classificationRDD = context.parallelizePairs(orderedByTrendRDD);



        /* List orderedByTrendRDD = remappedRDD.sortByKey(false).top(100);
        JavaPairRDD  <ClassificationKeyPojo, Tuple2<String,Double>> classificationRDD = context.parallelizePairs(orderedByTrendRDD);




       Function< Tuple2<ClassificationKeyPojo, Tuple2<String,Double>>,
                List<Tuple2<ClassificationKeyPojo, Tuple2<String,Double>>> > createAccumulator = accumulator.createAccumulator();

        Function2< List< Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> > ,
                Tuple2<ClassificationKeyPojo, Tuple2<String, Double>>,
                List<Tuple2<ClassificationKeyPojo, Tuple2<String, Double>>>> mergeOneValueAcc = accumulator.createMergeOneValueAcc();

        Function2< List<Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> >,
                List<Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> >,
                List<Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> >> mergeObjectsAcc = accumulator.createMergeObjectsAcc();

    */

   /*TODO:       //Create custom-accumulator instance and its methods.
      KeyAccumulator accumulator = new KeyAccumulator();
        Function<Tuple2<String,Double>,
                List<Tuple2<String,Double>> > createAccumulator = accumulator.createAccumulator();

        Function2< List< Tuple2<String, Double> >,
                Tuple2<String, Double>,
                List<Tuple2<String, Double>>> mergeOneValueAcc = accumulator.createMergeOneValueAcc();

        Function2< List<Tuple2<String, Double> >,
                List<Tuple2<String, Double> >,
                List<Tuple2<String, Double>> > mergeObjectsAcc = accumulator.createMergeObjectsAcc();


        //Key is ClassificationKeyPojo, value is List<weekYear, infected>
        JavaPairRDD<ClassificationKeyPojo, List<Tuple2<String, Double>>> combinedClassificationRDD =
                classificationRDD.combineByKey(createAccumulator, mergeOneValueAcc, mergeObjectsAcc);



        JavaPairRDD <ClassificationKeyPojo, Tuple2<String, Double>> explotedClassificationERDD =
                combinedClassificationRDD.flatMapToPair(new PairFlatMapFunction<
                        Tuple2<ClassificationKeyPojo, List<Tuple2<String, Double>>>,
                        ClassificationKeyPojo, Tuple2<String, Double>
                        >(){
                    @Override
                    public Iterator< Tuple2< ClassificationKeyPojo, Tuple2<String, Double>  >>

                    call(Tuple2<ClassificationKeyPojo, List<Tuple2<String, Double>>> tuplaRDD) throws Exception {

                        ArrayList< Tuple2<ClassificationKeyPojo, Tuple2<String, Double>> >  tupleList = new ArrayList<>();

                        for(Tuple2<String, Double> tupla : tuplaRDD._2()) {

                            Tuple2 exploted = new Tuple2<>(tuplaRDD._1(), tupla);
                            tupleList.add(exploted);
                        }

                        return tupleList.iterator();
                    }
                });




        JavaPairRDD statisticsGlobalRDD = explotedClassificationERDD

                //Value = infected x weekYear x continent
                .aggregateByKey(
                        new StatCounter(),
                        (acc, x) -> acc.merge(x._2()),
                        StatCounter::merge
                )

                //Key = ClassificationKeyPojo, Value = Tuple4<mean, std, min, max>
                .mapToPair(x -> {
                    ClassificationKeyPojo key = x._1();
                    Double mean = x._2().mean();
                    Double dev = x._2().stdev();
                    Double min = x._2().min();
                    Double max = x._2().max();
                    return new Tuple2<>(key, new Tuple4<>(mean, dev, min, max));
                })
                .sortByKey();





        /*JavaPairRDD<ClassificationKeyPojo, GlobalStatisticsPojo> globalInfo = nonHeaderCSV.mapToPair(
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



        JavaPairRDD<ClassificationKeyPojo, GlobalStatisticsPojo> orderedByTrendRDD = globalInfo.sortByKey(false);

        orderedByTrendRDD.fla

        JavaPairRDD remappedRDD = orderedByTrendRDD.flatMap(orderedRDD -> {

            TreeMap<DateTime, Integer> infectedTreeMap = orderedRDD._2.getInfectedPerDay();


            for(DateTime dateTimeKey: infectedTreeMap.keySet()) {
                ContinentStatisticsKey newKey = new ContinentStatisticsKey(orderedRDD._2.getContinent(), dateTimeKey.toString());
            }

            return new Tuple2(newKey, newPojo);
        }).take(100);



      /*
      JavaPairRDD<ClassificationKeyPojo, GlobalStatisticsPojo> classificationRDD = context.parallelizePairs(classification);
      JavaPairRDD<ContinentStatisticsKey, GlobalStatisticsPojo> remapped = classification.flatMap(
                rddElement -> {

                    rddElement.
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
            classificationRDD.repartition(1).saveAsTextFile(resultSecondQueryPath);
            context.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static DateTime formatDate(String date) {
        return DateTime.parse(date, formatter);
    }
}


