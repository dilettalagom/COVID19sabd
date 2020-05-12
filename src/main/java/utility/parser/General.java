package utility.parser;

import org.threeten.extra.YearWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;

public class General {


    //from '2020-02-24T18:00:00' to '2020-W09'
    public static String createKey(String date){
        DateTimeFormatter FMT = new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ISO_LOCAL_DATE)
                .optionalStart() //HERE WE INDICATE THAT THE TIME IS OPTIONAL
                .appendLiteral('T')
                .append(DateTimeFormatter.ISO_LOCAL_TIME)
                .toFormatter();
        TemporalAccessor dt = FMT.parseBest(date, LocalDateTime::from, LocalDate::from);
        YearWeek yw = YearWeek.from(dt);
        return yw.toString();

    }

}
