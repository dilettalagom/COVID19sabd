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
    echo "\n-------------------- submitting QUERY 1 --------------------"
    
    #query submit
    $SPARK_HOME/bin/spark-submit \
    --class query.FirstQuery \
    --master "local" \
    /target/jar/COVID19sabd-1.0-SNAPSHOT.jar

    #show results in hdfs
    hdfs dfs -ls /results/firstQuery
  
  elif [ $q = 2 ]
  then
    echo "\n-------------------- submitting QUERY 1 --------------------"
    
    #query submit
    $SPARK_HOME/bin/spark-submit \
    --class query.SecondQuery \
    --master "local" \
    /target/jar/COVID19sabd-1.0-SNAPSHOT.jar

    #show results in hdfs
    hdfs dfs -ls /results/secondQuery

  elif [ $q = 3 ]
  then
    echo "\n-------------------- submitting QUERY 1 --------------------"
    $SPARK_HOME/bin/spark-submit \
    --class query.ThirdQuery \
    --master "local" \
    /target/jar/COVID19sabd-1.0-SNAPSHOT.jar
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