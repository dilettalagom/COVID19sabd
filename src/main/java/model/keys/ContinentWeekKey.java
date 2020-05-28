package model.keys;

import lombok.Data;
import java.io.Serializable;
import java.util.Objects;

@Data
public class ContinentWeekKey implements Serializable, Comparable<ContinentWeekKey> {

    String continent;
    String weekYear;
    String dateStart;


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

}
