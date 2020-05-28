#!/usr/bin/env python
import sys
sys.path.append('/usr/local/lib/python2.7/dist-packages')
import re, csv

# get input
line = sys.stdin.readline()

if "data,dimessi_guariti,tamponi" in line:
    sys.stdout.write(line)

else:
    # remove comma between quotes and split
    line = line.replace('\n', '').split(',')

    # expectedHeader: data
    if re.compile("^(?:[1-9]\d{3}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1\d|2[0-8])|"
                  "(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|"
                  "(?:[1-9]\d(?:0[48]|[2468][048]|[13579][26])|"
                  "(?:[2468][048]|[13579][26])00)-02-29)T(?:[01]\d|2[0-3]):"
                  "[0-5]\d:[0-5]\d(?:Z|[+-][01]\d:[0-5]\d)$").match(line[0]) is None:
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