FROM effeerre/hadoop
USER root

# HDFS CONFIG FILES
ADD hdfs-config/hadoop-env.sh $HADOOP_HOME/etc/hadoop/hadoop-env.sh
ADD hdfs-config/core-site.xml $HADOOP_HOME/etc/hadoop/core-site.xml
ADD hdfs-config/hdfs-site.xml $HADOOP_HOME/etc/hadoop/hdfs-site.xml
ADD hdfs-config/mapred-site.xml $HADOOP_HOME/etc/hadoop/mapred-site.xml
ADD hdfs-config/workers $HADOOP_HOME/etc/hadoop/workers

# SPARK LIB FILES
RUN wget http://it.apache.contactlab.it/spark/spark-2.4.3/spark-2.4.3-bin-hadoop2.7.tgz; 
RUN tar -zxf spark-2.4.3-bin-hadoop2.7.tgz -C /usr/local/ ; rm spark-2.4.3-bin-hadoop2.7.tgz
RUN cd /usr/local && ln -s ./spark-2.4.3-bin-hadoop2.7 spark
ENV SPARK_HOME /usr/local/spark
ENV PATH $PATH:$SPARK_HOME/bin
RUN mkdir $SPARK_HOME/yarn-remote-client

# SPARK CONFIG FILES
ADD spark-config/spark-env.sh $SPARK_HOME/conf
ADD spark-config/spark-defaults.conf $SPARK_HOME/conf
ADD spark-config/slaves $SPARK_HOME/conf

#create data directory
RUN mkdir /data
WORKDIR /data

#volume esterno
VOLUME /

#TODO
#ADD start-services.sh /sabd
#RUN chmod 700 /sabd/start-services.sh
#ADD stop-services.sh /sabd
#RUN chmod 700 /sabd/stop-services.sh

# HDFS ports: slave 3-2-1 + master
EXPOSE 9863 9862 9861 9870

# Spark remote debugging for intellij
EXPOSE 43211

# Spark History UI port
EXPOSE 18080
