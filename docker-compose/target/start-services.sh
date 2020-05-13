#!/usr/bin/env bash
sh $HADOOP_HOME/etc/hadoop/hadoop-env.sh

rm /tmp/*.pid
service ssh start

if [ $HOSTNAME = "master" ]
then
    echo "----------------------- just doing " $HOSTNAME " stuff, don't mind me -----------------------"
    hdfs namenode -format
    $HADOOP_HOME/sbin/start-dfs.sh

    hdfs dfs -chmod -R 777 /
    hdfs dfs -mkdir /spark-events
    hdfs dfs -mkdir /spark-logs
    hdfs dfs -mkdir /dataset
    hdfs dfs -mkdir /results
    mkdir /tmp/spark-events

    $SPARK_HOME/sbin/start-all.sh
    $SPARK_HOME/sbin/start-history-server.sh
    echo "----------------------- end -----------------------"
fi

/bin/bash