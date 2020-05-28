#!/usr/bin/env python
import sys
sys.path.append('/usr/local/lib/python2.7/dist-packages')
import re, csv

# get input
line = sys.stdin.readline()

# send back header
if "Province/State,Country/Region,Lat,Long" in line:  # Province/State,Country/Region,Lat,Long,1/22/20,1/23/20,1/24/20,1/25/20,1/26/20,1/27/20
    sys.stdout.write(line)
    sys.stdout.flush()


else :  # ,"Korea, South",36.0,128.0,1,1,2,2,3,4,4,4,4,11,12,15,15,16,19,23,24,24,25,27,28,28,28,28
    # remove comma between quotes and split
    line = re.sub(r'(")([^",]+),([^"]+)(")', r'\2 \3', line).replace('\n', '').split(',')

    # expectedHeader: Province/State, Country/Region
    if line[0] != '' and re.compile("^[a-zA-Z*|'()&-*\s]*$").match(line[0]) is None:
        # if Province/State is not a string, change in null_string
        line[0] = ''
    if re.compile("^[a-zA-Z*|'()&-*\s]*$").match(line[1]) is None:
        # if Country/Region is not a string, remove completely the row
        exit(0)

    # expectedHeader: Lat,Long
    try:
        lat = float(line[2])
        lon = float(line[3])
        if not (-90.0 <= lat <= 90.0 and -180.0 <= lon <= 180.0):
            # check ranges of lat and long and, if wrong correct them
            #TODO: temp = (lat, long).get_coordinates;
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
	


