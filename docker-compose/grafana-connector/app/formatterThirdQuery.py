import reverse_geocode
import time
import datetime

class formatterThirdQuery():

    def __init__(self, result_set):
        self.dict_columns = []


    def create_column_dict(self):
        list_column = [{"text": "month_year", "type": "string"}.copy(),
                   {"text": "cluster_id", "type": "number"}.copy(),
                   {"text": "country", "type": "string"}.copy(),
                   {"text": "state", "type": "string"}.copy(),
                   {"text": "trend", "type": "number"}.copy(),
                   {"text": "lat", "type": "number"}.copy(),
                   {"text": "log", "type": "number"}.copy()]
        return list_column


    def create_row_dict(self, result_set):
        list_row = []
        for row in result_set:
            list_row.append(row)
        return list_row


    def create_dict_complete(self, result_set):
        a = self.create_column_dict()
        b = self.create_row_dict(result_set)
        dict = {"columns": a, "rows": b, "type": "table"}
        list_res = []
        list_res.append(dict.copy())
        return  list_res

