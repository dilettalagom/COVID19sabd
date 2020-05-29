import pycountry_convert as pc
import reverse_geocode
import csv

def main():
    with open("prova.csv", newline='') as input_row:
        df = list(csv.reader(input_row))
    continents = {'NA': 'America',
                  'SA': 'America',
                  'AS': 'Asia',
                  'OC': 'Oceania',
                  'AF': 'Africa',
                  'EU': 'Europa',
                  'AN': 'Antartide'}

    coordinates = (df[0][2], df[0][3]), (33.0, 65.0)
    country_code = reverse_geocode.search(coordinates)[0]["country_code"]
    continent_code = pc.country_alpha2_to_continent_code(country_code)
    df[0].append(continents[continent_code])
    print(df)
    with open("output.csv", "w") as output_row:
        writer = csv.writer(output_row)
        writer.writerows(df)