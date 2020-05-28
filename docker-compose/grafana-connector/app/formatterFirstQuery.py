class formatterFirstQuery():

    def __init__(self, result_set):
        self.dict_columns = []
        self.dict_rows = []
        self.dict_complete = {}

    def create_column_dict(self):
        list_column = [{"text": "week_year", "type": "string"}.copy(),
                       {"text": "datestart_week", "type": "string"}.copy(),
                       {"text": "mean_healed", "type": "number"}.copy(),
                       {"text": "mean_swabs", "type": "number"}.copy()]
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
        return  dict