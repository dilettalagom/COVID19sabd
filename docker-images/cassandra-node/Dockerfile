FROM cassandra:latest
USER root

RUN apt-get update
RUN apt-get install -y sudo wget vim openjdk-8-jre python2.7 python-pip

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64
ENV PATH $PATH:$JAVA_HOME

ENV CASSANDRA_HOME /opt/cassandra
ENV CASSANDRA_CONFIG /etc/cassandra
ENV PATH $CASSANDRA_HOME/bin:$PATH

#add config file
ADD cassandra-configuration/cassandra.yaml $CASSANDRA_CONFIG/cassandra.yaml

#add schemas files
RUN mkdir $CASSANDRA_HOME/schemas;
ADD ../../docker-compose/cassandra-schemas/COVID19schema.cql  $CASSANDRA_HOME/schemas/COVID19schema.cql
WORKDIR $CASSANDRA_HOME


#add entrypoint file
#COPY ./cassandra-configuration/docker-entrypoint.sh /docker-entrypoint.sh
#RUN chmod +x docker-entrypoint.sh
#RUN sh docker-entrypoint.sh


EXPOSE 7199 7000 7001 9042 9160
#8080 7077 22222
#ENTRYPOINT ["/docker-entrypoint.sh"]
#CMD ["cassandra","-f"]

#start services
ADD cassandra-configuration/start-services.sh ./start-services.sh
#RUN sh ./start-services.sh
