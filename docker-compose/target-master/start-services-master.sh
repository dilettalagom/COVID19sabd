#!/usr/bin/env bash
: ${HADOOP_PREFIX:=/usr/local/hadoop}
sudo $HADOOP_HOME/etc/hadoop/hadoop-env.sh

rm /tmp/*.pid
service ssh start

hdfs namenode -format
$HADOOP_HOME/sbin/start-dfs.sh

hdfs dfs -chmod -R 777 /
hdfs dfs -mkdir /spark-events
hdfs dfs -mkdir /spark-logs
mkdir /tmp/spark-events

#sleep 30

$SPARK_HOME/sbin/start-all.sh
$SPARK_HOME/sbin/start-history-server.sh

/bin/bash