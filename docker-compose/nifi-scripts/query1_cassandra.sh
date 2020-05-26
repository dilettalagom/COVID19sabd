#!/usr/bin/env python

import sys
sys.path.append('/usr/local/lib/python2.7/dist-packages')
import pandas as pd
import cassandra
import csv
from cassandra.cluster import Cluster


cluster = Cluster(["cassandra1", "cassandra2","cassandra-seed-node"])
session = cluster.connect()
session.set_keyspace('covid19')

with sys.stdin as csvfile:
    readCSV = csv.reader(csvfile, delimiter=',')
    next(readCSV,None)
    for row in readCSV:
        mean1 = float(row[2])
        mean2 = float(row[3])
        session.execute("INSERT INTO QUERY1_RESULTS (week_year,datestart_week, mean_healed, mean_swabs)"
                            " VALUES (%s,%s,%s,%s)", (row[1], row[0], mean1, mean2))