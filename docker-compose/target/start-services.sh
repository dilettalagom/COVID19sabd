#!/usr/bin/env bash
hdfs namenode -format
$HADOOP_HOME/sbin/start-dfs.sh
$SPARK_HOME/sbin/start-all.sh


$HADOOP_HOME/etc/hadoop/hadoop-env.sh

sleep 30

hdfs dfs -chmod -R 777 /
hdfs dfs -mkdir /spark-events
hdfs dfs -mkdir /spark-logs
$SPARK_HOME/sbin/start-history-server.sh