package model;

import lombok.Data;
import lombok.ToString;
import utility.regression.TrendCalculator;
import java.io.Serializable;
import java.util.Arrays;


@ToString @Data
public class GlobalStatisticsPojo implements Serializable {

    String state;
    String country;
    String continent;
    String[] infectedDates;
    double[] infectedPerDay;
    double trendCoefficient;



    public GlobalStatisticsPojo(String state, String nation, String continent, String[] infectedDates, String[] dates) {
        this.state = state;
        this.country = nation;
        this.continent = continent;
        this.infectedDates = dates;
        this.infectedPerDay = this.convert_infectedValues(infectedDates);
        this.trendCoefficient = TrendCalculator.getInstance().getTrendCoefficient(this.infectedPerDay);
    }


    private double[] convert_infectedValues(String[] infectedString){
        return Arrays.stream(infectedString).mapToDouble(Double::parseDouble).toArray();
    }


}


