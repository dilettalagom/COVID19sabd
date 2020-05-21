package model;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class ClassificationMonthPojo implements Serializable, Comparable<ClassificationMonthPojo>{

    String monthYear;
    String state;
    String country;
    double trendMonth;


    public ClassificationMonthPojo(String state, String country) {
        this.state = state;
        this.country = country;
    }


    public ClassificationMonthPojo(String state, String country,String monthYear) {
        this.monthYear = monthYear;
        this.state = state;
        this.country = country;
    }

    public ClassificationMonthPojo(String monthYear, String state, String country, double trendMonth) {
        this.monthYear = monthYear;
        this.state = state;
        this.country = country;
        this.trendMonth = trendMonth;
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
        return "trendMonth = " + trendMonth + '\'' +
                ", state = '" + state + '\'' +
                ", country = '" + country + '\'' +
                ", monthYear = '" + monthYear;
    }

    @Override
    public int compareTo(ClassificationMonthPojo o) {
        int c = (new Double(this.trendMonth)).compareTo(new Double(o.trendMonth));
        if( c == 0){
            c = this.monthYear.compareTo(o.monthYear);
        }
        return c;
    }
}
