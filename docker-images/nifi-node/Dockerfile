FROM ubuntu
USER root

RUN apt-get update
RUN apt-get install -y sudo wget openjdk-8-jre curl

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64


#RUN wget https://mirror.nohup.it/apache/nifi/1.11.4/nifi-1.11.4-bin.tar.gz ; tar -zxf nifi-1.11.4-bin.tar.gz -C /usr/local/ ; rm nifi-1.11.4-bin.tar.gz
ADD nifi-configuration/nifi-1.11.4-bin.tar.gz /usr/local/
ADD nifi-configuration/start-services.sh /usr/local/
ENV NIFI_HOME /usr/local/nifi-1.11.4
ENV NIFI_DIR /usr/local/nifi
##RUN cd $NIFI_HOME/conf;
##RUN mv nifi.properties nifi.properties-old;
#
ADD nifi-configuration/nifi.properties $NIFI_HOME/conf
#ADD nifi-configuration/bootstrap.conf $NIFI_HOME/conf

VOLUME $NIFI_DIR/conf/flow
##VOLUME $NIFI_HOME/extensions

RUN mkdir /dataset;
RUN mkdir /dataset/hdfsconf;
ADD hdfs-configuration/core-site.xml /dataset/hdfsconf
ADD hdfs-configuration/hdfs-site.xml /dataset/hdfsconf
#VOLUME /dataset/results-uploader
#
##RUN mkdir /data/hbaseconf
##ADD hbase-config/hbase-site.xml /data/hbaseconf/
#
##ADD nar/nifi-location-info-retriever-nar-1.0-SNAPSHOT.nar $NIFI_HOME/lib
#
ENV URL_DATASET_ITA https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-andamento-nazionale/dpc-covid19-ita-andamento-nazionale.csv
ENV URL_DATASET_GLOBAL https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv
WORKDIR /dataset
RUN wget $URL_DATASET_ITA; mv dpc-covid19-ita-andamento-nazionale.csv /dataset/covid19_national.csv;
RUN wget $URL_DATASET_GLOBAL; mv time_series_covid19_confirmed_global.csv /dataset/covid19_global.csv;

CMD sh /usr/local/start-services.sh
EXPOSE 4040
#to connect to nifi ui: 0.0.0.0:4040/nifi

# nifi remote debugging
EXPOSE 43210 43212

