FROM ubuntu:18.04
USER root

RUN apt-get update
RUN apt-get install -y wget openjdk-8-jre

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64


RUN wget https://mirror.nohup.it/apache/nifi/1.11.4/nifi-1.11.4-bin.tar.gz ; tar -zxf nifi-1.11.4-bin.tar.gz -C /usr/local/ ; rm nifi-1.11.4-bin.tar.gz
RUN cd /usr/local && ln -s ./nifi-1.11.4 nifi
ENV NIFI_HOME /usr/local/nifi
#RUN cd $NIFI_HOME/conf;
#RUN mv nifi.properties nifi.properties-old;

ADD nifi-configuration/nifi.properties $NIFI_HOME/conf
#ADD nifi-configuration/bootstrap.conf $NIFI_HOME/conf


VOLUME $NIFI_HOME/conf/flow
#VOLUME $NIFI_HOME/extensions

RUN mkdir /dataset; mkdir /dataset/hdfsconf;
ADD hdfs-configuration/core-site.xml /dataset/hdfsconf
ADD hdfs-configuration/hdfs-site.xml /dataset/hdfsconf
VOLUME /dataset/results-uploader

#RUN mkdir /data/hbaseconf
#ADD hbase-config/hbase-site.xml /data/hbaseconf/

#ADD nar/nifi-location-info-retriever-nar-1.0-SNAPSHOT.nar $NIFI_HOME/lib

ENV URL_DATASET_ITA https://github.com/pcm-dpc/COVID-19/blob/master/dati-andamento-nazionale/dpc-covid19-ita-andamento-nazionale.csv
ENV URL_DATASET_GLOBAL https://github.com/CSSEGISandData/COVID-19/blob/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv
WORKDIR /data
RUN wget $URL_DATASET_ITA; mv dpc-covid19-ita-andamento-nazionale.csv /dataset;
RUN wget $URL_DATASET_GLOBAL; mv time_series_covid19_confirmed_global.csv /dataset;

# trucco per tenere attivo il container dopo lo start
# exec /bin/bash -c "trap : TERM INT; sleep infinity & wait"
CMD $NIFI_HOME/bin/nifi.sh start ; exec /bin/bash -c "trap : TERM INT; sleep infinity & wait"

EXPOSE 4040

# nifi remote debugging
EXPOSE 43210 43212