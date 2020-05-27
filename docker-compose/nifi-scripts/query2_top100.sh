#!/usr/bin/env python

import sys
sys.path.append('/usr/local/lib/python2.7/dist-packages')
import pandas as pd
import csv
from cassandra.cluster import Cluster


cluster = Cluster(["cassandra1", "cassandra2","cassandra-seed-node"])
session = cluster.connect()
session.set_keyspace('covid19')

with sys.stdin as csvfile:
    readCSV = csv.reader(csvfile, delimiter=',')

    next(readCSV,None)
    for row in readCSV:
        trend = float(row[0])
        session.execute("INSERT INTO QUERY2_TOP100_RESULTS (trendline_coefficient, state, country)"
                            " VALUES (%s,%s,%s)", (trend, row[1], row[2]))