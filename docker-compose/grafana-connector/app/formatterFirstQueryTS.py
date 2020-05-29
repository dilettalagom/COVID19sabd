import time
import datetime

class formatterFirstQueryTS():

    def __init__(self, result_set):
        self.dict_columns = []
        self.dict_rows = []
        self.dict_complete = {}

    def create_unix_timestamp(self, string):
        d = string.split("-")
        dt = datetime.datetime(int(d[0]), int(d[1]), int(d[2]),0,0,0)
        timestamp = time.mktime(dt.timetuple())
        return timestamp*1000

    def get_target_content(self, result_set):
        list_healed = []
        list_swabs = []

        for row in result_set:
            timestamp = self.create_unix_timestamp(row[1])
            list_healed.append([float(row[2]), timestamp])
            list_swabs.append([float(row[3]), timestamp])

        dict_healed = {"target": "mean_healed", "datapoints": sorted(list_healed, key = lambda x: x[1])}
        dict_swabs = {"target": "mean_swabs", "datapoints": sorted(list_swabs, key = lambda x: x[1])}

        total_list = [dict_healed.copy(), dict_swabs.copy()]
        return total_list