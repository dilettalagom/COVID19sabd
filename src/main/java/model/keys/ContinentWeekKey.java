package model.keys;

import lombok.Data;
import org.threeten.extra.YearWeek;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@Data
public class ContinentWeekKey implements Serializable, Comparable<ContinentWeekKey> {

    String continent;
    String weekYear;
    String dateStart;

    //private static SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");

    /*public ContinentWeekKey( String continent, YearWeek weekYear) {
        this.continent = continent;
        this.weekYear = weekYear.toString();
        this.dateStart = getDateByYearWeek(weekYear);
    }*/


    public ContinentWeekKey( String continent, String weekYear, String date) {
        this.continent = continent;
        this.weekYear = weekYear;
        this.dateStart = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContinentWeekKey that = (ContinentWeekKey) o;
        return continent.equals(that.continent) &&
                weekYear.equals(that.weekYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(continent, weekYear);
    }


    @Override
    public int compareTo(ContinentWeekKey o) {
        int c = this.continent.compareTo(o.continent);
        if(c  == 0){
           c = this.weekYear.compareTo(o.weekYear);
        }
        return c;
    }

    /*private static String getDateByYearWeek(YearWeek yw) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.WEEK_OF_YEAR,yw.getWeek());
        calendar.set(Calendar.YEAR, yw.getYear());
        Date d = calendar.getTime();
        return format1.format(d);
    }*/

}
