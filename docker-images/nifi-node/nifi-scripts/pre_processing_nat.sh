#!/usr/bin/env python

import sys
sys.path.append('/usr/local/lib/python2.7/dist-packages')
import pandas as pd
df1 = pd.read_csv(sys.stdin)
df2 = df1[['dimessi_guariti', 'tamponi']].shift(periods=1, fill_value=0)
df1[['dimessi_guariti', 'tamponi']] = df1[['dimessi_guariti', 'tamponi']].subtract(df2[['dimessi_guariti', 'tamponi']])
df1.to_csv(sys.stdout,index=False)
