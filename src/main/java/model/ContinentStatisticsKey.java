package model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joda.time.DateTime;
import org.threeten.extra.YearWeek;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;


@Data @EqualsAndHashCode
public class ContinentStatisticsKey implements Serializable {

    String continent;
    String WeekYear;
    String dateStart;


    public ContinentStatisticsKey(String continent, String dateStart) {
        this.continent = continent;
        this.dateStart = dateStart;
        this.WeekYear = createKeyYearMonth(dateStart);

    }


    public static String createKeyYearMonth(String date){

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
