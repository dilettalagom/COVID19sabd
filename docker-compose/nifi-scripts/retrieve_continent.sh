#!/usr/bin/env python
import collections, csv, logging, os, sys, zipfile
sys.path.append('/usr/local/lib/python2.7/dist-packages')
import pycountry_convert as pc
import pandas as pd
import numpy as np
import re
import continetlib.retrieve_continent_lib as c

# get input
input = (sys.stdin).readline().replace('\n','')
line = input.split(',')

# retrieve continent from class continentlib.Continent
try:
	line[len(line)-1] = c.get_continent(float(line[2]), float(line[3]))
except ValueError:
	pass

with(sys.stdout):
	writer = csv.writer(sys.stdout)
	writer.writerow(line)
