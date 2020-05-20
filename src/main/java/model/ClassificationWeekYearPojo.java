package model;

import lombok.Data;
import java.io.Serializable;
import java.util.Objects;


@Data
public class ClassificationWeekYearPojo implements Serializable {

    double trendCoefficient;
    String state;
    String country;
    String continent;
    String weekYear;


    public ClassificationWeekYearPojo(double trendCoefficient, String state, String country, String continent, String weekYear) {
        this.trendCoefficient = trendCoefficient;
        this.state = state;
        this.country = country;
        this.continent = continent;
        this.weekYear = weekYear;
    }

    public ClassificationWeekYearPojo(double trendCoefficient, String state, String country, String continent) {
        this.trendCoefficient = trendCoefficient;
        this.state = state;
        this.country = country;
        this.continent = continent;
        //this.weekYear = "";
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassificationWeekYearPojo that = (ClassificationWeekYearPojo) o;
        return Double.compare(that.trendCoefficient, trendCoefficient) == 0 &&
                Objects.equals(state, that.state) &&
                country.equals(that.country) &&
                continent.equals(that.continent) &&
                Objects.equals(weekYear, that.weekYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trendCoefficient, state, country, continent, weekYear);
    }

    @Override
    public String toString() {
        return "trendCoefficient= '" + trendCoefficient+ '\'' +
                ", state= '" + state + '\'' +
                ", country= '" + country + '\'' +
                ", continent= '" + continent + '\'' +
                ", weekYear= '" + weekYear;
    }
}
