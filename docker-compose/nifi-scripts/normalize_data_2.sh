#!/usr/bin/env python

import sys
sys.path.append('/usr/local/lib/python2.7/dist-packages')
import pandas as pd
import numpy as np

df1 = pd.read_csv(sys.stdin)
geo_infos = df1.iloc[:, :4]
continent = df1['Continent']
subset = (df1.iloc[:, 4:-1]).astype(float)
header_subset = list(subset.columns.values)

sub = subset.to_numpy()
diff = np.diff(sub)
total_diff = np.concatenate((sub[:, :1], diff), axis=1)
diff_df = pd.DataFrame(data=(total_diff[0:, 0:]), columns=header_subset)
total_data_df = pd.concat([geo_infos, continent, diff_df], axis=1, join='inner')

total_data_df.to_csv(sys.stdout, index=False)