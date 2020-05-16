#!/usr/bin/env python

import sys
sys.path.append('/usr/local/lib/python2.7/dist-packages')
import pandas as pd
import numpy as np
import math

#read file from sys.stdin
df1 = pd.read_csv(sys.stdin)
geo_infos = df1.iloc[:, :4]
continent = df1['Continent']
subset = (df1.iloc[:, 4:-1]).astype(float)
header_subset = list(subset.columns.values)

sub = subset.to_numpy()

#find indexes of elements not in monotonic order
indexes_to_rectify = np.argwhere((sub[:, 1:] >= sub[:, :-1]) == False)

#substitute wrong element with mean of prev and next value
for i in indexes_to_rectify:
    #sub[i[0]][i[1]] = math.ceil((sub[i[0]][i[1] + 1] - sub[i[0]][i[1] - 1])/2)
    sub[i[0],i[1]] = sub[i[0],i[1] - 1]

#data normalization
#diff = np.diff(sub)

#rebuild csv
##total_diff = np.concatenate((sub[:, :1], diff), axis=1)
#diff_df = pd.DataFrame(data=(total_diff[0:, 0:]), columns=header_subset)
#total_data_df = pd.concat([geo_infos, continent, diff_df], axis=1, join='inner')

tot = pd.DataFrame(data=(sub[0:, 0:]), columns=header_subset)
total_data_df = pd.concat([geo_infos, continent, tot], axis=1, join='inner')

total_data_df.to_csv(sys.stdout, index=False)