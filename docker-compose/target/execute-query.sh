#!/usr/bin/env bash

usage() {
  cat <<EOF >&1
    Usage: $0 [options] [command]
    Builds or pushes the built-in Spark Docker image.
    Commands:
      execute-query

    Options:
      -q num_query          Execute the specific querys number (1,2,3)
      -t kmeans_type        Specify which k-means implementation will be used (naive, mllib, ml)
      -h help               Print all the COVID19sabd informations
EOF
  exit 1
}

getopts_get_optional_argument() {
  eval next_token=\${$OPTIND}
  if [[ -n $next_token && $next_token != -* ]]; then
    OPTIND=$((OPTIND + 1))
    OPTARG=$next_token
  else
    OPTARG=""
  fi
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
    #hdfs dfs -ls /results/secondQuery
    hdfs dfs -cat /results/secondQuery/part-00000 > result.csv
    #hdfs dfs -cat /results/TOP100/part-00000 > top100.csv


  elif [ $q = 3 ]
  then
    echo "\n--------------------< submitting QUERY 3 >--------------------"
    $SPARK_HOME/bin/spark-submit \
    --class query.ThirdQuery \
    --master "local" \
    /target/jar/COVID19sabd-1.0-SNAPSHOT.jar $kmeans_type

    hdfs dfs -ls /results/thirdQuery
    #hdfs dfs -cat /results/thirdQuery/part-00000 > query3.csv
    hdfs dfs -ls /results/TOP50_*
#    hdfs dfs -cat /results/kmeansmodel_1/part-00000 > kmeansmodel_1.csv
#    hdfs dfs -cat /results/kmeansmodel_2/part-00000 > kmeansmodel_2.csv
#    hdfs dfs -cat /results/kmeansmodel_3/part-00000 > kmeansmodel_3.csv
#    hdfs dfs -cat /results/kmeansmodel_4/part-00000 > kmeansmodel_4.csv
#    hdfs dfs -cat /results/kmeansmodel_5/part-00000 > kmeansmodel_5.csv
    #hdfs dfs -cat /results/kmeansmodel_mlib/part-00000 > kmeansmodel_mlib.csv


  else
    wrong_query_name
  fi

}

OPTARG2=${2:-t}

while getopts "q:t:h:" o;do
	case $o in
	  q) q=$OPTARG;;
	  t) t=$OPTARG;;
	  h) usage
	esac
done
shift "$((OPTIND - 1))"

kmeans_type=""
if [ $t != "" ]
then
    case $t in
        ("naive") kmeans_type="naive";;
        ("mllib") kmeans_type="mllib";;
        ("ml") kmeans_type="ml";;
        (*) wrong_query_name
    esac
fi

execute_query $q $kmeans_type