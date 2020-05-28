#!/usr/bin/env python
import sys
sys.path.append('/usr/local/lib/python2.7/dist-packages')
import re, csv

# get input
line = sys.stdin.readline()

# send back header
if "Province/State,Country/Region,Lat,Long" in line:
    sys.stdout.write(line)


else :
    # remove comma between quotes and split
    line = re.sub(r'(")([^",]+),([^"]+)(")', r'\2 \3', line).replace('\n', '').split(',')

    # expectedHeader: Province/State, Country/Region
    if line[0] != '' and re.compile("^[a-zA-Z-*|'()&-*\s]*$").match(line[0]) is None:
        # if Province/State is not a string, change in null_string
        line[0] = ''
    if re.compile("^[a-zA-Z-*|'()&-*\s]*$").match(line[1]) is None:
        # if Country/Region is not a string, remove completely the row
        exit(0)

    # expectedHeader: Lat,Long
    try:
        lat = float(line[2])
        lon = float(line[3])
        if not (-90.0 <= lat <= 90.0 and -180.0 <= lon <= 180.0):
            exit(0)
    except ValueError:
        # if Lat,Long are not float values, remove completely the row
        exit(0)

    # expectedHeader: [List of dates]
    for i in range(len(line[4:-1])):
        try:
            int(line[4+i])
        except ValueError:
	          #remove row
            if i == 0:
                exit(0)
	          #correcting value
            else:
                line[4+i] = line[4+i-1]

    # write back
    with sys.stdout:
        writer = csv.writer(sys.stdout)
        writer.writerow(line)
	


