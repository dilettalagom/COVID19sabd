package model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.TreeMap;

@ToString @Data
public class GlobalStatisticsPojo implements Serializable {

    String state;
    String country;
    String continent;
    TreeMap<DateTime,Integer> values;
    double trendCoefficient;


    public GlobalStatisticsPojo(String state, String nation, String continent, TreeMap<DateTime,Integer> values,double trend) {
        this.state = state;
        this.country = nation;
        this.continent = continent;
        this.values = values;
        this.trendCoefficient = trend;
    }

}


