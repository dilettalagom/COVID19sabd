package utility.parser;

import model.NationalStatisticsPojo;

public class NationalStatisticsParser {

    public static NationalStatisticsPojo parseCSV(String csvLine) {

        NationalStatisticsPojo outlet = null;
        String[] csvValues = csvLine.split(",");

        if (csvValues.length != 3)
            return null;

//            1464894,1377987280,3.216,0,1,0,3

        outlet = new NationalStatisticsPojo(
                csvValues[0], //dateTime
                csvValues[1], //dimessi_guariti
                csvValues[2] //tamponi
        );

        return outlet;
    }
}
