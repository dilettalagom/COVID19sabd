package utility.parser;

import model.NationalStatisticsPojo;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class NationalStatisticsParser {

    private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");


    public static NationalStatisticsPojo parseCSV(String csvLine) {

        NationalStatisticsPojo outlet = null;
        String[] csvValues = csvLine.split(",");
        if (csvValues.length != 3)
            return null;

        outlet = new NationalStatisticsPojo(
                csvValues[0], //dateTime
                csvValues[1], //dimessi_guariti
                csvValues[2] //tamponi
        );

        return outlet;
    }
}
