package model.keys;


import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.threeten.extra.YearWeek;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Objects;

@Data
public class NationalWeekKey implements Serializable, Comparable<NationalWeekKey>{


    private static org.joda.time.format.DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
    private String dateStart;
    private String WeekYear;


    public NationalWeekKey(String dateStart) {
        try {
            this.dateStart = changeFormatDate(dateStart);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.WeekYear = createKeyWeekYear(dateStart);
    }


    public static String createKeyWeekYear(String date){

        DateTimeFormatter FMT = new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ISO_LOCAL_DATE)
                .optionalStart()
                .appendLiteral('T')
                .append(DateTimeFormatter.ISO_LOCAL_TIME)
                .toFormatter();
        TemporalAccessor dt = FMT.parseBest(date, LocalDateTime::from, LocalDate::from);
        YearWeek yw = YearWeek.from(dt);
        return yw.toString();
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



    public static String changeFormatDate(String dateInput) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date d = sdf.parse(dateInput);
        sdf.applyPattern("yyyy-MM-dd");
        return sdf.format(d);
    }



}
