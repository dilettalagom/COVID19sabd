package model;

import jdk.nashorn.internal.objects.annotations.Setter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.threeten.extra.YearWeek;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.Objects;

@Data
public class NationalWeekKey implements Serializable, Comparable<NationalWeekKey>{


    private static org.joda.time.format.DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
    private DateTime dateStart;
    private String WeekYear;


    public NationalWeekKey(String dateStart) {
        this.dateStart = formatDate(dateStart);
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

    public static DateTime formatDate(String date){
        return DateTime.parse(date,formatter);
    }



    @Override
    public int compareTo(NationalWeekKey o) {
        return (this.WeekYear).compareTo(o.WeekYear);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NationalWeekKey that = (NationalWeekKey) o;
        return Objects.equals(WeekYear, that.WeekYear);
    }

    @Override
    public String toString() {
        return "dateStart = " + dateStart +
                ", WeekYear = " + WeekYear;
    }

    @Override
    public int hashCode() {
        return Objects.hash(WeekYear);
    }
}
