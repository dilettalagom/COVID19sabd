#!/usr/bin/env bash
: ${HADOOP_PREFIX:=/usr/local/hadoop}
sudo $HADOOP_HOME/etc/hadoop/hadoop-env.sh

rm /tmp/*.pid
service ssh start

/bin/bash