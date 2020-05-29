package model;

import lombok.Data;
import java.io.Serializable;
import java.util.Objects;


@Data
public class ClassificationMonthPojo implements Serializable, Comparable<ClassificationMonthPojo>{

    String monthYear;
    String state;
    String country;
    double lat;
    double lon;
    double trendMonth;
    int index;


    public ClassificationMonthPojo(String state, String country, double lat, double lon) {
        this.state = state;
        this.country = country;
        this.lat = lat;
        this.lon = lon;
    }


    public ClassificationMonthPojo(String state, String country,String monthYear,double lat, double lon) {
        this.monthYear = monthYear;
        this.state = state;
        this.country = country;
        this.lat = lat;
        this.lon = lon;
    }

    public ClassificationMonthPojo(String monthYear, String state, String country, double trendMonth, double lat, double lon) {
        this.monthYear = monthYear;
        this.state = state;
        this.country = country;
        this.trendMonth = trendMonth;
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassificationMonthPojo that = (ClassificationMonthPojo) o;
        return Objects.equals(monthYear, that.monthYear) &&
                state.equals(that.state) &&
                country.equals(that.country) &&
                Objects.equals(trendMonth, that.trendMonth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trendMonth, monthYear, state, country);
    }


    @Override
    public String toString() {
        return "trendMonth = " + trendMonth +
                ", state = " + state +
                ", country = " + country +
                ", monthYear = " + monthYear +
                ", lat = " + lat +
                ", lon = " + lon ;
    }

    @Override
    public int compareTo(ClassificationMonthPojo o) {
        int c = this.monthYear.compareTo(o.monthYear);
        if( c == 0){
            c = (new Double(this.trendMonth)).compareTo(new Double(o.trendMonth));
        }
        return c;
    }
}
