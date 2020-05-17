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
        return Objects.hash(trendCoefficient, country, state);
    }

    @Override
    public int compareTo(ClassificationKeyPojo o) {
        Double o1 = new Double(this.trendCoefficient);
        Double o2 = new Double(o.trendCoefficient);
        return o1.compareTo(o2);
    }

    @Override
    public String toString() {
        return "state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", trendCoefficient=" + trendCoefficient;
    }
}
