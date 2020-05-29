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
    for row in readCSV:
        cluster_id = int(row[0])
        trend = float(row[1])
        lat = float(row[5])
        lon = float(row[6])
        session.execute_async("INSERT INTO QUERY3_KMEANS_NAIVE_RESULTS (cluster_id, month_year, trend, state, country,lat, lon) VALUES (%s,%s,%s,%s,%s,%s,%s)", (cluster_id, row[4], trend, row[2], row[3],lat, lon))
