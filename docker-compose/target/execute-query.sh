#!/usr/bin/env bash

usage() {
  cat <<EOF >&1
    Usage: $0 [options] [command]
    Builds or pushes the built-in Spark Docker image.
    Commands:
      execute-query

    Options:
      -q num_query          Execute the specific querys number (1,2,3)
      -h help               Print all the COVID19sabd informations
EOF
  exit 1
}

wrong_query_name() {
  echo "ERROR: Select the right query_number (1,2,3)";
  usage
}

execute_query() {

  if [ $q = 1 ]
  then
    echo "\n--------------------< submitting QUERY 1 >--------------------"
    
    #query submit
    $SPARK_HOME/bin/spark-submit \
    --class query.FirstQuery \
    --master "local" \
    /target/jar/COVID19sabd-1.0-SNAPSHOT.jar

    #show results in hdfs
    hdfs dfs -ls /results/firstQuery
    hdfs dfs -cat /results/firstQuery/part-00000
  
  elif [ $q = 2 ]
  then
    echo "\n--------------------< submitting QUERY 2 >--------------------"
    
    #query submit
    $SPARK_HOME/bin/spark-submit \
    --class query.SecondQuery \
    --master "local" \
    /target/jar/COVID19sabd-1.0-SNAPSHOT.jar --num-executors 1 --executor-cores 1 \
    --conf "spark.executor.extraJavaOptions=-agentlib:jdwp=transport=dt_socket,server=n,address=mbp-di-giorgia.homenet.telecomitalia.it:43211,suspend=y,onthrow=<FQ exception class name>,onuncaught=<y/n>"
    
    #show results in hdfs
    hdfs dfs -ls /results/secondQuery
    hdfs dfs -cat /results/secondQuery/part-00000 > statisticsGlobalRDD.csv
    hdfs dfs -cat /results/TOP100/part-00000 > top100.csv


  elif [ $q = 3 ]
  then
    echo "\n--------------------< submitting QUERY 3 >--------------------"
    $SPARK_HOME/bin/spark-submit \
    --class query.ThirdQuery \
    --master "local" \
    /target/jar/COVID19sabd-1.0-SNAPSHOT.jar

    hdfs dfs -ls /results/thirdQuery
    #hdfs dfs -cat /results/thirdQuery/part-00000 > query3.csv
    hdfs dfs -cat /results/TOP50/part-00000 > top50.csv


  else
    wrong_query_name
  fi

}

while getopts "q:h:" o;do
	case $o in
	  q) q=$OPTARG;;
	  h) usage
	esac
done
shift "$((OPTIND - 1))"

execute_query $q