package model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.TreeMap;
import java.util.TreeSet;

@ToString
public class GlobalStatisticsPojo implements Comparable<DateTime>{

    @Getter @Setter
    String state;
    @Getter @Setter
    String country;
    @Getter @Setter
    String continent;
    @Getter @Setter
    TreeMap<DateTime,Integer> values;
    @Getter @Setter
    float trendCoefficient;


    public GlobalStatisticsPojo(String state, String nation, String continent, TreeMap<DateTime,Integer> values) {
        this.state = state;
        this.country = nation;
        this.continent = continent;
        this.values = values;
    }

    public GlobalStatisticsPojo(String state, String nation, String continent, TreeMap<DateTime,Integer> values,float trend) {
        this.state = state;
        this.country = nation;
        this.continent = continent;
        this.values = values;
        this.trendCoefficient = trend;
    }



    @Override
    public int compareTo(DateTime o) {
        return 0;
    }
}


//