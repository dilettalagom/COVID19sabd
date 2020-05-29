#!/usr/bin/env python
import sys
sys.path.append('/usr/local/lib/python2.7/dist-packages')
import re, csv

# get input
line = sys.stdin.readline()

if "data,dimessi_guariti,tamponi" in line:
    sys.stdout.write(line + '\n')

else:
    # remove comma between quotes and split
    line = line.replace('\n', '').split(',')

    # expectedHeader: data
    if re.compile(r'^(-?(?:[1-9][0-9]*)?[0-9]{4})-(1[0-2]|0[1-9])-(3[01]|0[1-9]|[12][0-9])T(2[0-3]|[01][0-9]):([0-5][0-9]):([0-5][0-9])(\.[0-9]+)?(Z|[+-](?:2[0-3]|[01][0-9]):[0-5][0-9])?$').match(line[0]) is None:
        # if is not a date
        exit(0)


    # expectedHeader: dimessi_guariti
    try:
        dimessi_guariti = int(line[1])
    except ValueError:
        exit(0)


    # expectedHeader: tamponi
    try:
        tamponi = int(line[2])
    except ValueError:
        exit(0)


    # write back
    with sys.stdout:
        writer = csv.writer(sys.stdout)
        writer.writerow(line)




