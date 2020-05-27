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
        mean = float(row[3])
        std_dev = float(row[4])
        min = float(row[5])
        max = float(row[6])
        session.execute("INSERT INTO QUERY2_RESULTS (continent, week_year, datestart_week, mean_confirmed_cases, dev_std_confirmed_cases, min_confirmed_cases, max_confirmed_cases)"
                            " VALUES (%s,%s,%s,%s,%s,%s,%s)", (row[0], row[1], row[2],mean, std_dev, min, max))

