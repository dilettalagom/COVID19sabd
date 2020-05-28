#!/usr/bin/env python

#bin/cassandra && tail -f logs/system.log
#bin/nodetool status


echo 'Waiting for seed node' && sleep 30 && /docker-entrypoint.sh cassandra -f

echo "--------------building COVID19 schemas--------------"
bin/cqlsh -f COVID19schema.cql

#connect to db
#bin/cqlsh -f schemas/COVID19schema.cql