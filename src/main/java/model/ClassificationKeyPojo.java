package model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Objects;


@Data
public class ClassificationKeyPojo implements Serializable, Comparable<ClassificationKeyPojo>{

    String state;
    String country;
    double trendCoefficient;

    public ClassificationKeyPojo(String state, String country, double trendCoefficient) {
        this.state = state;
        this.country = country;
        this.trendCoefficient = trendCoefficient;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassificationKeyPojo that = (ClassificationKeyPojo) o;
        return Double.compare(that.trendCoefficient, trendCoefficient) == 0 &&
                Objects.equals(state, that.state) &&
                country.equals(that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trendCoefficient);
    }

    @Override
    public int compareTo(ClassificationKeyPojo o) {
        if(this.trendCoefficient==o.trendCoefficient)
            return 0;
        else if(this.trendCoefficient>o.trendCoefficient)
            return 1;
        else
            return -1;
    }

    @Override
    public String toString() {
        return "state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", trendCoefficient=" + trendCoefficient;
    }
}
