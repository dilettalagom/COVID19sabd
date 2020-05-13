package model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@ToString
public class GlobalStatisticsPojo {

    private static DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyyMM");

    @Getter @Setter
    String state;
    @Getter @Setter
    String country;
    @Getter @Setter
    String continent;
    @Getter @Setter
    private DateTime date;
    @Getter @Setter
    private int numHealed;


    public GlobalStatisticsPojo(String state, String nation, String continent, DateTime date, int numHealed) {
        this.state = state;
        this.country = nation;
        this.continent = continent;
        this.date = date;
        this.numHealed = numHealed;
    }

    public static DateTime formatDate(String date){
        return DateTime.parse(date,formatter);
    }


}


//