FROM cassandra:latest
USER root

RUN apt-get update
RUN apt-get install -y sudo wget vim

ENV PATH $PATH:$JAVA_HOME

ENV CASSANDRA_HOME /opt/cassandra
ENV CASSANDRA_CONFIG /etc/cassandra
ENV PATH $CASSANDRA_HOME/bin:$PATH

#add config file for graphite connector
#ADD cassandra-configuration/cassandra.yaml $CASSANDRA_CONFIG/cassandra.yaml

#add schemas files
VOLUME $CASSANDRA_HOME/schemas
WORKDIR $CASSANDRA_HOME/schemas
#RUN cqlsh -f COVID19schema.cql

CMD sh ./start-services.sh
EXPOSE 7199 7000 7001 9042 9160

