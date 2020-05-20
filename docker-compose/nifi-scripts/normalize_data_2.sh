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

# find indexes of elements not in monotonic order
indexes_to_rectify = np.argwhere((sub[:, 1:] >= sub[:, :-1]) == False)
#per sapere quante righe correggere eseguire  print(len(indexes_to_rectify))

# substitute wrong element with mean of prev and next value
for i in indexes_to_rectify:
    indexes_to_rectify_in_row = np.argwhere((sub[i[0], i[1]:] < sub[i[0], i[1]]) == True)

    # finestra di numero di valori che accettiamo siano errati (come se fosse avvenuto un bitflip) = 10
    #if len(indexes_to_rectify_in_row) <= 10:
    for j in indexes_to_rectify_in_row:
        sub[i[0], i[1] + j] = sub[i[0], i[1]]
    #else:
    #    print(len(indexes_to_rectify_in_row), all_line)

# data normalization
diff = np.diff(sub)

# rebuild csv
total_diff_np = np.concatenate((sub[:, :1], diff), axis=1) #attach first column
total_diff_df = pd.DataFrame(data=(total_diff_np[0:, 0:]), columns=header_subset)
total_data_df = pd.concat([geo_infos, continent, total_diff_df], axis=1, join='inner')

# write back
total_data_df.to_csv(sys.stdout, index=False)
