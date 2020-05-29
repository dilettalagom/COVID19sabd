#!/usr/bin/env python

#bin/cassandra && tail -f logs/system.log
#bin/nodetool status


#echo 'Waiting for seed node' && sleep 30 && /docker-entrypoint.sh cassandra -f

#echo "--------------building COVID19 schemas--------------"
#cqlsh -f COVID19schema.cql

#connect to db
#bin/cqlsh -f schemas/COVID19schema.cql

#!/usr/bin/env python

echo 'Waiting for seed node'


echo "--------------building COVID19 schemas--------------"

cqlsh -f COVID19schema.cql
cqlsh consistency LOCAL_QUORUM
#bin/nodetool status


if [ $HOSTNAME = "cassandra1" ]
then
  sleep 30

elif [ $HOSTNAME = "cassandra2" ]
  then
    sleep 80

elif [ $HOSTNAME = "cassandra3" ]
  then
    sleep 130

fi

/docker-entrypoint.sh cassandra -f