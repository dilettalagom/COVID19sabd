class formatterSecondQueryTS():

    def __init__(self):
        self.dict_columns = []
        self.dict_values = []
        self.dict_complete = {}

    def create_column_dict(self):
        list_column = ["week_year", "datestart_week","continent",
                       "mean_confirmed_cases", "dev_std_confirmed_cases","min_confirmed_cases", "max_confirmed_cases"]
        return list_column

    def create_values_dict(self, result_set):
        list_values = []
        for row in result_set:
            list_values.append(row)
        return list_values

    def get_geohash(self, geohash):
        return {"geohash": geohash}#print pgh.encode(lat,lon)

    def create_series_dict(self, result_set):
        list_nations = []

        for row in result_set:
            dict_single_nation = {"name":"worldwide infected",
                                  "tags": self.get_geohash(result_set["geohash"]),#TODO: prendi geohash
                                  "columns":self.create_column_dict(),
                                  "values": self.create_values_dict(result_set)} #TODO: prendi la riga
            list_nations.append(dict_single_nation.copy())

        return {"series": list_nations}