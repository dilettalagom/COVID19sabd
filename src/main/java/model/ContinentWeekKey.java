package model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Comparator;

@Data @EqualsAndHashCode
public class ContinentWeekKey implements Serializable, Comparable<ContinentWeekKey> {

    String continent;
    String weekYear;
    double trendCoefficient;


    public ContinentWeekKey( String continent, String weekYear,double trendCoefficient) {
        this.continent = continent;
        this.weekYear = weekYear;
        this.trendCoefficient = trendCoefficient;
    }


    @Override
    public int compareTo(ContinentWeekKey o) {
        int c = this.weekYear.compareTo(o.weekYear);
        if(c  == 0){
            c = this.continent.compareTo(o.continent);
        }
        return c;
    }
}
