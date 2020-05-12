package model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.io.Serializable;


@ToString
public class NationalStatisticsPojo implements Serializable {

    private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Getter @Setter
    private DateTime date;
    @Getter @Setter
    private int numHealed;
    @Getter @Setter
    private int numTampons;


    public NationalStatisticsPojo(String dateTime, String healed, String tampons) {
        numHealed = Integer.parseInt(healed) ;
        date = formatDate(dateTime);
        numTampons = Integer.parseInt(tampons);
    }

    public static DateTime formatDate(String date){
        return DateTime.parse(date,formatter);
    }


}
