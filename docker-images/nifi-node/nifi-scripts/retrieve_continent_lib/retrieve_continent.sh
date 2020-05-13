#!/usr/bin/env python
import sys
import pandas as pd
from retrieve_continent_lib import Continent as c

df=pd.read_csv(sys.stdin)

#retrieve continent
continent = c.get_Continent(df[2], df[3])
df.append(continent)
print(continent)

#send back
df.to_csv(sys.stdout, index=False)