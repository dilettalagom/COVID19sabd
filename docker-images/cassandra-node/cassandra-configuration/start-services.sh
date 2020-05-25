#!/usr/bin/env python

echo "sto partendo"
#bin/cassandra && tail -f logs/system.log
#bin/nodetool status

#connect to db
bin/cqlsh -f schemas/COVID19schema.cql

