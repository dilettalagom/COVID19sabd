FROM  dilettalagom/base
USER root

RUN apt-get update;

# copy HDFS local configuratio in spark_hdfs_container
ADD hdfs-configuration/hadoop-env.sh $HADOOP_CONF_DIR/hadoop-env.sh
ADD hdfs-configuration/core.xml $HADOOP_CONF_DIR/core-site.xml
ADD hdfs-configuration/workers-names $HADOOP_CONF_DIR/workers
#ADD hdfs-configuration/hdfs-site.xml $HADOOP_HOME/etc/hadoop/hdfs-site.xml
#-> per permessi super-root/replicazione (rischiosi?)
#ADD hdfs-config/mapred-site.xml $HADOOP_HOME/etc/hadoop/mapred-site.xml -> per yarn (sostituire misos ?)

#  install SPARK LIB in spark_hdfs_container
RUN wget https://downloads.apache.org/spark/spark-2.4.5/spark-2.4.5-bin-hadoop2.7.tgz
RUN tar -zxf spark-2.4.5-bin-hadoop2.7.tgz -C /usr/local/ ; rm spark-2.4.5-bin-hadoop2.7.tgz
RUN cd /usr/local && ln -s ./spark-2.4.5-bin-hadoop2.7 spark
ENV SPARK_HOME /usr/local/spark
ENV PATH $PATH:$SPARK_HOME/bin

# SPARK CONFIG FILES
ADD spark-configuration/spark-env.sh $SPARK_HOME/conf
ADD spark-configuration/spark-defaults.conf $SPARK_HOME/conf
ADD spark-configuration/workers-names $SPARK_HOME/conf

#create main directory
RUN mkdir /target

#external linking
VOLUME /target

ENV PATH $PATH:$SPARK_HOME/bin:$HADOOP_PREFIX/bin
WORKDIR /target


#TODO
#ADD start-services.sh /target
#RUN chmod 700 /target/start-services.sh
#ADD stop-services.sh /target
#RUN chmod 700 /target/stop-services.sh

# HDFS ports: slave 3-2-1 + master
EXPOSE 9861 9862 9863 9870
# SSH port
EXPOSE 22

# Spark remote debugging for intellij
EXPOSE 43211

# Spark History UI port
EXPOSE 18080
