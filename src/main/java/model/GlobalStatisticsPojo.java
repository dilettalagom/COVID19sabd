package model;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.IntStream;


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
        this.trendCoefficient = get_trendCoefficient(this.infectedPerDay);
    }


    private double[] convert_infectedValues(String[] infectedString){
        return Arrays.stream(infectedString).mapToDouble(Double::parseDouble).toArray();
    }


    private double get_trendCoefficient(double[] infected){

        double[] x = Arrays.stream(IntStream.range(0, infected.length).toArray()).asDoubleStream().toArray();
        SimpleRegression r = new SimpleRegression();
        for (int i = 0; i < infected.length -1; i++) {
            r.addData(x[i], infected[i]);
        }
        return r.getSlope();
    }


}


