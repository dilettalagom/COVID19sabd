#!/usr/bin/env bash
hdfs namenode -format
$HADOOP_HOME/sbin/start-dfs.sh
$HADOOP_HOME/etc/hadoop/hadoop-env.sh

hdfs dfs -chmod -R 777 /
hdfs dfs -mkdir /spark-events
hdfs dfs -mkdir /spark-logs

sleep 30

$SPARK_HOME/sbin/start-all.sh
$SPARK_HOME/sbin/start-history-server.sh