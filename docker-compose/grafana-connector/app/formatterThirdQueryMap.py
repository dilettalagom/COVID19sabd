import reverse_geocode
import time
import datetime

class formatterThirdQueryMap():

    def __init__(self, result_set):
        self.dict_columns = []


    # def get_target_content(self, result_set):
    #     total_list=[]
    #     for row in result_set:
    #         coordinates = (row[5],row[6]), (33.0, 65.0)
    #         country_code = reverse_geocode.search(coordinates)[0]["country_code"]
    #         dict_nation = {"key": country_code , "latitude": row[5], "longitude": row[6] , "name": row[2]}
    #         total_list.append(dict_nation.copy())
    #     return total_list

    # def get_target_content(self, result_set):
    #     total_list = []
    #     for row in result_set:
    #         year,month=row[0].split('-')
    #         dt = datetime.datetime(int(year),int(month),1,0,0,0)
    #         timestamp = int((time.mktime(dt.timetuple()))*1000)
    #         coordinates = (row[5],row[6]), (33.0, 65.0)
    #         country_code = reverse_geocode.search(coordinates)[0]["country_code"]
    #         dict_nation = {"target": row[1], "datapoints": [row[1], timestamp]}
    #         total_list.append(dict_nation.copy())
    #     return total_list