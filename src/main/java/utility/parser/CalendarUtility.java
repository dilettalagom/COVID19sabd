package utility.parser;

import org.threeten.extra.YearWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;

public class CalendarUtility {


    //from '2020-02-24T18:00:00' to '2020-W09'
    //il metodo YearWeek considera lun-lun
    public static String createKeyWeekYear(String date) {
        DateTimeFormatter FMT = new DateTimeFormatterBuilder()
                .appendOptional(DateTimeFormatter.ofPattern("M/d/yy"))
                .toFormatter();
        TemporalAccessor dt = FMT.parseBest(date, LocalDateTime::from, LocalDate::from);
        YearWeek yw = YearWeek.from(dt);
        //System.out.println(yw.toString());
        return yw.toString();
    }


    public static String createKeyYearMonth(String date) {
        DateTimeFormatter FMT = new DateTimeFormatterBuilder()
                .appendOptional(DateTimeFormatter.ofPattern("M/d/yy"))
                .toFormatter();
        TemporalAccessor dt = FMT.parseBest(date, LocalDateTime::from, LocalDate::from);
        YearMonth ym = YearMonth.from(dt);
        return ym.toString();
    }



}
